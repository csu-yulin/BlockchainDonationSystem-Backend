package csu.yulin.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员 DTO
 *
 * @author lp
 * @create 2025-01-02
 */
@Data
public class AdminDTO implements Serializable {

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
     * 管理员等级
     */
    private Byte adminLevel;

    /**
     * 用户电子邮件地址
     */
    private String email;

    /**
     * 用户电话号码
     */
    private String phoneNumber;

    /**
     * 用户状态
     */
    private String status;
}
