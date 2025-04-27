package csu.yulin.controller;

import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import csu.yulin.model.bo.*;
import csu.yulin.model.entity.CharityDonation;
import csu.yulin.service.CharityDonationService;
import csu.yulin.utils.AssertUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
    public CommonResponse<Map<String, Object>> createProject(@RequestBody CharityDonationCreateProjectInputBO inputBO) {
        // 调用智能合约创建项目
        TransactionResponse response = charityDonationService.createProject(inputBO);

        // 打印 TransactionResponse 的基本信息
        printTransactionResponse(response);
        Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
        List<List<Object>> projectCreatedEvents = eventResultMap.get("ProjectCreated");
        List<Object> eventData = projectCreatedEvents.get(0);
        Integer projectId = (Integer) eventData.get(0);

        // 记录成功日志
        log.info("创建项目成功，项目ID: {}, 组织ID: {}, 目标金额: {}",
                projectId, inputBO.getOrgId(), inputBO.getTargetAmount());
        Map<String, Object> responseData = Map.of(
                "projectId", projectId,
                "transactionHash", response.getTransactionReceipt().getTransactionHash()
        );

        return CommonResponse.success("项目创建成功", responseData);
    }

    /**
     * 捐款
     */
    @PostMapping("/donate")
    public CommonResponse<DonationResponseVO> donate(@RequestBody CharityDonationDonateInputBO inputBO) {
        TransactionResponse response = charityDonationService.donate(inputBO);
        printTransactionResponse(response);

        // 转换为 VO
        DonationResponseVO vo = DonationResponseVO.fromTransactionResponse(response, inputBO);

        log.info("捐款成功，捐款ID: {}, 用户ID: {}, 项目ID: {}, 金额: {}",
                vo.getDonationId(), vo.getUserId(), vo.getProjectId(), vo.getAmount());
        return CommonResponse.success("捐款成功", vo);
    }

    /**
     * 上传凭证
     */
    @PostMapping("/voucher/upload")
    public CommonResponse<VoucherResponseVO> uploadVoucher(@RequestBody CharityDonationUploadVoucherInputBO inputBO) {
        try {
            // 调用区块链服务
            TransactionResponse response = charityDonationService.uploadVoucher(inputBO);
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
                    voucherId, inputBO.getProjectId(), inputBO.getOrgId(), voucherVO.getTransactionHash());
            return CommonResponse.success("凭证上传成功", voucherVO);
        } catch (Exception e) {
            log.error("上传凭证失败: projectId={}, orgId={}, ipfsHash={}, error={}",
                    inputBO.getProjectId(), inputBO.getOrgId(), Hex.encodeHexString(inputBO.getIpfsHash()),
                    e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "凭证上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询项目详情
     */
    @PostMapping("/project/get")
    public CommonResponse<CallResponse> getProject(@RequestBody CharityDonationGetProjectInputBO inputBO) {
        CallResponse response = charityDonationService.getProject(inputBO);
        printCallResponse(response);

        log.info("查询项目成功，项目ID: {}", inputBO.getProjectId());
        return CommonResponse.success("查询项目成功", response);
    }

    /**
     * 查询项目捐款列表
     */
    @PostMapping("/project/donations")
    public CommonResponse<CallResponseVO> getProjectDonations(@RequestBody CharityDonationGetProjectDonationsInputBO inputBO) {
        CallResponse response = charityDonationService.getProjectDonations(inputBO);
        printCallResponse(response);
        // 转换为自定义 VO，避免序列化 returnABIObject
        CallResponseVO vo = new CallResponseVO(response.getValues(), response.getReturnObject());

        log.info("查询项目捐款成功，项目ID: {}", inputBO.getProjectId());

        return CommonResponse.success("查询项目捐款成功", vo);
    }

    /**
     * 查询捐款详情
     */
    @PostMapping("/donation/get")
    public CommonResponse<CallResponse> getDonation(@RequestBody CharityDonationGetDonationInputBO inputBO) {
        CallResponse response = charityDonationService.getDonation(inputBO);
        printCallResponse(response);

        log.info("查询捐款详情成功，捐款ID: {}", inputBO.getDonationId());
        return CommonResponse.success("查询捐款详情成功", response);
    }

    /**
     * 查询凭证详情
     */
    @PostMapping("/voucher/get")
    public CommonResponse<CallResponse> getVoucher(@RequestBody CharityDonationGetVoucherInputBO inputBO) {
        CallResponse response = charityDonationService.getVoucher(inputBO);
        printCallResponse(response);

        log.info("查询凭证详情成功，凭证ID: {}", inputBO.getVoucherId());
        return CommonResponse.success("查询凭证详情成功", response);
    }

    /**
     * 查询项目凭证列表
     */
    @PostMapping("/project/vouchers")
    public CommonResponse<CallResponseVO> getProjectVouchers(@RequestBody CharityDonationGetProjectVouchersInputBO inputBO) {
        CallResponse response = charityDonationService.getProjectVouchers(inputBO);
        printCallResponse(response);

        // 转换为自定义 VO，避免序列化 returnABIObject
        CallResponseVO vo = new CallResponseVO(response.getValues(), response.getReturnObject());
        log.info("查询项目凭证成功，项目ID: {}", inputBO.getProjectId());
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
    public CommonResponse<CallResponse> projectDonations(@RequestBody CharityDonationProjectDonationsInputBO inputBO) {
        CallResponse response = charityDonationService.projectDonations(inputBO);
        printCallResponse(response);

        log.info("查询特定项目捐款记录成功，项目ID: {}, 索引: {}", inputBO.getArg0(), inputBO.getArg1());
        return CommonResponse.success("查询特定项目捐款记录成功", response);
    }

    /**
     * 查询特定项目的凭证记录
     */
    @PostMapping("/project/vouchers/detail")
    public CommonResponse<CallResponse> projectVouchers(@RequestBody CharityDonationProjectVouchersInputBO inputBO) {
        CallResponse response = charityDonationService.projectVouchers(inputBO);
        printCallResponse(response);

        log.info("查询特定项目凭证记录成功，项目ID: {}, 索引: {}", inputBO.getArg0(), inputBO.getArg1());
        return CommonResponse.success("查询特定项目凭证记录成功", response);
    }

    // TODO: 原智能合约里有以下这方法吗

    /**
     * 查询捐款记录详情（通过索引）
     */
    @PostMapping("/donations")
    public CommonResponse<CallResponse> donations(@RequestBody CharityDonationDonationsInputBO inputBO) {
        CallResponse response = charityDonationService.donations(inputBO);
        printCallResponse(response);

        log.info("查询捐款记录成功，索引: {}", inputBO.getArg0());
        return CommonResponse.success("查询捐款记录成功", response);
    }

    /**
     * 查询项目记录详情（通过索引）
     */
    @PostMapping("/projects")
    public CommonResponse<CallResponse> projects(@RequestBody CharityDonationProjectsInputBO inputBO) {
        CallResponse response = charityDonationService.projects(inputBO);
        printCallResponse(response);

        log.info("查询项目记录成功，索引: {}", inputBO.getArg0());
        return CommonResponse.success("查询项目记录成功", response);
    }

    /**
     * 查询凭证记录详情（通过索引）
     */
    @PostMapping("/vouchers")
    public CommonResponse<CallResponse> vouchers(@RequestBody CharityDonationVouchersInputBO inputBO) {
        CallResponse response = charityDonationService.vouchers(inputBO);
        printCallResponse(response);

        log.info("查询凭证记录成功，索引: {}", inputBO.getArg0());
        return CommonResponse.success("查询凭证记录成功", response);
    }

    /**
     * 验证交易: 根据 transactionHash 验证链上数据是否有效，并返回详细的交易信息
     */
    @PostMapping("/verify")
    public CommonResponse<VerifyTransactionResponseVO> verifyTransaction(@RequestBody VerifyBO request) {
        try {
            // 获取交易回执
            TransactionReceipt receipt = charityDonationService.getClient()
                    .getTransactionReceipt(request.getTransactionHash())
                    .getTransactionReceipt()
                    .orElse(null);
            if (receipt == null || !"0x0".equals(receipt.getStatus())) {
                log.warn("Transaction not found or failed: transactionHash={}, status={}",
                        request.getTransactionHash(), receipt != null ? receipt.getStatus() : "null");
                return CommonResponse.error(ResultCode.NOT_FOUND, "链上未找到有效的交易");
            }

            // 创建 CharityDonation 实例以解析事件
            CharityDonation charityContract = CharityDonation.load(
                    charityDonationService.getAddress(),
                    charityDonationService.getClient(),
                    charityDonationService.getClient().getCryptoSuite().getCryptoKeyPair()
            );

            // 解析事件
            Map<String, List<List<Object>>> eventResultMap = new HashMap<>();

            // ProjectCreated 事件
            List<CharityDonation.ProjectCreatedEventResponse> projectCreatedEvents = charityContract.getProjectCreatedEvents(receipt);
            if (!projectCreatedEvents.isEmpty()) {
                List<List<Object>> eventDataList = new ArrayList<>();
                for (CharityDonation.ProjectCreatedEventResponse event : projectCreatedEvents) {
                    eventDataList.add(Arrays.asList(
                            event.projectId,
                            event.orgId,
                            event.targetAmount
                    ));
                }
                eventResultMap.put("ProjectCreated", eventDataList);
            }

            // Donated 事件
            List<CharityDonation.DonatedEventResponse> donatedEvents = charityContract.getDonatedEvents(receipt);
            if (!donatedEvents.isEmpty()) {
                List<List<Object>> eventDataList = new ArrayList<>();
                for (CharityDonation.DonatedEventResponse event : donatedEvents) {
                    eventDataList.add(Arrays.asList(
                            event.donationId,
                            event.userId,
                            event.projectId,
                            event.amount,
                            event.timestamp
                    ));
                }
                eventResultMap.put("Donated", eventDataList);
            }

            // VoucherUploaded 事件
            List<CharityDonation.VoucherUploadedEventResponse> voucherUploadedEvents = charityContract.getVoucherUploadedEvents(receipt);
            if (!voucherUploadedEvents.isEmpty()) {
                List<List<Object>> eventDataList = new ArrayList<>();
                for (CharityDonation.VoucherUploadedEventResponse event : voucherUploadedEvents) {
                    eventDataList.add(Arrays.asList(
                            event.voucherId,
                            event.projectId,
                            event.orgId,
                            event.ipfsHash,
                            event.timestamp
                    ));
                }
                eventResultMap.put("VoucherUploaded", eventDataList);
            }

            // FundFlowRecorded 事件
            List<CharityDonation.FundFlowRecordedEventResponse> fundFlowRecordedEvents = charityContract.getFundFlowRecordedEvents(receipt);
            if (!fundFlowRecordedEvents.isEmpty()) {
                List<List<Object>> eventDataList = new ArrayList<>();
                for (CharityDonation.FundFlowRecordedEventResponse event : fundFlowRecordedEvents) {
                    eventDataList.add(Arrays.asList(
                            event.flowId,
                            event.projectId,
                            event.recipientId,
                            event.amount,
                            event.timestamp
                    ));
                }
                eventResultMap.put("FundFlowRecorded", eventDataList);
            }

            // 获取交易输入数据
            JsonTransactionResponse transaction = charityDonationService.getClient()
                    .getTransactionByHash(receipt.getTransactionHash())
                    .getTransaction()
                    .orElse(null);
            String inputData = transaction != null ? transaction.getInput() : null;
            String inputFunction = null;
            List<Object> inputParameters = null;
            if (inputData != null && inputData.length() > 10) {
                String methodSignature = inputData.substring(2, 10);
                switch (methodSignature) {
                    case "a4f318c8": // createProject
                        inputFunction = CharityDonation.FUNC_CREATEPROJECT;
                        inputParameters = Arrays.asList(
                                charityContract.getCreateProjectInput(receipt).getValue1(),
                                charityContract.getCreateProjectInput(receipt).getValue2()
                        );
                        break;
                    case "c22a4ce4": // donate
                        inputFunction = CharityDonation.FUNC_DONATE;
                        inputParameters = Arrays.asList(
                                charityContract.getDonateInput(receipt).getValue1(),
                                charityContract.getDonateInput(receipt).getValue2(),
                                charityContract.getDonateInput(receipt).getValue3()
                        );
                        break;
                    case "ebfa5b88": // uploadVoucher
                        inputFunction = CharityDonation.FUNC_UPLOADVOUCHER;
                        inputParameters = Arrays.asList(
                                charityContract.getUploadVoucherInput(receipt).getValue1(),
                                charityContract.getUploadVoucherInput(receipt).getValue2(),
                                charityContract.getUploadVoucherInput(receipt).getValue3()
                        );
                        break;
                    case "0a30ae21": // recordFundFlow
                        inputFunction = CharityDonation.FUNC_RECORDFUNDFLOW;
                        inputParameters = Arrays.asList(
                                charityContract.getRecordFundFlowInput(receipt).getValue1(),
                                charityContract.getRecordFundFlowInput(receipt).getValue2(),
                                charityContract.getRecordFundFlowInput(receipt).getValue3()
                        );
                        break;
                    default:
                        inputFunction = "unknown";
                        try {
                            List<Type> decoded = FunctionReturnDecoder.decode(
                                    inputData.substring(10),
                                    new Function("unknown", Collections.emptyList(), Collections.emptyList()).getOutputParameters()
                            );
                            inputParameters = decoded.stream().map(Type::getValue).collect(Collectors.toList());
                        } catch (Exception e) {
                            log.warn("Failed to decode input data for transactionHash={}: {}", receipt.getTransactionHash(), e.getMessage());
                        }
                }
            }

            // 获取交易输出数据（仅对 view 函数有效）
            List<Object> outputData = null;
            if (receipt.getOutput() != null && !receipt.getOutput().equals("0x")) {
                try {
                    Function function = new Function(
                            inputFunction != null ? inputFunction : "unknown",
                            Collections.emptyList(),
                            Arrays.asList(new TypeReference<Uint256>() {
                            }, new TypeReference<Uint256>() {
                            }, new TypeReference<Uint256>() {
                            }, new TypeReference<Uint256>() {
                            })
                    );
                    List<Type> decoded = FunctionReturnDecoder.decode(receipt.getOutput(), function.getOutputParameters());
                    outputData = decoded.stream().map(Type::getValue).collect(Collectors.toList());
                } catch (Exception e) {
                    log.warn("Failed to decode output data for transactionHash={}: {}", receipt.getTransactionHash(), e.getMessage());
                }
            }

            // 构造响应 VO
            VerifyTransactionResponseVO vo = new VerifyTransactionResponseVO();
            vo.setTransactionHash(receipt.getTransactionHash());
            String blockNumberHex = receipt.getBlockNumber();
            vo.setBlockNumber(new BigInteger(blockNumberHex.substring(2), 16));
            vo.setStatus(receipt.getStatus());
            vo.setFrom(receipt.getFrom());
            vo.setTo(receipt.getTo());
            vo.setGasUsed(new BigInteger(receipt.getGasUsed().substring(2), 16));
            vo.setBlockHash(receipt.getBlockHash());
            vo.setInputFunction(inputFunction);
            vo.setInputParameters(inputParameters);
            vo.setOutputData(outputData);

            // 解析事件数据
            List<EventData> events = new ArrayList<>();
            for (Map.Entry<String, List<List<Object>>> entry : eventResultMap.entrySet()) {
                String eventName = entry.getKey();
                List<List<Object>> eventDataList = entry.getValue();
                for (List<Object> eventData : eventDataList) {
                    EventData event = new EventData();
                    event.setEventName(eventName);
                    switch (eventName) {
                        case "ProjectCreated":
                            event.setParameters(Map.of(
                                    "projectId", eventData.get(0),
                                    "orgId", eventData.get(1),
                                    "targetAmount", new BigDecimal((BigInteger) eventData.get(2))
                            ));
                            break;
                        case "Donated":
                            BigInteger timestamp = (BigInteger) eventData.get(4);
                            // 验证时间戳是否合理（1970-01-01 到 2100-01-01）
                            long minTimestamp = 0; // 1970-01-01
                            long maxTimestamp = 4102444800L; // 2100-01-01
                            long timestampValue = timestamp.longValue();
                            if (timestampValue < minTimestamp || timestampValue > maxTimestamp) {
                                log.warn("Invalid timestamp for Donated event: transactionHash={}, timestamp={}",
                                        request.getTransactionHash(), timestamp);
                                // 使用当前时间作为回退
                                timestampValue = System.currentTimeMillis() / 1000;
                            }
                            event.setParameters(Map.of(
                                    "donationId", eventData.get(0),
                                    "userId", eventData.get(1),
                                    "projectId", eventData.get(2),
                                    "amount", new BigDecimal((BigInteger) eventData.get(3)),
                                    "timestamp", LocalDateTime.ofEpochSecond(timestampValue, 0, ZoneOffset.UTC)
                                            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            ));
                            break;
                        case "VoucherUploaded":
                            event.setParameters(Map.of(
                                    "voucherId", eventData.get(0),
                                    "projectId", eventData.get(1),
                                    "orgId", eventData.get(2),
                                    "ipfsHash", "0x" + Hex.encodeHexString((byte[]) eventData.get(3)),
                                    "timestamp", LocalDateTime.ofEpochSecond(((BigInteger) eventData.get(4)).longValue(), 0, ZoneOffset.UTC)
                                            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            ));
                            break;
                        case "FundFlowRecorded":
                            event.setParameters(Map.of(
                                    "flowId", eventData.get(0),
                                    "projectId", eventData.get(1),
                                    "recipientId", eventData.get(2),
                                    "amount", new BigDecimal((BigInteger) eventData.get(3)),
                                    "timestamp", LocalDateTime.ofEpochSecond(((BigInteger) eventData.get(4)).longValue(), 0, ZoneOffset.UTC)
                                            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            ));
                            break;
                        default:
                            event.setParameters(Map.of("rawData", eventData));
                    }
                    events.add(event);
                }
            }
            vo.setEvents(events);

            log.info("交易验证成功: transactionHash={}, events={}", request.getTransactionHash(), events);
            return CommonResponse.success("交易验证成功", vo);
        } catch (Exception e) {
            log.error("Failed to verify transaction: transactionHash={}, error={}",
                    request.getTransactionHash(), e.getMessage());
            return CommonResponse.error(ResultCode.INTERNAL_SERVER_ERROR, "交易验证失败: " + e.getMessage());
        }
    }

    /**
     * 记录资金流转
     */
    @PostMapping("/fundflow/record")
    public CommonResponse<FundFlowResponseVO> recordFundFlow(@RequestBody CharityDonationRecordFundFlowInputBO inputBO) {
        try {
            // 调用区块链服务
            TransactionResponse response = charityDonationService.recordFundFlow(inputBO);
            AssertUtil.notNull(response, ResultCode.INTERNAL_SERVER_ERROR, "区块链响应为空");
            AssertUtil.isTrue("0x0".equals(response.getTransactionReceipt().getStatus()),
                    ResultCode.INTERNAL_SERVER_ERROR, "区块链交易失败");

            // 打印交易详情
            printTransactionResponse(response);

            // 解析 FundFlowRecorded 事件
            Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
            List<List<Object>> fundFlowRecordedEvents = eventResultMap.get("FundFlowRecorded");
            AssertUtil.notEmpty(fundFlowRecordedEvents, ResultCode.INTERNAL_SERVER_ERROR, "未找到 FundFlowRecorded 事件");
            List<Object> eventData = fundFlowRecordedEvents.get(0); // 假设每次交易只有一个事件

            // 提取事件数据
            BigInteger flowId = BigInteger.valueOf(((Number) eventData.get(0)).longValue());
            BigInteger projectId = BigInteger.valueOf(((Number) eventData.get(1)).longValue());
            BigInteger recipientId = BigInteger.valueOf(((Number) eventData.get(2)).longValue());
            BigInteger amount = BigInteger.valueOf(((Number) eventData.get(3)).longValue());
            BigInteger timestamp = BigInteger.valueOf(((Number) eventData.get(4)).longValue());

            // 构造 FundFlowResponseVO
            FundFlowResponseVO fundFlowVO = new FundFlowResponseVO();
            fundFlowVO.setFlowId(flowId);
            fundFlowVO.setProjectId(projectId);
            fundFlowVO.setRecipientId(recipientId);
            fundFlowVO.setAmount(new BigDecimal(amount));
            fundFlowVO.setTimestamp(LocalDateTime.ofEpochSecond(timestamp.longValue(), 0, java.time.ZoneOffset.UTC));
            fundFlowVO.setTransactionHash(response.getTransactionReceipt().getTransactionHash());
            // 从 TransactionReceipt 获取 blockNumber（十六进制字符串）
            String blockNumberHex = response.getTransactionReceipt().getBlockNumber();
            // 去掉 "0x" 前缀并转换为 BigInteger
            BigInteger blockNumber = new BigInteger(blockNumberHex.substring(2), 16);
            fundFlowVO.setBlockNumber(blockNumber);

            log.info("记录资金流转成功，流转ID: {}, 项目ID: {}, 接收者ID: {}, 金额: {}, txHash: {}",
                    flowId, inputBO.getProjectId(), inputBO.getRecipientId(), inputBO.getAmount(), fundFlowVO.getTransactionHash());
            return CommonResponse.success("资金流转记录成功", fundFlowVO);
        } catch (Exception e) {
            log.error("记录资金流转失败: projectId={}, recipientId={}, amount={}, error={}",
                    inputBO.getProjectId(), inputBO.getRecipientId(), inputBO.getAmount(), e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "资金流转记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询项目资金流转列表
     */
    @PostMapping("/project/fundflows")
    public CommonResponse<CallResponseVO> getProjectFundFlows(@RequestBody CharityDonationGetProjectFundFlowsInputBO inputBO) {
        CallResponse response = charityDonationService.getProjectFundFlows(inputBO);
        printCallResponse(response);

        // 转换为自定义 VO，避免序列化 returnABIObject
        CallResponseVO vo = new CallResponseVO(response.getValues(), response.getReturnObject());
        log.info("查询项目资金流转成功，项目ID: {}", inputBO.getProjectId());

        return CommonResponse.success("查询项目资金流转成功", vo);
    }

    /**
     * 交易验证响应 VO
     */
    @Data
    public static class VerifyTransactionResponseVO {
        /**
         * 交易哈希
         */
        private String transactionHash;

        /**
         * 区块号
         */
        private BigInteger blockNumber;

        /**
         * 交易状态 (0x0 表示成功)
         */
        private String status;

        /**
         * 交易发送者地址
         */
        private String from;

        /**
         * 交易接收者地址
         */
        private String to;

        /**
         * 消耗的 Gas
         */
        private BigInteger gasUsed;

        /**
         * 区块哈希
         */
        private String blockHash;

        /**
         * 输入的函数名
         */
        private String inputFunction;

        /**
         * 输入参数
         */
        private List<Object> inputParameters;

        /**
         * 输出数据
         */
        private List<Object> outputData;

        /**
         * 触发的事件列表
         */
        private List<EventData> events;
    }

    /**
     * 事件数据结构
     */
    @Data
    public static class EventData {
        /**
         * 事件名称
         */
        private String eventName;

        /**
         * 事件参数
         */
        private Map<String, Object> parameters;
    }

    @Data
    public static class VerifyBO {
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
                                                                 CharityDonationDonateInputBO inputBO) {
            DonationResponseVO vo = new DonationResponseVO();
            vo.setTransactionHash(response.getTransactionReceipt() != null ?
                    response.getTransactionReceipt().getTransactionHash() : null);

            // 从事件中提取 donationId
            Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
            List<List<Object>> donatedEvents = eventResultMap.get("Donated");
            List<Object> eventData = donatedEvents.get(0);
            vo.setDonationId(BigInteger.valueOf((Integer) eventData.get(0)));

            // 从输入 BO 中提取 userId, projectId, amount
            vo.setUserId(inputBO.getUserId());
            vo.setProjectId(inputBO.getProjectId());
            vo.setAmount(new BigDecimal(inputBO.getAmount()));

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

    /**
     * 资金流转响应 VO
     */
    @Data
    public static class FundFlowResponseVO {
        /**
         * 资金流转ID
         */
        private BigInteger flowId;

        /**
         * 项目ID
         */
        private BigInteger projectId;

        /**
         * 接收者ID
         */
        private BigInteger recipientId;

        /**
         * 流转金额
         */
        private BigDecimal amount;

        /**
         * 流转时间戳
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
