package csu.yulin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举类
 *
 * @author lp
 * @create 2025-01-03
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    ACTIVE("ACTIVE", "启用"),
    INACTIVE("INACTIVE", "禁用");

    private final String code;
    private final String description;
}
