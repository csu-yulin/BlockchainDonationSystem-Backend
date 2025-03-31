package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 项目凭证请求 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationProjectVouchersInputDTO {
    // 项目ID
    private BigInteger arg0;

    // 凭证ID
    private BigInteger arg1;

    public List<Object> toArgs() {
        return List.of(arg0, arg1);
    }
}
