package csu.yulin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.yulin.common.PageDTO;
import csu.yulin.mapper.DonationMapper;
import csu.yulin.model.convert.DonationConverter;
import csu.yulin.model.dto.DonationDTO;
import csu.yulin.model.entity.Donation;
import csu.yulin.model.vo.DonationVO;
import csu.yulin.service.IDonationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Donation表 服务实现类
 *
 * @author lp
 * @create 2025-03-26
 */
@Service
public class DonationServiceImpl extends ServiceImpl<DonationMapper, Donation>
        implements IDonationService {

    /**
     * 根据 projectId 查询项目的捐款列表
     */
    @Override
    public List<DonationVO> getProjectDonations(Long projectId) {
        // 查询数据库中的捐款记录
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getProjectId, projectId)
                .eq(Donation::getIsDeleted, false);
        List<Donation> donations = this.list(queryWrapper);

        // 转换为 VO 列表
        List<DonationVO> donationVOs = donations.stream()
                .map(DonationConverter::toVO)
                .collect(Collectors.toList());

        return donationVOs;
    }

    /**
     * 根据 userId 查询用户的捐款历史
     */
    @Override
    public List<DonationVO> getUserDonations(Long userId) {
        // 查询数据库中的捐款记录
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getUserId, userId)
                .eq(Donation::getIsDeleted, false);
        List<Donation> donations = this.list(queryWrapper);

        // 转换为 VO 列表
        List<DonationVO> donationVOs = donations.stream()
                .map(DonationConverter::toVO)
                .collect(Collectors.toList());

        return donationVOs;
    }

    /**
     * 分页查询捐款记录: 支持按项目、用户、时间范围筛选
     */
    @Override
    public Page<DonationVO> listDonations(PageDTO requestDTO) {
        // 构造分页对象
        Page<Donation> page = new Page<>(requestDTO.getPage(), requestDTO.getSize());

        // 构造查询条件
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getIsDeleted, false);

        // 筛选条件
        if (requestDTO.getProjectId() != null) {
            queryWrapper.eq(Donation::getProjectId, requestDTO.getProjectId());
        }
        if (requestDTO.getUserId() != null) {
            queryWrapper.eq(Donation::getUserId, requestDTO.getUserId());
        }
        if (requestDTO.getStartTime() != null) {
            queryWrapper.ge(Donation::getTimestamp, requestDTO.getStartTime());
        }
        if (requestDTO.getEndTime() != null) {
            queryWrapper.le(Donation::getTimestamp, requestDTO.getEndTime());
        }

        // 默认按时间降序排序
        queryWrapper.orderByDesc(Donation::getTimestamp);

        // 执行分页查询
        Page<Donation> donationPage = this.page(page, queryWrapper);

        // 转换为 VO
        List<DonationVO> donationVOs = donationPage.getRecords().stream()
                .map(DonationConverter::toVO)
                .collect(Collectors.toList());

        // 构造返回的分页对象
        Page<DonationVO> resultPage = new Page<>(requestDTO.getPage(), requestDTO.getSize());
        resultPage.setRecords(donationVOs);
        resultPage.setTotal(donationPage.getTotal());

        return resultPage;
    }

    /**
     * 统计捐款总额: 根据项目或用户条件统计捐款总额
     */
    @Override
    public BigDecimal getDonationTotal(DonationDTO requestDTO) {
        // 构造查询条件
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getIsDeleted, false);

        // 筛选条件
        if (requestDTO.getProjectId() != null) {
            queryWrapper.eq(Donation::getProjectId, requestDTO.getProjectId());
        }
        if (requestDTO.getUserId() != null) {
            queryWrapper.eq(Donation::getUserId, requestDTO.getUserId());
        }

        // 查询所有符合条件的记录
        List<Donation> donations = this.list(queryWrapper);

        // 计算总额
        BigDecimal totalAmount = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.compareTo(BigDecimal.ZERO) > 0 ? totalAmount : null;
    }
}




