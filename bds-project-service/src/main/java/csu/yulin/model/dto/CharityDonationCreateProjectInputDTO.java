package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 创建项目dto
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationCreateProjectInputDTO {
    private BigInteger orgId;
    private BigInteger targetAmount;

    public List<Object> toArgs() {
        return List.of(orgId, targetAmount);
    }
}
