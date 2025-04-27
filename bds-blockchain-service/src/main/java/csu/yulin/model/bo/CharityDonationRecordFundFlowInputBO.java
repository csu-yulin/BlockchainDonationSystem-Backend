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
public class CharityDonationRecordFundFlowInputBO {
    private BigInteger projectId;

    private BigInteger recipientId;

    private BigInteger amount;

    public List<Object> toArgs() {
        List args = new ArrayList();
        args.add(projectId);
        args.add(recipientId);
        args.add(amount);
        return args;
    }
}
