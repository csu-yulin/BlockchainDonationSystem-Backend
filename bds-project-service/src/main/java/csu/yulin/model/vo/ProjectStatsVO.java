package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectStatsVO {
    /**
     * 项目总数
     */
    private Long projectCount;

    /**
     * 目标总金额
     */
    private BigDecimal totalTargetAmount;

    /**
     * 已筹集总金额
     */
    private BigDecimal totalRaisedAmount;
}