package csu.yulin.common;

import lombok.Data;

import java.time.LocalDate;

/**
 * 分页查询 DTO
 *
 * @author lp
 * @create 2025-01-04
 */
@Data
public class PageDTO {
    /**
     * 当前页码（默认值为 1）
     */
    private long page = 1;

    /**
     * 每页大小（默认值为 10）
     */
    private long size = 10;

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 创建者角色：INDIVIDUAL 或 ORG
     */
    private String creatorRole;

    /**
     * 审批状态：PENDING、APPROVED、REJECTED
     */
    private String approvalStatus;

    /**
     * 项目状态：ACTIVE、COMPLETED、CANCELLED、EXPIRED
     */
    private String status;

    /**
     * 项目开始日期范围：起始日期
     */
    private LocalDate startDate;

    /**
     * 项目开始日期范围：结束日期
     */
    private LocalDate endDate;
}
