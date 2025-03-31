package csu.yulin.common;

import lombok.Data;

import java.time.LocalDateTime;

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
     * 项目ID（筛选条件，可选）
     */
    private Long projectId;

    /**
     * 组织ID（筛选条件，可选）
     */
    private Long orgId;

    /**
     * 开始时间（筛选条件，可选）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（筛选条件，可选）
     */
    private LocalDateTime endTime;
}
