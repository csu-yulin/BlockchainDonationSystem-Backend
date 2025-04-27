package csu.yulin.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityDonationRecordFundFlowInputBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
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