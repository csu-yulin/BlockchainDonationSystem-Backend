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
 * 捐赠信息表
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class Donation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 捐款ID，与链上 donationId 一致
     */
    @TableId
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

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer isDeleted;

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