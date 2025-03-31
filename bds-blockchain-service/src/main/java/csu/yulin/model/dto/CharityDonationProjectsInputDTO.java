package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 查询项目列表 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationProjectsInputDTO {
    private BigInteger arg0;

    public List<Object> toArgs() {
        return List.of(arg0);
    }
}
