package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigInteger;

/**
 * 公益项目视图对象，用于表示区块链智能合约中公益项目记录的返回数据。
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class ProjectVO {

    /**
     * 项目的唯一标识符。
     * 由智能合约生成，通常为自增的整数，用于区分不同的公益项目。
     */
    private BigInteger projectId;

    /**
     * 创建项目的组织标识符。
     * 对应系统中公益组织的唯一 ID，表示该项目的发起组织。
     */
    private BigInteger orgId;

    /**
     * 项目目标金额。
     * 表示该公益项目计划筹集的总金额，通常以最小单位（如 wei）记录。
     */
    private BigInteger targetAmount;

    /**
     * 项目已筹集金额。
     * 表示该公益项目当前已收到的捐款总额，通常以最小单位（如 wei）记录。
     */
    private BigInteger raisedAmount;
}