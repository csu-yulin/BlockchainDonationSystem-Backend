package csu.yulin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目状态枚举
 *
 * @author lp
 * @create 2025-01-08
 */
@Getter
@AllArgsConstructor
public enum ProjectStatusEnum {
    ACTIVE("ACTIVE", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String description;
}
