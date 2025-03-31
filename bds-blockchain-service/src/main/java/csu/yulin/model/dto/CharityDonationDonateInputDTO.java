package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 捐赠dto
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationDonateInputDTO {
    private BigInteger userId;
    private BigInteger projectId;
    private BigInteger amount;

    public List<Object> toArgs() {
        return List.of(userId, projectId, amount);
    }
}
