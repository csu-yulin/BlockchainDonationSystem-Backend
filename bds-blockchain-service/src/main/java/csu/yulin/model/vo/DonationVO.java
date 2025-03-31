package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigInteger;

// TODO:暂定用不到VO

/**
 * 捐款视图对象，用于表示区块链智能合约中捐款记录的返回数据。
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class DonationVO {

    /**
     * 捐款的唯一标识符。
     * 由智能合约生成，通常为自增的整数，用于区分不同的捐款记录。
     */
    private BigInteger donationId;

    /**
     * 捐款用户的标识符。
     * 对应系统中用户的唯一 ID，表示发起捐款的用户。
     */
    private BigInteger userId;

    /**
     * 捐款目标项目的标识符。
     * 对应系统中公益项目的唯一 ID，表示捐款所支持的具体项目。
     */
    private BigInteger projectId;

    /**
     * 捐款金额。
     * 表示用户捐款的数值，通常以最小单位（如 wei）记录，避免浮点数精度问题。
     */
    private BigInteger amount;

    /**
     * 捐款时间戳。
     * 表示捐款交易被记录到区块链上的时间，通常为 Unix 时间戳（秒）。
     */
    private BigInteger timestamp;
}