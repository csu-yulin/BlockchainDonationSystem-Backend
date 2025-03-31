package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 获取捐赠记录 DTO
 * <p>
 * 用于封装查询捐赠记录的请求参数
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationDonationsInputDTO {
    private BigInteger arg0;

    public List<Object> toArgs() {
        return List.of(arg0);
    }
}
