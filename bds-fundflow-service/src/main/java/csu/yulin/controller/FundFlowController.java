package csu.yulin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import csu.yulin.enums.StatusEnum;
import csu.yulin.exception.BusinessException;
import csu.yulin.feign.BlockchainServiceFeignClient;
import csu.yulin.feign.ProjectServiceFeignClient;
import csu.yulin.feign.UserServiceFeignClient;
import csu.yulin.feign.VoucherServiceFeignClient;
import csu.yulin.model.bo.CharityDonationRecordFundFlowInputBO;
import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.dto.VoucherDTO;
import csu.yulin.model.entity.AssistanceHistoryRecord;
import csu.yulin.model.entity.FundFlow;
import csu.yulin.model.entity.Project;
import csu.yulin.model.entity.User;
import csu.yulin.model.vo.FundFlowResponseVO;
import csu.yulin.service.IFundFlowService;
import csu.yulin.util.AssertUtil;
import csu.yulin.util.OSSUtil;
import csu.yulin.util.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资金流动控制器
 *
 * @author lp
 * @create 2025-04-25
 */
@Slf4j
@RestController
@RequestMapping("/fundflow")
@RequiredArgsConstructor
public class FundFlowController {

    private final OSSUtil ossUtil;

    private final IFundFlowService fundFlowService;

    private final VoucherServiceFeignClient voucherServiceFeignClient;

    private final BlockchainServiceFeignClient blockchainServiceFeignClient;

    private final ProjectServiceFeignClient projectServiceFeignClient;
    private final UserServiceFeignClient userServiceFeignClient;

    private final ObjectMapper objectMapper;

    /**
     * 创建资金流动记录（个体）
     */
    @PostMapping(value = "/create/individual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<FundFlow> createByIndividual(
            @RequestParam("projectId") Long projectId,
            @RequestParam("recipientId") Long recipientId,
            @RequestParam("amount") Long amount,
            @RequestParam("proofFile") MultipartFile file) {
        // 校验请求参数
        AssertUtil.notNull(projectId, "项目ID不能为空");
        AssertUtil.notNull(recipientId, "接收者ID不能为空");
        AssertUtil.notNull(amount, "流动金额不能为空");
        AssertUtil.isTrue(amount > 0, ResultCode.BAD_REQUEST, "流动金额必须大于0");
        AssertUtil.notNull(file, "证明文件不能为空");

        // 上传证明文件到阿里云 OSS
        String proofFileUrl;
        try {
            String originalFileName = file.getOriginalFilename();
            AssertUtil.hasText(originalFileName, "证明文件名称不能为空");
            String newFileName = null;
            if (originalFileName != null) {
                newFileName = generateUniqueFileName(originalFileName);
            }
            proofFileUrl = ossUtil.uploadFile(newFileName, file.getInputStream());
        } catch (IOException e) {
            log.error("证明文件上传失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "证明文件上传失败");
        }

        // 创建资金流动记录
        FundFlow fundFlow = new FundFlow();
        fundFlow.setFlowId((long) (Math.random() * 90000) + 10000);
        fundFlow.setProjectId(projectId);
        fundFlow.setRecipientId(recipientId);
        fundFlow.setAmount(new BigDecimal(amount));
        fundFlow.setReason("用户求助");
        fundFlow.setProofFileUrl(proofFileUrl);
        fundFlow.setTimestamp(LocalDateTime.now());
        fundFlow.setStatus(StatusEnum.PENDING.getCode());
        fundFlow.setIsDeleted(false);

        // 保存资金流动记录
        boolean success = fundFlowService.save(fundFlow);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "资金流动创建失败");

        // 日志记录
        log.info("个体用户资金流动创建成功: flowId={}, projectId={}, recipientId={}",
                fundFlow.getFlowId(), fundFlow.getProjectId(), fundFlow.getRecipientId());

        // 返回创建的资金流动记录
        return CommonResponse.success("资金流动创建成功", fundFlow);
    }

