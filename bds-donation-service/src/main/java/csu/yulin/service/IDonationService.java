package csu.yulin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import csu.yulin.common.PageDTO;
import csu.yulin.model.dto.DonationDTO;
import csu.yulin.model.entity.Donation;
import csu.yulin.model.vo.DonationVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 捐款记录表 服务类
 *
 * @author lp
 * @create 2025-03-26
 */
public interface IDonationService extends IService<Donation> {
    /**
     * 根据 projectId 查询项目的捐款列表
     */
    List<DonationVO> getProjectDonations(Long projectId);

    /**
     * 根据 userId 查询用户的捐款列表
     */
    List<DonationVO> getUserDonations(Long userId);

    /**
     * 分页查询捐款列表
     */
    Page<DonationVO> listDonations(PageDTO requestDTO);

    /**
     * 获取捐款总额
     */
    BigDecimal getDonationTotal(DonationDTO requestDTO);
}
