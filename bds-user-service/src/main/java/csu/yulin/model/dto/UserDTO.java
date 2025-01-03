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
     * 用户密码，存储加密值
     */
    private String password;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

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
