package csu.yulin.controller;

import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import csu.yulin.model.dto.*;
import csu.yulin.service.CharityDonationService;
import csu.yulin.utils.AssertUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 慈善捐款控制器，负责处理与 CharityDonation 智能合约相关的请求
 *
 * @author lp
 * @create 2025-03-26
 */
@Slf4j
@RestController
@RequestMapping("/blockchain")
@RequiredArgsConstructor
public class CharityDonationController {

    private final CharityDonationService charityDonationService;

    /**
     * 打印 TransactionResponse 的详细信息
     */
    private static void printTransactionResponse(TransactionResponse response) {
        // 打印 TransactionResponse 的详细信息
        TransactionReceipt receipt = response.getTransactionReceipt();
        log.info("TransactionResponse 详细信息: " +
                        "TransactionHash={}, BlockNumber={}, Status={}, ContractAddress={}, " +
                        "TransactionIndex={}, Root={}, BlockHash={}, From={}, To={}, " +
                        "GasUsed={}, RemainGas={}, LogsBloom={}, Input={}, Output={}, " +
                        "ReceiptMessages={}, Values={}, Events={}",
                receipt.getTransactionHash(),
                receipt.getBlockNumber(),
                receipt.getStatus(),
                response.getContractAddress(),
                receipt.getTransactionIndex(),
                receipt.getRoot(),
                receipt.getBlockHash(),
                receipt.getFrom(),
                receipt.getTo(),
                receipt.getGasUsed(),
                receipt.getRemainGas(),
                receipt.getLogsBloom(),
                receipt.getInput(),
                receipt.getOutput(),
                response.getReceiptMessages(),
                response.getValues(),
                response.getEvents());

        // 打印 Logs（如果存在）
        if (receipt.getLogs() != null && !receipt.getLogs().isEmpty()) {
            for (TransactionReceipt.Logs logEntry : receipt.getLogs()) {
                log.info("Log Entry: Address={}, Topics={}, Data={}",
                        logEntry.getAddress(),
                        logEntry.getTopics(),
                        logEntry.getData());
            }
        }

        // 打印 Events 解析结果（如果存在）
        Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
        if (eventResultMap != null) {
            log.info("Parsed Events: {}", eventResultMap);
        }

        // 打印 InputObject 和 InputABIObject（如果存在）
        if (response.getInputObject() != null) {
            log.info("InputObject: {}", response.getInputObject());
        }
        if (response.getInputABIObject() != null) {
            log.info("InputABIObject: {}", response.getInputABIObject());
        }
    }

    /**
     * 打印 CallResponse 的详细信息
     */
    private static void printCallResponse(CallResponse response) {
        log.info("CallResponse 详细信息: Values={}", response.getValues());

        // 打印 returnObject（如果存在）
        if (response.getReturnObject() != null) {
            log.info("ReturnObject: {}", response.getReturnObject());
        }

        // 打印 returnABIObject（如果存在）
        if (response.getReturnABIObject() != null) {
            log.info("ReturnABIObject: {}", response.getReturnABIObject());
        }
    }

    /**
     * 创建公益项目
     */
    @PostMapping("/project/create")
    public CommonResponse<TransactionResponse> createProject(@RequestBody CharityDonationCreateProjectInputDTO inputDTO) {
        // 调用智能合约创建项目
        TransactionResponse response = charityDonationService.createProject(inputDTO);

        // 打印 TransactionResponse 的基本信息
        printTransactionResponse(response);
        Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
        List<List<Object>> projectCreatedEvents = eventResultMap.get("ProjectCreated");
        List<Object> eventData = projectCreatedEvents.get(0);
        Integer projectId = (Integer) eventData.get(0);

        // 记录成功日志
        log.info("创建项目成功，项目ID: {}, 组织ID: {}, 目标金额: {}",
                projectId, inputDTO.getOrgId(), inputDTO.getTargetAmount());
        return CommonResponse.success("项目创建成功", response);
    }

    /**
     * 捐款
     */
    @PostMapping("/donate")
    public CommonResponse<DonationResponseVO> donate(@RequestBody CharityDonationDonateInputDTO inputDTO) {
        TransactionResponse response = charityDonationService.donate(inputDTO);
        printTransactionResponse(response);

        // 转换为 VO
        DonationResponseVO vo = DonationResponseVO.fromTransactionResponse(response, inputDTO);

        log.info("捐款成功，捐款ID: {}, 用户ID: {}, 项目ID: {}, 金额: {}",
                vo.getDonationId(), vo.getUserId(), vo.getProjectId(), vo.getAmount());
        return CommonResponse.success("捐款成功", vo);
    }

