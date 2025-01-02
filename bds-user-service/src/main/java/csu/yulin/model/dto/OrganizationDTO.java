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
     * 用户ID
     */
    private Long userId;

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
     * 公益组织联系人姓名
     */
    private String contactPersonName;
}
