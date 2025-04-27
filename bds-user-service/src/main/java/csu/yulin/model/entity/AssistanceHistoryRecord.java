package csu.yulin.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssistanceHistoryRecord {
    private Long assistanceId;
    private Long projectId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String txHash;
}
