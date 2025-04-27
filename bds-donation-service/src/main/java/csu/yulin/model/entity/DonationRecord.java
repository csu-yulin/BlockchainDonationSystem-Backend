package csu.yulin.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationRecord {
    private Long donationId;
    private Long userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String txHash;
}
