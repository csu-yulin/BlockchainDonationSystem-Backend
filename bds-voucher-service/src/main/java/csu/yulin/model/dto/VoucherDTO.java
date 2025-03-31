package csu.yulin.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 凭证记录表 DTO
 *
 * @author lp
 * @create 2025-03-27
 */
@Data
public class VoucherDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 凭证ID，与链上 voucherId 一致
     */
    private Long voucherId;

    /**
     * 关联项目ID（关联 project 表）
     */
    private Long projectId;

    /**
     * 公益组织ID（关联 user 表，role=ORG）
     */
    private Long orgId;

    /**
     * 凭证文件的IPFS哈希，与链上 bytes32 一致
     */
    private String ipfsHash;

    /**
     * 上传时间戳，与链上一致
     */
    private LocalDateTime timestamp;

    /**
     * 区块链交易哈希，用于追溯链上记录
     */
    private String transactionHash;

    /**
     * 凭证文件的实际URL（可选，链下存储）
     */
    private String fileUrl;

}