package csu.yulin.common;

import lombok.Data;

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
     * 用户状态：ACTIVE 表示启用，INACTIVE 表示禁用
     */
    private String status;

    /**
     * 公益组织认证状态：PENDING、APPROVED、REJECTED
     */
    private String certificationStatus;
}
