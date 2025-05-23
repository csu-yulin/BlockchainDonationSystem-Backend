package csu.yulin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目审批状态枚举
 *
 * @author lp
 * @create 2025-01-08
 */
@Getter
@AllArgsConstructor
public enum ApprovalStatusEnum {
    PENDING("PENDING", "待审批"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝");

    private final String code;
    private final String description;
}
