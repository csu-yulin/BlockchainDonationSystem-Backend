package csu.yulin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 上传凭证请求 DTO
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationUploadVoucherInputDTO {
    private BigInteger projectId;

    private BigInteger orgId;

    private byte[] ipfsHash;

    public List<Object> toArgs() {
        return List.of(projectId, orgId, ipfsHash);
    }
}
