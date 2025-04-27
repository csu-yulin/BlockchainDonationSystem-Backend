package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import csu.yulin.alipay.AliPayConfig;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import csu.yulin.feign.BlockchainServiceFeignClient;
import csu.yulin.feign.ProjectServiceFeignClient;
import csu.yulin.feign.UserServiceFeignClient;
import csu.yulin.model.convert.DonationConverter;
import csu.yulin.model.dto.CharityDonationDonateInputDTO;
import csu.yulin.model.dto.DonationDTO;
import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.Donation;
import csu.yulin.model.entity.DonationHistoryRecord;
import csu.yulin.model.entity.DonationRecord;
import csu.yulin.model.vo.DonationResponseVO;
import csu.yulin.model.vo.DonationVO;
import csu.yulin.service.IDonationService;
import csu.yulin.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final UserServiceFeignClient userServiceFeignClient;
    private final ProjectServiceFeignClient projectServiceFeignClient;
    private final AliPayConfig aliPayConfig;
    private final ObjectMapper objectMapper;

    @GetMapping("/pay")
    public CommonResponse<String> pay(@RequestParam("userId") String userId,
                                      @RequestParam("projectId") String projectId,
                                      @RequestParam("amount") String amount) {
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), aliPayConfig.getFormat(), aliPayConfig.getCharset(),
                aliPayConfig.getAlipayPublicKey(), aliPayConfig.getSignType());

        // 2. 创建 Request并设置Request参数
        // 发送请求的 Request类
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        // 设置商户订单号
        model.setOutTradeNo(UUID.randomUUID().toString().replace("-", ""));
        // 设置订单总金额
        model.setTotalAmount(amount);
        // 设置订单标题
        model.setSubject("公益项目捐款");
        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");

        String token = StpUtil.getTokenValue();
        String passbackParams = URLEncoder.encode("userId=" + userId + "&projectId=" + projectId + "&token=" + token, StandardCharsets.UTF_8);
        model.setPassbackParams(passbackParams);

        request.setBizModel(model);

        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request, "POST").getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        log.info(form);

        return CommonResponse.success(form);
    }

    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        if ("TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
            log.info("========================================支付宝异步回调begin========================================");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8");
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                String passbackParams = URLDecoder.decode(request.getParameter("passback_params"), StandardCharsets.UTF_8);
                Map<String, String> paramMap = Arrays.stream(passbackParams.split("&"))
                        .map(kv -> kv.split("="))
                        .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

                String userId = paramMap.get("userId");
                String projectId = paramMap.get("projectId");
                String amount = params.get("total_amount");
                String token = paramMap.get("token");
                log.info("捐赠金额: {}", amount);
                log.info("userId: {}", userId);
                log.info("projectId: {}", projectId);
                log.info("token: {}", token);
                StpUtil.setTokenValue(token);

                try {
                    // 构造链上请求 DTO
                    CharityDonationDonateInputDTO inputDTO = new CharityDonationDonateInputDTO();
                    inputDTO.setUserId(BigInteger.valueOf(Long.parseLong(userId)));
                    inputDTO.setProjectId(BigInteger.valueOf(Long.parseLong(projectId)));
                    BigDecimal decimalAmount = new BigDecimal(amount);
                    BigInteger fenAmount = decimalAmount.multiply(BigDecimal.valueOf(100)).toBigInteger();
                    inputDTO.setAmount(fenAmount);

                    // 调用区块链服务
                    CommonResponse<DonationResponseVO> response = blockchainServiceFeignClient.donate(inputDTO);
                    AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");
                    AssertUtil.isTrue(response.getCode().equals(ResultCode.SUCCESS.getCode()), ResultCode.INTERNAL_SERVER_ERROR,
                            "区块链捐款失败: " + response.getMessage());
                    DonationResponseVO responseVO = response.getData();
                    AssertUtil.notNull(responseVO, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应数据为空");

                    // 构造 Donation 实体
                    Donation donation = new Donation();
                    donation.setUserId(Long.valueOf(userId));
                    donation.setProjectId(Long.valueOf(projectId));
                    donation.setAmount(decimalAmount);
                    donation.setTransactionHash(responseVO.getTransactionHash());
                    donation.setDonationId(responseVO.getDonationId().longValue());
                    donation.setTimestamp(LocalDateTime.now());

                    // 保存到数据库
                    boolean success = donationService.save(donation);
                    AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "捐款记录保存失败");

                    // 记录捐款历史JSON
                    DonationHistoryRecord donationHistoryRecord = new DonationHistoryRecord();
                    donationHistoryRecord.setDonationId(donation.getDonationId());
                    donationHistoryRecord.setProjectId(donation.getProjectId());
                    donationHistoryRecord.setAmount(donation.getAmount());
                    donationHistoryRecord.setTimestamp(donation.getTimestamp());
                    donationHistoryRecord.setTxHash(donation.getTransactionHash());
                    String donationHistoryRecordJson = objectMapper.writeValueAsString(donationHistoryRecord);

                    UserDTO userDTO = new UserDTO();
                    userDTO.setUserId(donation.getUserId());
                    userDTO.setDonationHistory(donationHistoryRecordJson);
                    userServiceFeignClient.updateIndividualUser(userDTO);

                    DonationRecord donationRecord = new DonationRecord();
                    donationRecord.setDonationId(donation.getDonationId());
                    donationRecord.setUserId(donation.getUserId());
                    donationRecord.setAmount(donation.getAmount());
                    donationRecord.setTimestamp(donation.getTimestamp());
                    donationRecord.setTxHash(donation.getTransactionHash());
                    String donationRecordJson = objectMapper.writeValueAsString(donationRecord);

                    ProjectDTO projectDTO = new ProjectDTO();
                    projectDTO.setProjectId(donation.getProjectId());
                    projectDTO.setDonationRecords(donationRecordJson);
                    projectDTO.setRaisedAmount(decimalAmount);
                    projectServiceFeignClient.updateProject(projectDTO);

                    // 转换返回 DTO
                    DonationDTO donationDTO = DonationConverter.toDTO(donation);

                    log.info("Donation created successfully: donationId={}, userId={}, projectId={}, amount={}, txHash={}",
                            donationDTO.getDonationId(), donationDTO.getUserId(), donationDTO.getProjectId(),
                            donationDTO.getAmount(), donationDTO.getTransactionHash());

                } catch (Exception e) {
                    log.error("Failed to create donation: userId={}, projectId={}, amount={}, error={}",
                            userId, projectId, amount, e.getMessage());
                    throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款创建失败: " + e.getMessage());
                }

                log.info("========================================支付宝异步回调end========================================");
            }
        }
        return "success";
    }

    /**
     * 创建捐款: 记录用户对项目的捐款，触发链上 donate 方法，并将结果同步到数据库
     */
    @PostMapping("/donate")
    public CommonResponse<DonationDTO> donate(@RequestBody DonationDTO requestDTO) {
//        try {
//            // 构造链上请求 DTO
//            CharityDonationDonateInputDTO inputDTO = new CharityDonationDonateInputDTO();
//            inputDTO.setUserId(BigInteger.valueOf(requestDTO.getUserId()));
//            inputDTO.setProjectId(BigInteger.valueOf(requestDTO.getProjectId()));
//            inputDTO.setAmount(requestDTO.getAmount().toBigInteger());
//
//            // 调用区块链服务
//            CommonResponse<DonationResponseVO> response = blockchainServiceFeignClient.donate(inputDTO);
//            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");
//            AssertUtil.isTrue(response.getCode().equals(ResultCode.SUCCESS.getCode()), ResultCode.INTERNAL_SERVER_ERROR,
//                    "区块链捐款失败: " + response.getMessage());
//            DonationResponseVO responseVO = response.getData();
//            AssertUtil.notNull(responseVO, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应数据为空");
//
//            // 构造 Donation 实体
//            Donation donation = new Donation();
//            donation.setUserId(requestDTO.getUserId());
//            donation.setProjectId(requestDTO.getProjectId());
//            donation.setAmount(requestDTO.getAmount());
//            donation.setTransactionHash(responseVO.getTransactionHash());
//            donation.setDonationId(responseVO.getDonationId().longValue());
//            donation.setTimestamp(LocalDateTime.now());
//
//            // 保存到数据库
//            boolean success = donationService.save(donation);
//            AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "捐款记录保存失败");
//
//            // 转换返回 DTO
//            DonationDTO donationDTO = DonationConverter.toDTO(donation);
//
//            log.info("Donation created successfully: donationId={}, userId={}, projectId={}, amount={}, txHash={}",
//                    donationDTO.getDonationId(), donationDTO.getUserId(), donationDTO.getProjectId(),
//                    donationDTO.getAmount(), donationDTO.getTransactionHash());
//
//            return CommonResponse.success("捐款创建成功", donationDTO);
//        } catch (Exception e) {
//            log.error("Failed to create donation: userId={}, projectId={}, amount={}, error={}",
//                    requestDTO.getUserId(), requestDTO.getProjectId(), requestDTO.getAmount(), e.getMessage());
//            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "捐款创建失败: " + e.getMessage());
//        }

        return null;
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

    /**
     * 统计捐款总额
     */
    @GetMapping("/amount")
    public CommonResponse<BigDecimal> getDonationAmount() {
        BigDecimal totalAmount = donationService.getDonationTotal(new DonationDTO());
        if (totalAmount != null) {
            return CommonResponse.success("捐款总额查询成功", totalAmount);
        } else {
            return CommonResponse.error(ResultCode.NOT_FOUND, "暂无捐款记录");
        }
    }
}