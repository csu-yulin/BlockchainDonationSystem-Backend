package csu.yulin.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 个体用户 VO
 *
 * @author lp
 * @create 2025-01-02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户电子邮件地址
     */
    private String email;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

    /**
     * 用户个人头像的 URL
     */
    private String avatar;

    /**
     * 用户个人简介
     */
    private String userBio;

    /**
     * 用户银行账户信息
     */
    private String userBankAccount;

    /**
     * 用户的捐赠历史记录（JSON格式）
     */
    private String donationHistory;

    /**
     * 用户的受助历史记录（JSON格式）
     */
    private String assistanceHistory;

    /**
     * 用户角色：个体(捐赠者和受助者)、管理员、公益组织
     */
    private String role;

    /**
     * 用户状态：ACTIVE 表示启用，INACTIVE 表示禁用
     */
    private String status;
}