    /**
     * 创建资金流动记录（组织）
     */
    @PostMapping("/create/org")
    public CommonResponse<FundFlow> createByOrg(
            @RequestParam("projectId") Long projectId,
            @RequestParam("recipientId") Long recipientId,
            @RequestParam("amount") Long amount,
            @RequestParam("proofFile") MultipartFile file) throws IOException {
        // 校验请求参数
        AssertUtil.notNull(projectId, "项目ID不能为空");
        AssertUtil.notNull(recipientId, "接收者ID不能为空");
        AssertUtil.notNull(amount, "流动金额不能为空");
        AssertUtil.isTrue(amount > 0, ResultCode.BAD_REQUEST, "流动金额必须大于0");
        AssertUtil.notNull(file, "证明文件不能为空");

        // 创建凭证记录（通过 Feign 调用）
        CommonResponse<VoucherDTO> voucherResponse;
        try {
            voucherResponse = voucherServiceFeignClient.createVoucher(file, projectId, recipientId);
            AssertUtil.isTrue(voucherResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "凭证创建失败: " + voucherResponse.getMessage());
        } catch (Exception e) {
            log.error("调用凭证服务失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "凭证服务调用失败");
        }
        VoucherDTO voucher = voucherResponse.getData();
        AssertUtil.notNull(voucher, ResultCode.INTERNAL_SERVER_ERROR, "凭证数据为空");
        AssertUtil.notNull(voucher.getVoucherId(), ResultCode.INTERNAL_SERVER_ERROR, "凭证ID不能为空");

        // 创建资金流动记录
        FundFlow fundFlow = new FundFlow();
        fundFlow.setFlowId((long) (Math.random() * 90000) + 10000);
        fundFlow.setProjectId(projectId);
        fundFlow.setRecipientId(recipientId);
        fundFlow.setAmount(new BigDecimal(amount));
        fundFlow.setReason("组织挪用");
        fundFlow.setStatus(StatusEnum.PENDING.getCode());
        fundFlow.setTimestamp(LocalDateTime.now());
        fundFlow.setVoucherId(voucher.getVoucherId());
        fundFlow.setIsDeleted(false);

        // 保存资金流动记录
        boolean success = fundFlowService.save(fundFlow);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "资金流动创建失败");

        // 日志记录
        log.info("公益组织资金流动创建成功: flowId={}, projectId={}, recipientId={}",
                fundFlow.getFlowId(), fundFlow.getProjectId(), fundFlow.getRecipientId());

        // 返回创建的资金流动记录
        return CommonResponse.success("资金流动创建成功", fundFlow);
    }

