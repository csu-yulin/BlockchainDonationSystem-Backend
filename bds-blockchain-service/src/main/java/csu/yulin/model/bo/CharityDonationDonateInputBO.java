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
public class CharityDonationDonateInputBO {
    private BigInteger userId;

    private BigInteger projectId;

    private BigInteger amount;

    public List<Object> toArgs() {
        List args = new ArrayList();
        args.add(userId);
        args.add(projectId);
        args.add(amount);
        return args;
    }
}
