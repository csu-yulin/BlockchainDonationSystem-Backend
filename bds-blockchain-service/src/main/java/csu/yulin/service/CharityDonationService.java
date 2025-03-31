package csu.yulin.service;

import csu.yulin.model.dto.*;
import csu.yulin.utils.IOUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 负责 CharityDonation 合约的调用
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@Slf4j
@Service
@NoArgsConstructor
public class CharityDonationService {

    public static final String ABI = IOUtil.readResourceAsString("abi/CharityDonation.abi");
    public static final String BINARY = IOUtil.readResourceAsString("bin/ecc/CharityDonation.bin");
    public static final String SM_BINARY = IOUtil.readResourceAsString("bin/sm/CharityDonation.bin");

    private AssembleTransactionProcessor txProcessor;
    private CryptoKeyPair cryptoKeyPair;

    @Value("${system.contract.charityDonationAddress}")
    private String address;

    @Autowired
    private Client client;

    @PostConstruct
    public void init() {
        try {
            this.cryptoKeyPair = this.client.getCryptoSuite().getCryptoKeyPair();
            this.txProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, this.cryptoKeyPair);
            log.info("CharityDonationService 初始化成功，合约地址: {}", this.address);
        } catch (Exception e) {
            log.error("CharityDonationService 初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("CharityDonationService 初始化失败", e);
        }
    }

    /*** 通用调用方法（只读） ***/
    private CallResponse callContractMethod(String methodName, List<Object> args) {
        try {
            return this.txProcessor.sendCall(this.cryptoKeyPair.getAddress(), this.address, ABI, methodName, args);
        } catch (Exception e) {
            log.error("调用 {} 失败: {}", methodName, e.getMessage(), e);
            throw new RuntimeException("调用 " + methodName + " 失败", e);
        }
    }

    /*** 通用交易方法（修改状态） ***/
    private TransactionResponse sendTransaction(String methodName, List<Object> args) {
        try {
            return this.txProcessor.sendTransactionAndGetResponse(this.address, ABI, methodName, args);
        } catch (Exception e) {
            log.error("交易 {} 失败: {}", methodName, e.getMessage(), e);
            throw new RuntimeException("交易 " + methodName + " 失败", e);
        }
    }

    /*** 区块链查询方法 ***/
    public CallResponse getProjectDonations(CharityDonationGetProjectDonationsInputDTO input) {
        return callContractMethod("getProjectDonations", input.toArgs());
    }

    public CallResponse donationCount() {
        return callContractMethod("donationCount", List.of());
    }

    public CallResponse projectVouchers(CharityDonationProjectVouchersInputDTO input) {
        return callContractMethod("projectVouchers", input.toArgs());
    }

    public CallResponse getProjectVouchers(CharityDonationGetProjectVouchersInputDTO input) {
        return callContractMethod("getProjectVouchers", input.toArgs());
    }

    public CallResponse getProject(CharityDonationGetProjectInputDTO input) {
        return callContractMethod("getProject", input.toArgs());
    }

    public CallResponse voucherCount() {
        return callContractMethod("voucherCount", List.of());
    }

    public CallResponse getDonation(CharityDonationGetDonationInputDTO input) {
        return callContractMethod("getDonation", input.toArgs());
    }

    public CallResponse getVoucher(CharityDonationGetVoucherInputDTO input) {
        return callContractMethod("getVoucher", input.toArgs());
    }

    public CallResponse vouchers(CharityDonationVouchersInputDTO input) {
        return callContractMethod("vouchers", input.toArgs());
    }

    public CallResponse projectDonations(CharityDonationProjectDonationsInputDTO input) {
        return callContractMethod("projectDonations", input.toArgs());
    }

    public CallResponse donations(CharityDonationDonationsInputDTO input) {
        return callContractMethod("donations", input.toArgs());
    }

    public CallResponse projects(CharityDonationProjectsInputDTO input) {
        return callContractMethod("projects", input.toArgs());
    }

    public CallResponse projectCount() {
        return callContractMethod("projectCount", List.of());
    }

    /*** 区块链交易方法 ***/
    public TransactionResponse uploadVoucher(CharityDonationUploadVoucherInputDTO input) {
        return sendTransaction("uploadVoucher", input.toArgs());
    }

    public TransactionResponse donate(CharityDonationDonateInputDTO input) {
        return sendTransaction("donate", input.toArgs());
    }

    public TransactionResponse createProject(CharityDonationCreateProjectInputDTO input) {
        return sendTransaction("createProject", input.toArgs());
    }
}
