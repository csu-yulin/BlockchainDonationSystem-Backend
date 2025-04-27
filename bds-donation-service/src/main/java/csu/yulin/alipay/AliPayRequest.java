package csu.yulin.alipay;

import lombok.Data;

@Data
public class AliPayRequest {
    private String userId;
    private String projectId;
    private Long amount;
}