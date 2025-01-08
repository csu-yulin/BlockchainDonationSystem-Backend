package csu.yulin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举类
 *
 * @author lp
 * @create 2025-01-02
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {
    INDIVIDUAL("INDIVIDUAL", "个体用户"),
    ADMIN("ADMIN", "管理员"),
    ORGANIZATION("ORG", "公益组织");

    private final String code;
    private final String description;
}