    /**
     * 更新资金流动记录：管理员更新
     */
    @PostMapping("/update/admin")
    public CommonResponse<FundFlow> updateByAdmin(@RequestBody FundFlow request) throws JsonProcessingException {
        // 校验请求参数
        AssertUtil.notNull(request, "资金流动请求不能为空");
        AssertUtil.notNull(request.getFlowId(), "资金流动ID不能为空");
        AssertUtil.notNull(request.getStatus(), "审核意见不能为空");
        AssertUtil.notNull(request.getReviewerId(), "审核者ID不能为空");
        AssertUtil.notNull(request.getReviewNotes(), "审核备注不能为空");

        // 校验资金流动记录是否存在
        FundFlow existingFundFlow = fundFlowService.getById(request.getFlowId());
        AssertUtil.notNull(existingFundFlow, ResultCode.NOT_FOUND, "资金流动记录不存在");

        // 校验记录是否处于待审核状态
        AssertUtil.isTrue(StatusEnum.PENDING.equals(existingFundFlow.getStatus()),
                ResultCode.BAD_REQUEST, "只能更新待审核的资金流动记录");

        // 根据 status 处理更新逻辑
        if (StatusEnum.REJECTED.equals(request.getStatus())) {
            // 拒绝：只更新 status, review_notes, reviewer_id
            existingFundFlow.setStatus(StatusEnum.REJECTED.getCode());
            existingFundFlow.setReviewNotes(request.getReviewNotes());
            existingFundFlow.setReviewerId(request.getReviewerId());

            // 保存更新
            boolean success = fundFlowService.updateById(existingFundFlow);
            AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "资金流动更新失败");
        } else if (StatusEnum.APPROVED.equals(request.getStatus())) {
            // 批准：更新 status, review_notes, reviewer_id, transaction_hash，并更改主键
            CharityDonationRecordFundFlowInputBO bo = new CharityDonationRecordFundFlowInputBO();
            bo.setProjectId(BigInteger.valueOf(existingFundFlow.getProjectId()));
            bo.setRecipientId(BigInteger.valueOf(existingFundFlow.getRecipientId()));
            bo.setAmount(existingFundFlow.getAmount().toBigInteger());
            CommonResponse<FundFlowResponseVO> response = blockchainServiceFeignClient.recordFundFlow(bo);
            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");
            AssertUtil.isTrue(response.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "区块链记录失败: " + response.getMessage());
            FundFlowResponseVO responseVO = response.getData();
            AssertUtil.notNull(responseVO, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应数据为空");

            // 记录用户受助历史JSON
            AssistanceHistoryRecord assistanceHistoryRecord = new AssistanceHistoryRecord();
            assistanceHistoryRecord.setAssistanceId(existingFundFlow.getRecipientId());
            assistanceHistoryRecord.setProjectId(existingFundFlow.getProjectId());
            assistanceHistoryRecord.setAmount(existingFundFlow.getAmount());
            assistanceHistoryRecord.setTimestamp(LocalDateTime.now());
            assistanceHistoryRecord.setTxHash(responseVO.getTransactionHash());
            String assistanceHistoryRecordJson = objectMapper.writeValueAsString(assistanceHistoryRecord);

            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(existingFundFlow.getRecipientId());
            userDTO.setAssistanceHistory(assistanceHistoryRecordJson);
            CommonResponse<User> userResponse = userServiceFeignClient.updateIndividualUser(userDTO);
            AssertUtil.isTrue(userResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "更新用户失败: " + userResponse.getMessage());
            User user = userResponse.getData();
            AssertUtil.notNull(user, ResultCode.NOT_FOUND, "用户不存在");

            // Fetch the project details
            CommonResponse<Project> projectResponse = projectServiceFeignClient.getProjectById(existingFundFlow.getProjectId());
            AssertUtil.isTrue(projectResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "查询项目失败: " + projectResponse.getMessage());
            Project project = projectResponse.getData();
            AssertUtil.notNull(project, ResultCode.NOT_FOUND, "项目不存在");

            // Update project's raisedAmount
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(existingFundFlow.getProjectId());
            BigDecimal raisedAmount = project.getRaisedAmount();
            BigDecimal newRaisedAmount = raisedAmount.subtract(existingFundFlow.getAmount());
            AssertUtil.isTrue(newRaisedAmount.compareTo(BigDecimal.ZERO) >= 0, ResultCode.BAD_REQUEST,
                    "项目已筹金额不能为负");
            projectDTO.setRaisedAmount(newRaisedAmount);

            // Call Feign client to update project
            CommonResponse<String> stringCommonResponse = projectServiceFeignClient.updateProject(projectDTO);
            AssertUtil.isTrue(stringCommonResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "更新项目失败: " + stringCommonResponse.getMessage());

            FundFlow updatedFundFlow = new FundFlow();
            BeanUtils.copyProperties(existingFundFlow, updatedFundFlow);
            updatedFundFlow.setFlowId(responseVO.getFlowId().longValue());
            updatedFundFlow.setStatus(StatusEnum.APPROVED.getCode());
            updatedFundFlow.setReviewNotes(request.getReviewNotes());
            updatedFundFlow.setReviewerId(request.getReviewerId());
            updatedFundFlow.setTransactionHash(responseVO.getTransactionHash());
            AssertUtil.hasText(updatedFundFlow.getTransactionHash(), "交易哈希不能为空");

            fundFlowService.removeById(existingFundFlow.getFlowId());
            boolean success = fundFlowService.save(updatedFundFlow);
            AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "资金流动保存失败");
            existingFundFlow = updatedFundFlow;
        } else {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的审核状态");
        }

        // 日志记录
        log.info("资金流动更新成功: flowId={}, status={}, reviewerId={}",
                existingFundFlow.getFlowId(), existingFundFlow.getStatus(), existingFundFlow.getReviewerId());

