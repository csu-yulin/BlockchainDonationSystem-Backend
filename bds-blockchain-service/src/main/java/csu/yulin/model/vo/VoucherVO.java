package csu.yulin.model.vo;

import lombok.Data;

import java.math.BigInteger;

/**
 * 凭证视图对象，用于表示区块链智能合约中凭证记录的返回数据。
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
public class VoucherVO {

    /**
     * 凭证的唯一标识符。
     * 由智能合约生成，通常为自增的整数，用于区分不同的凭证记录。
     */
    private BigInteger voucherId;

    /**
     * 凭证关联的公益项目标识符。
     * 对应系统中公益项目的唯一 ID，表示该凭证所属的具体项目。
     */
    private BigInteger projectId;

    /**
     * 上传凭证的组织标识符。
     * 对应系统中公益组织的唯一 ID，表示上传该凭证的组织。
     */
    private BigInteger orgId;

    /**
     * 凭证的 IPFS 哈希值。
     * 表示存储在 IPFS（分布式文件系统）中的凭证文件哈希，通常为字节数组，用于定位和验证凭证内容。
     */
    private byte[] ipfsHash;

    /**
     * 凭证上传时间戳。
     * 表示凭证被记录到区块链上的时间，通常为 Unix 时间戳（秒）。
     */
    private BigInteger timestamp;
}