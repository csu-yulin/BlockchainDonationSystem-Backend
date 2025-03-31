package csu.yulin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.yulin.common.PageDTO;
import csu.yulin.mapper.VoucherMapper;
import csu.yulin.model.convert.VoucherConverter;
import csu.yulin.model.dto.VoucherDTO;
import csu.yulin.model.entity.Voucher;
import csu.yulin.service.IVoucherService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * 凭证记录表 服务实现类
 *
 * @author lp
 * @create 2025-03-27
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher>
        implements IVoucherService {

    /**
     * 查询凭证列表（支持分页和过滤）
     */
    @Override
    public IPage<VoucherDTO> listVouchers(PageDTO pageDTO) {
        // 构造分页对象
        Page<Voucher> pageRequest = new Page<>(pageDTO.getPage(), pageDTO.getSize());

        // 构造查询条件
        LambdaQueryWrapper<Voucher> queryWrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getProjectId() != null) {
            queryWrapper.eq(Voucher::getProjectId, pageDTO.getProjectId());
        }
        if (pageDTO.getOrgId() != null) {
            queryWrapper.eq(Voucher::getOrgId, pageDTO.getOrgId());
        }
        if (pageDTO.getStartTime() != null) {
            queryWrapper.ge(Voucher::getTimestamp, pageDTO.getStartTime());
        }
        if (pageDTO.getEndTime() != null) {
            queryWrapper.le(Voucher::getTimestamp, pageDTO.getEndTime());
        }
        // 默认按创建时间降序排序
        queryWrapper.orderByDesc(Voucher::getCreatedAt);

        // 查询分页数据
        IPage<Voucher> voucherPage = this.page(pageRequest, queryWrapper);

        // 转换为 DTO 分页对象
        Page<VoucherDTO> resultPage = new Page<>(voucherPage.getCurrent(), voucherPage.getSize(), voucherPage.getTotal());
        resultPage.setRecords(voucherPage.getRecords().stream()
                .map(VoucherConverter::toDTO)
                .collect(Collectors.toList()));

        return resultPage;
    }

    /**
     * 根据IPFS哈希查询凭证
     */
    @Override
    public VoucherDTO getVoucherByIpfsHash(String ipfsHash) {
        LambdaQueryWrapper<Voucher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Voucher::getIpfsHash, ipfsHash);
        Voucher voucher = this.getOne(queryWrapper);
        return voucher != null ? VoucherConverter.toDTO(voucher) : null;
    }
}




