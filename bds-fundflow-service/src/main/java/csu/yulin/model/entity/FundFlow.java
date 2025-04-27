package csu.yulin.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金流动记录表
 *
 * @author lp
 * @create 2025-04-25
 */
@Data
public class FundFlow implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 资金流动ID，与链上 flowId 一致
     */
    @TableId
    private Long flowId;

    /**
     * 关联项目ID（关联 project 表）
     */
    private Long projectId;

    /**
     * 资金接收者ID（关联 user 表，role=INDIVIDUAL 或 ORG）
     */
    private Long recipientId;

    /**
     * 流动金额
     */
    private BigDecimal amount;

    /**
     * 挪用理由（如“用户求助”、“项目支出”）
     */
    private String reason;

    /**
     * 用户求助证明资料的阿里云 OSS URL（用户申领时使用）
     */
    private String proofFileUrl;

    /**
     * 关联凭证ID（关联 voucher 表，公益组织上传的凭证）
     */
    private Long voucherId;

    /**
     * 审核状态：待审核、通过、拒绝
     */
    private String status;

    /**
     * 审核备注或拒绝原因
     */
    private String reviewNotes;

    /**
     * 审核者ID（管理员，关联 user 表，role=ADMIN）
     */
    private Long reviewerId;

    /**
     * 链上时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 区块链交易哈希，用于追溯链上记录
     */
    private String transactionHash;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}