        // 返回更新后的记录
        FundFlow updatedFundFlow = fundFlowService.getById(existingFundFlow.getFlowId());
        return CommonResponse.success("资金流动更新成功", updatedFundFlow);
    }

    /**
     * 更新资金流动记录：公益组织更新
     */
    @PostMapping("/update/org")
    public CommonResponse<FundFlow> updateByOrg(
            @RequestParam("flowId") Long flowId,
            @RequestParam("projectId") Long projectId,
            @RequestParam("proofFile") MultipartFile file) throws IOException {
        // 校验请求参数
        AssertUtil.notNull(flowId, "资金流动ID不能为空");
        AssertUtil.notNull(projectId, "项目ID不能为空");
        AssertUtil.notNull(file, "证明文件不能为空");

        // 校验资金流动记录是否存在
        FundFlow existingFundFlow = fundFlowService.getById(flowId);
        AssertUtil.notNull(existingFundFlow, ResultCode.NOT_FOUND, "资金流动记录不存在");

        // 校验记录是否处于待审核状态
        AssertUtil.isTrue(StatusEnum.PENDING.equals(existingFundFlow.getStatus()),
                ResultCode.BAD_REQUEST, "只能更新待审核的资金流动记录");

        // 校验项目是否存在并由当前公益组织创建
        CommonResponse<Project> projectResponse = projectServiceFeignClient.getProjectById(projectId);
        AssertUtil.isTrue(projectResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                "查询项目失败: " + projectResponse.getMessage());
        Project project = projectResponse.getData();
        AssertUtil.notNull(project, ResultCode.NOT_FOUND, "项目不存在");

        // 创建凭证记录（通过 Feign 调用）
        CommonResponse<VoucherDTO> voucherResponse;
        try {
            voucherResponse = voucherServiceFeignClient.createVoucher(file, projectId, project.getCreatorId());
            AssertUtil.isTrue(voucherResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "凭证创建失败: " + voucherResponse.getMessage());
            VoucherDTO voucher = voucherResponse.getData();
            AssertUtil.notNull(voucher, ResultCode.INTERNAL_SERVER_ERROR, "凭证数据为空");
            AssertUtil.notNull(voucher.getVoucherId(), ResultCode.INTERNAL_SERVER_ERROR, "凭证ID不能为空");

            // 更新资金流动记录
            existingFundFlow.setVoucherId(voucher.getVoucherId());
            // 保持待审核状态
            existingFundFlow.setStatus(StatusEnum.PENDING.getCode());
        } catch (Exception e) {
            log.error("调用凭证服务失败: flowId={}, error={}", flowId, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "凭证服务调用失败");
        }

        // 保存更新
        boolean success = fundFlowService.updateById(existingFundFlow);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "资金流动更新失败");

        // 日志记录
        log.info("公益组织资金流动更新成功: flowId={}, projectId={}, voucherId={}",
                existingFundFlow.getFlowId(), existingFundFlow.getProjectId(), existingFundFlow.getVoucherId());

        // 返回更新后的记录
        FundFlow updatedFundFlow = fundFlowService.getById(existingFundFlow.getFlowId());
        return CommonResponse.success("资金流动更新成功", updatedFundFlow);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return SnowflakeUtil.generateId() + fileExtension;
    }

    /**
     * 根据组织Id获取资金流动记录列表
     */
    @GetMapping("/{orgId}")
    public CommonResponse<List<FundFlow>> getFundFlowListByOrgId(@PathVariable("orgId") Long orgId) {
        // 校验请求参数
        AssertUtil.notNull(orgId, "组织ID不能为空");

        try {
            // 查询组织创建的所有项目
            CommonResponse<List<Project>> projectResponse = projectServiceFeignClient.getProjectsByCreatorId(orgId);
            AssertUtil.isTrue(projectResponse.getCode() == 200, ResultCode.INTERNAL_SERVER_ERROR,
                    "查询项目失败: " + projectResponse.getMessage());
            List<Project> projects = projectResponse.getData();
            if (projects == null || projects.isEmpty()) {
                log.info("组织ID={} 未创建任何项目", orgId);
                return CommonResponse.success("暂无相关资金流动记录", List.of());
            }

            // 提取项目 ID 列表
            List<Long> projectIds = projects.stream()
                    .map(Project::getProjectId)
                    .toList();

            // 查询与这些项目关联的资金流动记录，且 recipient_id 不等于 orgId
            LambdaQueryWrapper<FundFlow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(FundFlow::getProjectId, projectIds)
                    .ne(FundFlow::getRecipientId, orgId);
            List<FundFlow> fundFlows = fundFlowService.list(queryWrapper);
            if (fundFlows.isEmpty()) {
                log.info("组织ID={} 的项目未找到符合条件的资金流动记录", orgId);
                return CommonResponse.success("暂无相关资金流动记录", List.of());
            }

            // 日志记录
            log.info("成功查询组织ID={} 的资金流动记录，记录数={}", orgId, fundFlows.size());

            // 返回资金流动记录
            return CommonResponse.success("查询资金流动记录成功", fundFlows);
        } catch (Exception e) {
            log.error("查询组织ID={} 的资金流动记录失败: {}", orgId, e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询资金流动记录失败");
        }
    }
}