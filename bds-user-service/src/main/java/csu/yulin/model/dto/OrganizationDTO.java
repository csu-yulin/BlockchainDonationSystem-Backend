package csu.yulin.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公益组织 DTO
 *
 * @author lp
 * @create 2025-01-02
 */
@Data
public class OrganizationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键
     */
    private Long userId;

    /**
     * 公益组织名称
     */
    private String orgName;

    /**
     * 用户密码，存储加密值
     */
    private String password;

    /**
     * 公益组织电子邮件地址
     */
    private String email;

    /**
     * 公益组织LOGO
     */
    private String avatar;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

    /**
     * 公益组织联系人姓名
     */
    private String contactPersonName;

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
     * 用户状态：ACTIVE 表示启用，INACTIVE 表示禁用
     */
    private String status;

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
}
