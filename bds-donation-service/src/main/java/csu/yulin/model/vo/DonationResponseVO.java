package csu.yulin.model.vo;

import csu.yulin.model.dto.CharityDonationDonateInputDTO;
import lombok.Data;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 捐款响应视图对象
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class DonationResponseVO {
    /**
     * 区块链交易哈希
     */
    private String transactionHash;

    /**
     * 捐款ID
     */
    private BigInteger donationId;

    /**
     * 捐款用户ID
     */
    private BigInteger userId;

    /**
     * 关联项目ID
     */
    private BigInteger projectId;

    /**
     * 捐款金额
     */
    private BigDecimal amount;

    /**
     * 从 TransactionResponse 转换到 VO
     */
    public static DonationResponseVO fromTransactionResponse(TransactionResponse response,
                                                             CharityDonationDonateInputDTO inputDTO) {
        if (response == null || inputDTO == null) {
            return null;
        }
        DonationResponseVO vo = new DonationResponseVO();
        vo.setTransactionHash(response.getTransactionReceipt() != null ?
                response.getTransactionReceipt().getTransactionHash() : null);

        // 从事件中提取 donationId
        Map<String, List<List<Object>>> eventResultMap = response.getEventResultMap();
        List<List<Object>> donatedEvents = eventResultMap.get("Donated");
        List<Object> eventData = donatedEvents.get(0);
        vo.setDonationId((BigInteger) eventData.get(0));

        // 从输入 DTO 中提取 userId, projectId, amount
        vo.setUserId(inputDTO.getUserId());
        vo.setProjectId(inputDTO.getProjectId());
        vo.setAmount(new BigDecimal(inputDTO.getAmount()));

        return vo;
    }
}