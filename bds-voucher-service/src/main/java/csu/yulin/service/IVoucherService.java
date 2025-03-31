package csu.yulin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import csu.yulin.common.PageDTO;
import csu.yulin.model.dto.VoucherDTO;
import csu.yulin.model.entity.Voucher;

/**
 * 凭证记录表 服务类
 *
 * @author lp
 * @create 2025-03-27
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 查询凭证列表（支持分页和过滤）
     */
    IPage<VoucherDTO> listVouchers(PageDTO pageDTO);

    /**
     * 根据IPFS哈希查询凭证
     */
    VoucherDTO getVoucherByIpfsHash(String ipfsHash);
}
