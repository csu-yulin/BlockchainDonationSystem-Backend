package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 项目捐赠查询 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationProjectDonationsInputDTO {

    private BigInteger arg0;

    private BigInteger arg1;

    public List<Object> toArgs() {
        return List.of(arg0, arg1);
    }
}
