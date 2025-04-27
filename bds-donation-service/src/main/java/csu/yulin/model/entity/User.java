package csu.yulin.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @author lp
 * @create 2024-12-30
 */
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户昵称，非唯一
     */
    private String username;

    /**
     * 用户密码，存储加密值
     */
    private String password;

    /**
     * 用户电子邮件地址
     */
    private String email;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

    /**
     * 用户个人头像的URL | 公益组织LOGO
     */
    private String avatar;

    /**
     * 用户角色：个体(捐赠者和受助者)、管理员、公益组织
     */
    private String role;

    /**
     * 管理员等级：1-基础，2-审核组织、项目资质
     */
    private Byte adminLevel;

    /**
     * 用户状态：ACTIVE 表示启用，INACTIVE 表示禁用
     */
    private String status;

    /**
     * 用户真实姓名
     */
    private String userRealName;

    /**
     * 用户身份证号
     */
    private String idCardNumber;

    /**
     * 用户个人简介
     */
    private String userBio;

    /**
     * 用户银行账户信息
     */
    private String userBankAccount;

    /**
     * 公益组织名称
     */
    private String orgName;

    /**
     * 公益组织简介或宗旨说明
     */
    private String orgBio;

    /**
     * 公益组织注册号或营业执照编号
     */
    private String orgLicenseNumber;

    /**
     * 公益组织银行账户信息
     */
    private String orgBankAccount;

    /**
     * 公益组织认证状态
     */
    private String certificationStatus;

    /**
     * 认证状态备注或原因
     */
    private String certificationNotes;

    /**
     * 最后审核该组织信息的管理员ID
     */
    private Long verifierId;

    /**
     * 公益组织联系人姓名
     */
    private String contactPersonName;

    /**
     * 用户的捐赠历史记录（JSON格式）
     */
    private String donationHistory;

    /**
     * 用户的受助历史记录（JSON格式）
     */
    private String assistanceHistory;

    /**
     * 逻辑删除标志：TRUE 表示已删除
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 用户注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 用户信息最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
