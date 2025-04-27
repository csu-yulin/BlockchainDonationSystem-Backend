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
public class CharityDonationProjectFundFlowsInputBO {
    private BigInteger arg0;

    private BigInteger arg1;

    public List<Object> toArgs() {
        List args = new ArrayList();
        args.add(arg0);
        args.add(arg1);
        return args;
    }
}
