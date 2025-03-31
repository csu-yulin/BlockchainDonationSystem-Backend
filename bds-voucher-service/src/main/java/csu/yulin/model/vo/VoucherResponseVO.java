package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 凭证上传响应 VO
 *
 * @author lp
 * @create 2025-03-27
 */
@Data
public class VoucherResponseVO {
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