    /**
     * 上传凭证
     */
    @PostMapping("/voucher/upload")
    public CommonResponse<VoucherResponseVO> uploadVoucher(@RequestBody CharityDonationUploadVoucherInputDTO inputDTO) {
        try {
            // 调用区块链服务
            TransactionResponse response = charityDonationService.uploadVoucher(inputDTO);
            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应为空");
            AssertUtil.isTrue("0x0".equals(response.getTransactionReceipt().getStatus()),
                    ResultCode.INTERNAL_SERVER_ERROR, "区块链交易失败");

            // 打印交易详情
            printTransactionResponse(response);

            // 解析 VoucherUploaded 事件
            Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
            List<List<Object>> voucherUploadedEvents = eventResultMap.get("VoucherUploaded");
            AssertUtil.notEmpty(voucherUploadedEvents, ResultCode.INTERNAL_SERVER_ERROR, "未找到 VoucherUploaded 事件");
            List<Object> eventData = voucherUploadedEvents.get(0); // 假设每次交易只有一个事件

            // 提取事件数据
            BigInteger voucherId = BigInteger.valueOf(((Number) eventData.get(0)).longValue());
            BigInteger projectId = BigInteger.valueOf(((Number) eventData.get(1)).longValue());
            BigInteger orgId = BigInteger.valueOf(((Number) eventData.get(2)).longValue());
//            byte[] ipfsHashEvent = (byte[]) eventData.get(3); // 链上返回的 bytes32
            BigInteger timestamp = BigInteger.valueOf(((Number) eventData.get(4)).longValue());

            // 构造 VoucherResponseVO
            VoucherResponseVO voucherVO = new VoucherResponseVO();
            voucherVO.setVoucherId(voucherId);
            voucherVO.setProjectId(projectId);
            voucherVO.setOrgId(orgId);
//            voucherVO.setIpfsHash("0x" + Hex.encodeHexString(ipfsHashEvent)); // 转换为十六进制字符串
            voucherVO.setTimestamp(LocalDateTime.ofEpochSecond(timestamp.longValue(), 0, java.time.ZoneOffset.UTC));
            voucherVO.setTransactionHash(response.getTransactionReceipt().getTransactionHash());
            // 从 TransactionReceipt 获取 blockNumber（十六进制字符串）
            String blockNumberHex = response.getTransactionReceipt().getBlockNumber();
            // 去掉 "0x" 前缀并转换为 BigInteger
            BigInteger blockNumber = new BigInteger(blockNumberHex.substring(2), 16);
            // 设置到 VO
            voucherVO.setBlockNumber(blockNumber);

            log.info("上传凭证成功，凭证ID: {}, 项目ID: {}, 组织ID: {}, txHash: {}",
                    voucherId, inputDTO.getProjectId(), inputDTO.getOrgId(), voucherVO.getTransactionHash());
            return CommonResponse.success("凭证上传成功", voucherVO);
        } catch (Exception e) {
            log.error("上传凭证失败: projectId={}, orgId={}, ipfsHash={}, error={}",
                    inputDTO.getProjectId(), inputDTO.getOrgId(), Hex.encodeHexString(inputDTO.getIpfsHash()),
                    e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "凭证上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询项目详情
     */
    @PostMapping("/project/get")
    public CommonResponse<CallResponse> getProject(@RequestBody CharityDonationGetProjectInputDTO inputDTO) {
        CallResponse response = charityDonationService.getProject(inputDTO);
        printCallResponse(response);

        log.info("查询项目成功，项目ID: {}", inputDTO.getProjectId());
        return CommonResponse.success("查询项目成功", response);
    }

    /**
     * 查询项目捐款列表
     */
    @PostMapping("/project/donations")
    public CommonResponse<CallResponseVO> getProjectDonations(@RequestBody CharityDonationGetProjectDonationsInputDTO inputDTO) {
        CallResponse response = charityDonationService.getProjectDonations(inputDTO);
        printCallResponse(response);
        // 转换为自定义 VO，避免序列化 returnABIObject
        CallResponseVO vo = new CallResponseVO(response.getValues(), response.getReturnObject());

        log.info("查询项目捐款成功，项目ID: {}", inputDTO.getProjectId());

        return CommonResponse.success("查询项目捐款成功", vo);
    }

    /**
     * 查询捐款详情
     */
    @PostMapping("/donation/get")
    public CommonResponse<CallResponse> getDonation(@RequestBody CharityDonationGetDonationInputDTO inputDTO) {
        CallResponse response = charityDonationService.getDonation(inputDTO);
        printCallResponse(response);

        log.info("查询捐款详情成功，捐款ID: {}", inputDTO.getDonationId());
        return CommonResponse.success("查询捐款详情成功", response);
    }

    /**
     * 查询凭证详情
     */
    @PostMapping("/voucher/get")
    public CommonResponse<CallResponse> getVoucher(@RequestBody CharityDonationGetVoucherInputDTO inputDTO) {
        CallResponse response = charityDonationService.getVoucher(inputDTO);
        printCallResponse(response);

        log.info("查询凭证详情成功，凭证ID: {}", inputDTO.getVoucherId());
        return CommonResponse.success("查询凭证详情成功", response);
    }

    /**
     * 查询项目凭证列表
     */
    @PostMapping("/project/vouchers")
    public CommonResponse<CallResponseVO> getProjectVouchers(@RequestBody CharityDonationGetProjectVouchersInputDTO inputDTO) {
        CallResponse response = charityDonationService.getProjectVouchers(inputDTO);
        printCallResponse(response);

        // 转换为自定义 VO，避免序列化 returnABIObject
        CallResponseVO vo = new CallResponseVO(response.getValues(), response.getReturnObject());
        log.info("查询项目凭证成功，项目ID: {}", inputDTO.getProjectId());
        return CommonResponse.success("查询项目凭证成功", vo);
    }

    /**
     * 查询总项目数
     */
    @GetMapping("/project/count")
    public CommonResponse<CallResponse> getProjectCount() {
        CallResponse response = charityDonationService.projectCount();
        printCallResponse(response);

        log.info("查询项目总数成功");
        return CommonResponse.success("查询项目总数成功", response);
    }

    /**
     * 查询总捐款数
     */
    @GetMapping("/donation/count")
    public CommonResponse<CallResponse> getDonationCount() {
        CallResponse response = charityDonationService.donationCount();
        printCallResponse(response);

        log.info("查询捐款总数成功");
        return CommonResponse.success("查询捐款总数成功", response);
    }

    /**
     * 查询总凭证数
     */
    @GetMapping("/voucher/count")
    public CommonResponse<CallResponse> getVoucherCount() {
        CallResponse response = charityDonationService.voucherCount();
        printCallResponse(response);

        log.info("查询凭证总数成功");
        return CommonResponse.success("查询凭证总数成功", response);
    }

    /**
     * 查询特定项目的捐款记录
     */
    @PostMapping("/project/donations/detail")
    public CommonResponse<CallResponse> projectDonations(@RequestBody CharityDonationProjectDonationsInputDTO inputDTO) {
        CallResponse response = charityDonationService.projectDonations(inputDTO);
        printCallResponse(response);

        log.info("查询特定项目捐款记录成功，项目ID: {}, 索引: {}", inputDTO.getArg0(), inputDTO.getArg1());
        return CommonResponse.success("查询特定项目捐款记录成功", response);
    }

    /**
     * 查询特定项目的凭证记录
     */
    @PostMapping("/project/vouchers/detail")
    public CommonResponse<CallResponse> projectVouchers(@RequestBody CharityDonationProjectVouchersInputDTO inputDTO) {
        CallResponse response = charityDonationService.projectVouchers(inputDTO);
        printCallResponse(response);

        log.info("查询特定项目凭证记录成功，项目ID: {}, 索引: {}", inputDTO.getArg0(), inputDTO.getArg1());
        return CommonResponse.success("查询特定项目凭证记录成功", response);
    }

    // TODO: 原智能合约里有以下这方法吗

    /**
     * 查询捐款记录详情（通过索引）
     */
    @PostMapping("/donations")
    public CommonResponse<CallResponse> donations(@RequestBody CharityDonationDonationsInputDTO inputDTO) {
        CallResponse response = charityDonationService.donations(inputDTO);
        printCallResponse(response);

        log.info("查询捐款记录成功，索引: {}", inputDTO.getArg0());
        return CommonResponse.success("查询捐款记录成功", response);
    }

    /**
     * 查询项目记录详情（通过索引）
     */
    @PostMapping("/projects")
    public CommonResponse<CallResponse> projects(@RequestBody CharityDonationProjectsInputDTO inputDTO) {
        CallResponse response = charityDonationService.projects(inputDTO);
        printCallResponse(response);

        log.info("查询项目记录成功，索引: {}", inputDTO.getArg0());
        return CommonResponse.success("查询项目记录成功", response);
    }

    /**
     * 查询凭证记录详情（通过索引）
     */
    @PostMapping("/vouchers")
    public CommonResponse<CallResponse> vouchers(@RequestBody CharityDonationVouchersInputDTO inputDTO) {
        CallResponse response = charityDonationService.vouchers(inputDTO);
        printCallResponse(response);

        log.info("查询凭证记录成功，索引: {}", inputDTO.getArg0());
        return CommonResponse.success("查询凭证记录成功", response);
    }

    /**
     * 验证捐款交易: 根据 transactionHash 验证链上数据是否有效
     */
    @PostMapping("/verify")
    public CommonResponse<TransactionReceipt> verifyTransaction(@RequestBody VerifyDTO request) {
        try {
            // 获取交易回执
            TransactionReceipt receipt = charityDonationService.getClient()
                    .getTransactionReceipt(request.getTransactionHash())
                    .getTransactionReceipt()
                    .orElse(null);
            if (receipt == null || !"0x0".equals(receipt.getStatus())) {
                log.warn("Transaction not found or failed: transactionHash={}, status={}",
                        request.getTransactionHash(), receipt != null ? receipt.getStatus() : "null");
                return CommonResponse.error(ResultCode.NOT_FOUND, "链上未找到有效的捐款交易");
            }

            return CommonResponse.success("交易验证成功", receipt);
        } catch (Exception e) {
            log.error("Failed to verify transaction: transactionHash={}, error={}",
                    request.getTransactionHash(), e.getMessage());
            return CommonResponse.error(ResultCode.INTERNAL_SERVER_ERROR, "捐款交易验证失败: " + e.getMessage());
        }
    }

    @Data
    public static class VerifyDTO {
        private String transactionHash;
    }

    /**
     * 自定义 CallResponse VO，避免序列化问题
     */
    @Data
    private static class CallResponseVO {
        private String values;
        private List<Object> returnObject;

        public CallResponseVO(String values, List<Object> returnObject) {
            this.values = values;
            this.returnObject = returnObject;
        }
    }

    @Data
    public static class DonationResponseVO {
        /**
         * 区块链交易哈希
         */
        private String transactionHash;

        /**
         * 捐款ID
         */
        private BigInteger donationId;

        /**
         * 捐款用户ID
         */
        private BigInteger userId;

        /**
         * 关联项目ID
         */
        private BigInteger projectId;

        /**
         * 捐款金额
         */
        private BigDecimal amount;

        /**
         * 从 TransactionResponse 转换到 VO
         */
        public static DonationResponseVO fromTransactionResponse(TransactionResponse response,
                                                                 CharityDonationDonateInputDTO inputDTO) {
            DonationResponseVO vo = new DonationResponseVO();
            vo.setTransactionHash(response.getTransactionReceipt() != null ?
                    response.getTransactionReceipt().getTransactionHash() : null);

            // 从事件中提取 donationId
            Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
            List<List<Object>> donatedEvents = eventResultMap.get("Donated");
            List<Object> eventData = donatedEvents.get(0);
            vo.setDonationId(BigInteger.valueOf((Integer) eventData.get(0)));

            // 从输入 DTO 中提取 userId, projectId, amount
            vo.setUserId(inputDTO.getUserId());
            vo.setProjectId(inputDTO.getProjectId());
            vo.setAmount(new BigDecimal(inputDTO.getAmount()));

            return vo;
        }
    }

    /**
     * 凭证上传响应 VO
     */
    @Data
    public static class VoucherResponseVO {
        /**
         * 凭证ID，从链上事件中获取
         */
        private BigInteger voucherId;

        /**
         * 项目ID
         */
        private BigInteger projectId;

        /**
         * 组织ID
         */
        private BigInteger orgId;

        /**
         * IPFS 哈希（链上存储的 bytes32）
         */
        private String ipfsHash;

        /**
         * 上传时间戳，从链上事件中获取
         */
        private LocalDateTime timestamp;

        /**
         * 区块链交易哈希
         */
        private String transactionHash;

        /**
         * 区块号
         */
        private BigInteger blockNumber;
    }
}
