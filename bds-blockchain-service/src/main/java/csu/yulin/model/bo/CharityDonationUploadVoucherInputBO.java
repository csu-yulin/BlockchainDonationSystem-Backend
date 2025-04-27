package csu.yulin.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationUploadVoucherInputBO {
    private BigInteger projectId;

    private BigInteger orgId;

    private byte[] ipfsHash;

    public List<Object> toArgs() {
        List args = new ArrayList();
        args.add(projectId);
        args.add(orgId);
        args.add(ipfsHash);
        return args;
    }
}
