package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 资金流转响应 VO
 *
 * @author lp200
 */
@Data
public class FundFlowResponseVO {
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