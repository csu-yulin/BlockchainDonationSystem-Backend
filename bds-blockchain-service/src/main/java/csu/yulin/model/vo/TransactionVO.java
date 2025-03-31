package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigInteger;

/**
 * 区块链交易视图对象，用于表示智能合约交易的返回数据。
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class TransactionVO {

    /**
     * 交易的哈希值。
     * 由区块链生成，是该交易的唯一标识符，通常为 66 字符的十六进制字符串（包含 "0x" 前缀）。
     * 可用于在区块链浏览器中查询交易详情。
     */
    private String transactionHash;

    /**
     * 交易所在区块的编号。
     * 表示该交易被打包确认的区块高度，是区块链上的一个整数标识。
     */
    private BigInteger blockNumber;

    /**
     * 交易状态。
     * 表示交易的执行结果，通常为 "0"（成功）或非 "0"（失败），具体值由区块链平台定义。
     */
    private String status;
}