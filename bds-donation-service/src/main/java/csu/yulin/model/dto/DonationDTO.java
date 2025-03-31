package csu.yulin.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 捐赠记录表 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class DonationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 捐款ID，与链上 donationId 一致
     */
    private Long donationId;

    /**
     * 捐款用户ID（关联 user 表）
     */
    private Long userId;

    /**
     * 关联项目ID（关联 project 表）
     */
    private Long projectId;

    /**
     * 捐款金额
     */
    private BigDecimal amount;

    /**
     * 捐款时间戳，与链上一致
     */
    private LocalDateTime timestamp;

    /**
     * 区块链交易哈希，用于追溯链上记录
     */
    private String transactionHash;

}