package csu.yulin.model.convert;

import csu.yulin.model.dto.DonationDTO;
import csu.yulin.model.entity.Donation;
import csu.yulin.model.vo.DonationVO;
import org.springframework.beans.BeanUtils;

/**
 * 捐款实体与 DTO、VO 的转换工具类
 *
 * @author lp
 * @create 2025-03-26
 */
public class DonationConverter {

    /**
     * 将 Donation 实体转换为 DonationDTO
     *
     * @param donation 捐款实体
     * @return 捐款 DTO
     */
    public static DonationDTO toDTO(Donation donation) {
        if (donation == null) {
            return null;
        }
        DonationDTO dto = new DonationDTO();
        BeanUtils.copyProperties(donation, dto);
        return dto;
    }

    /**
     * 将 DonationDTO 转换为 Donation 实体
     *
     * @param dto 捐款 DTO
     * @return 捐款实体
     */
    public static Donation toEntity(DonationDTO dto) {
        if (dto == null) {
            return null;
        }
        Donation donation = new Donation();
        BeanUtils.copyProperties(dto, donation);
        return donation;
    }

    /**
     * 将 Donation 实体转换为 DonationVO
     *
     * @param donation 捐款实体
     * @return 捐款 VO
     */
    public static DonationVO toVO(Donation donation) {
        if (donation == null) {
            return null;
        }
        DonationVO vo = new DonationVO();
        BeanUtils.copyProperties(donation, vo);
        return vo;
    }

    /**
     * 将 DonationDTO 转换为 DonationVO
     *
     * @param dto 捐款 DTO
     * @return 捐款 VO
     */
    public static DonationVO toVO(DonationDTO dto) {
        if (dto == null) {
            return null;
        }
        DonationVO vo = new DonationVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}