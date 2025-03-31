package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 获取凭证 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationGetVoucherInputDTO {
    private BigInteger voucherId;

    public List<Object> toArgs() {
        return List.of(voucherId);
    }
}
