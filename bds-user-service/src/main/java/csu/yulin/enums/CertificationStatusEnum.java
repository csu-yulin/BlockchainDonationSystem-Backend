package csu.yulin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公益组织认证状态枚举
 *
 * @author lp
 * @create 2025-01-04
 */
@Getter
@AllArgsConstructor
public enum CertificationStatusEnum {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝");

    private final String code;
    private final String description;
}
