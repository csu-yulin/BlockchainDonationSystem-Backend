package csu.yulin.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户 DTO
 *
 * @author lp
 * @create 2025-01-02
 */
@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户密码，存储加密值
     */
    private String password;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

    /**
     * 用户电子邮件地址
     */
    private String email;

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
     * 用户状态：ACTIVE 表示启用，INACTIVE 表示禁用
     */
    private String status;

    /**
     * 验证码
     */
    private String code;

    /**
     * 用户真实姓名
     */
    private String userRealName;

    /**
     * 用户身份证号
     */
    private String idCardNumber;
}
