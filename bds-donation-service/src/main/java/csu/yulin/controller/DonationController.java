package csu.yulin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import csu.yulin.feign.BlockchainServiceFeignClient;
import csu.yulin.model.convert.DonationConverter;
import csu.yulin.model.dto.CharityDonationDonateInputDTO;
import csu.yulin.model.dto.DonationDTO;
import csu.yulin.model.entity.Donation;
import csu.yulin.model.vo.DonationResponseVO;
import csu.yulin.model.vo.DonationVO;
import csu.yulin.service.IDonationService;
import csu.yulin.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 捐款记录表 控制器
 *
 * @author lp
 * @create 2025-03-26
 */
@Slf4j
@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final IDonationService donationService;
    private final BlockchainServiceFeignClient blockchainServiceFeignClient;

    /**
     * 创建捐款: 记录用户对项目的捐款，触发链上 donate 方法，并将结果同步到数据库
     */
    @PostMapping("/donate")
    public CommonResponse<DonationDTO> donate(@RequestBody DonationDTO requestDTO) {
        try {
            // 构造链上请求 DTO
            CharityDonationDonateInputDTO inputDTO = new CharityDonationDonateInputDTO();
            inputDTO.setUserId(BigInteger.valueOf(requestDTO.getUserId()));
            inputDTO.setProjectId(BigInteger.valueOf(requestDTO.getProjectId()));
            inputDTO.setAmount(requestDTO.getAmount().toBigInteger());

            // 调用区块链服务
            CommonResponse<DonationResponseVO> response = blockchainServiceFeignClient.donate(inputDTO);
            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");
            AssertUtil.isTrue(response.getCode().equals(ResultCode.SUCCESS.getCode()), ResultCode.INTERNAL_SERVER_ERROR,
                    "区块链捐款失败: " + response.getMessage());
            DonationResponseVO responseVO = response.getData();
            AssertUtil.notNull(responseVO, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应数据为空");

            // 构造 Donation 实体
            Donation donation = new Donation();
            donation.setUserId(requestDTO.getUserId());
            donation.setProjectId(requestDTO.getProjectId());
            donation.setAmount(requestDTO.getAmount());
            donation.setTransactionHash(responseVO.getTransactionHash());
            donation.setDonationId(responseVO.getDonationId().longValue());
            donation.setTimestamp(LocalDateTime.now());

            // 保存到数据库
            boolean success = donationService.save(donation);
            AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "捐款记录保存失败");

            // 转换返回 DTO
            DonationDTO donationDTO = DonationConverter.toDTO(donation);

            log.info("Donation created successfully: donationId={}, userId={}, projectId={}, amount={}, txHash={}",
                    donationDTO.getDonationId(), donationDTO.getUserId(), donationDTO.getProjectId(),
                    donationDTO.getAmount(), donationDTO.getTransactionHash());

            return CommonResponse.success("捐款创建成功", donationDTO);
        } catch (Exception e) {
            log.error("Failed to create donation: userId={}, projectId={}, amount={}, error={}",
                    requestDTO.getUserId(), requestDTO.getProjectId(), requestDTO.getAmount(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款创建失败: " + e.getMessage());
        }
    }

    /**
     * 查询单笔捐款详情: 根据 donationId 查询链上或链下的捐款信息
     */
    @PostMapping("/get")
    public CommonResponse<DonationVO> getDonation(@RequestBody DonationDTO requestDTO) {
        try {
            // 参数校验
            AssertUtil.notNull(requestDTO, ResultCode.BAD_REQUEST, "请求参数不能为空");
            AssertUtil.notNull(requestDTO.getDonationId(), ResultCode.BAD_REQUEST, "捐款ID不能为空");

            // 查询数据库中的捐款记录
            Donation donation = donationService.getById(requestDTO.getDonationId());
            AssertUtil.notNull(donation, ResultCode.NOT_FOUND, "捐款记录不存在");

            // 转换为 DTO
            DonationVO donationVO = DonationConverter.toVO(donation);

            log.info("Donation retrieved successfully: donationId={}, userId={}, projectId={}, amount={}, txHash={}",
                    donationVO.getDonationId(), donationVO.getUserId(), donationVO.getProjectId(),
                    donationVO.getAmount(), donationVO.getTransactionHash());

            return CommonResponse.success("捐款查询成功", donationVO);
        } catch (Exception e) {
            log.error("Failed to retrieve donation: donationId={}, error={}",
                    requestDTO.getDonationId(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询项目的捐款列表: 根据 projectId 查询该项目的所有捐款记录
     */
    @PostMapping("/project/donations")
    public CommonResponse<List<DonationVO>> getProjectDonations(@RequestBody DonationDTO requestDTO) {
        try {
            // 参数校验
            AssertUtil.notNull(requestDTO, ResultCode.BAD_REQUEST, "请求参数不能为空");
            AssertUtil.notNull(requestDTO.getProjectId(), ResultCode.BAD_REQUEST, "项目ID不能为空");

            // 调用服务层查询捐款列表
            List<DonationVO> donationVOs = donationService.getProjectDonations(requestDTO.getProjectId());
            AssertUtil.notEmpty(donationVOs, ResultCode.NOT_FOUND, "该项目暂无捐款记录");

            log.info("Project donations retrieved successfully: projectId={}, count={}",
                    requestDTO.getProjectId(), donationVOs.size());

            return CommonResponse.success("项目捐款列表查询成功", donationVOs);
        } catch (Exception e) {
            log.error("Failed to retrieve project donations: projectId={}, error={}",
                    requestDTO.getProjectId(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "项目捐款列表查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户的捐款历史: 根据 userId 查询该用户的所有捐款记录
     */
    @PostMapping("/user/donations")
    public CommonResponse<List<DonationVO>> getUserDonations(@RequestBody DonationDTO requestDTO) {
        try {
            // 参数校验
            AssertUtil.notNull(requestDTO, ResultCode.BAD_REQUEST, "请求参数不能为空");
            AssertUtil.notNull(requestDTO.getUserId(), ResultCode.BAD_REQUEST, "用户ID不能为空");

            // 调用服务层查询捐款历史
            List<DonationVO> donationVOs = donationService.getUserDonations(requestDTO.getUserId());
            AssertUtil.notEmpty(donationVOs, ResultCode.NOT_FOUND, "该用户暂无捐款记录");

            log.info("User donations retrieved successfully: userId={}, count={}",
                    requestDTO.getUserId(), donationVOs.size());

            return CommonResponse.success("用户捐款历史查询成功", donationVOs);
        } catch (Exception e) {
            log.error("Failed to retrieve user donations: userId={}, error={}",
                    requestDTO.getUserId(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "用户捐款历史查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询捐款记录: 提供带分页的捐款记录查询，支持按项目、用户、时间范围筛选
     */
    @PostMapping("/list")
    public CommonResponse<Page<DonationVO>> listDonations(@RequestBody PageDTO requestDTO) {
        try {
            // 参数校验
            AssertUtil.notNull(requestDTO, ResultCode.BAD_REQUEST, "请求参数不能为空");
            AssertUtil.notNull(requestDTO.getPage(), ResultCode.BAD_REQUEST, "页码不能为空");
            AssertUtil.notNull(requestDTO.getSize(), ResultCode.BAD_REQUEST, "每页数量不能为空");
            AssertUtil.isTrue(requestDTO.getPage() > 0, ResultCode.BAD_REQUEST, "页码必须大于0");
            AssertUtil.isTrue(requestDTO.getSize() > 0, ResultCode.BAD_REQUEST, "每页数量必须大于0");

            // 调用服务层分页查询
            Page<DonationVO> donationPage = donationService.listDonations(requestDTO);
            AssertUtil.notEmpty(donationPage.getRecords(), ResultCode.NOT_FOUND, "暂无符合条件的捐款记录");

            log.info("Donations retrieved successfully: pageNum={}, pageSize={}, total={}",
                    requestDTO.getPage(), requestDTO.getSize(), donationPage.getTotal());

            return CommonResponse.success("捐款记录分页查询成功", donationPage);
        } catch (Exception e) {
            log.error("Failed to retrieve donations: pageNum={}, pageSize={}, error={}",
                    requestDTO.getPage(), requestDTO.getSize(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款记录查询失败: " + e.getMessage());
        }
    }

    /**
     * 统计捐款总额: 根据项目或用户条件统计捐款总额
     */
    @PostMapping("/stats/total")
    public CommonResponse<BigDecimal> getDonationTotal(@RequestBody DonationDTO requestDTO) {
        try {
            // 参数校验
            AssertUtil.notNull(requestDTO, ResultCode.BAD_REQUEST, "请求参数不能为空");
            AssertUtil.isTrue(requestDTO.getProjectId() != null || requestDTO.getUserId() != null,
                    ResultCode.BAD_REQUEST, "必须指定项目ID或用户ID");

            // 调用服务层统计总额
            BigDecimal totalAmount = donationService.getDonationTotal(requestDTO);
            AssertUtil.notNull(totalAmount, ResultCode.NOT_FOUND, "暂无符合条件的捐款记录");

            log.info("Donation total retrieved successfully: projectId={}, userId={}, totalAmount={}",
                    requestDTO.getProjectId(), requestDTO.getUserId(), totalAmount);

            return CommonResponse.success("捐款总额统计成功", totalAmount);
        } catch (Exception e) {
            log.error("Failed to retrieve donation total: projectId={}, userId={}, error={}",
                    requestDTO.getProjectId(), requestDTO.getUserId(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款总额统计失败: " + e.getMessage());
        }
    }
}