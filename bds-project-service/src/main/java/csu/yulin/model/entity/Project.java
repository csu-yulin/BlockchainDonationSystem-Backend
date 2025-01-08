package csu.yulin.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目表
 *
 * @author lp
 * @create 2025-01-07
 */
@Data
public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID，主键
     */
    @TableId(value = "project_id", type = IdType.AUTO)
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目详细描述
     */
    private String description;

    /**
     * 项目封面图片的URL
     */
    private String coverImage;

    /**
     * 项目创建者的用户ID（关联user表）
     */
    private Long creatorId;

    /**
     * 项目创建者角色：个人或公益组织
     */
    private String creatorRole;

    /**
     * 若由公益组织发起，记录组织名称
     */
    private String orgName;

    /**
     * 项目联系人姓名
     */
    private String contactPersonName;

    /**
     * 项目审批状态
     */
    private String approvalStatus;

    /**
     * 审批备注或原因
     */
    private String approvalNotes;

    /**
     * 最后审核该项目的管理员ID（关联user表）
     */
    private Long verifierId;

    /**
     * 项目状态：进行中、已完成、已取消
     */
    private String status;

    /**
     * 目标募集金额
     */
    private BigDecimal targetAmount;

    /**
     * 已募集金额
     */
    private BigDecimal raisedAmount;

    /**
     * 用于接收捐款的银行账户
     */
    private String bankAccount;

    /**
     * 项目开始日期
     */
    private LocalDate startDate;

    /**
     * 项目结束日期（可为空，表示长期项目）
     */
    private LocalDate endDate;

    /**
     * 项目的捐赠记录（JSON格式）
     */
    private String donationRecords;

    /**
     * 项目相关的活动记录（如更新、公告等，JSON格式）
     */
    private String activityRecords;

    /**
     * 逻辑删除标志：TRUE 表示已删除
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 项目创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 项目最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}