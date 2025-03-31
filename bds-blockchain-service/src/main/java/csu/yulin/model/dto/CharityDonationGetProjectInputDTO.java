package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 获取项目详情 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationGetProjectInputDTO {
    private BigInteger projectId;

    public List<Object> toArgs() {
        return List.of(projectId);
    }
}
