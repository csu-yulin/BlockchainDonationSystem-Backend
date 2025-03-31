package csu.yulin.model.convert;

import csu.yulin.model.dto.VoucherDTO;
import csu.yulin.model.entity.Voucher;
import csu.yulin.model.vo.VoucherVO;
import org.springframework.beans.BeanUtils;

/**
 * 凭证实体与 DTO、VO 的转换工具类
 *
 * @author lp
 * @create 2025-03-27
 */
public class VoucherConverter {

    /**
     * 将 Voucher 实体转换为 VoucherDTO
     *
     * @param voucher 凭证实体
     * @return 凭证 DTO
     */
    public static VoucherDTO toDTO(Voucher voucher) {
        if (voucher == null) {
            return null;
        }
        VoucherDTO dto = new VoucherDTO();
        BeanUtils.copyProperties(voucher, dto);
        return dto;
    }

    /**
     * 将 VoucherDTO 转换为 Voucher 实体
     *
     * @param dto 凭证 DTO
     * @return 凭证实体
     */
    public static Voucher toEntity(VoucherDTO dto) {
        if (dto == null) {
            return null;
        }
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(dto, voucher);
        return voucher;
    }

    /**
     * 将 Voucher 实体转换为 VoucherVO
     *
     * @param voucher 凭证实体
     * @return 凭证 VO
     */
    public static VoucherVO toVO(Voucher voucher) {
        if (voucher == null) {
            return null;
        }
        VoucherVO vo = new VoucherVO();
        BeanUtils.copyProperties(voucher, vo);
        return vo;
    }

    /**
     * 将 VoucherDTO 转换为 VoucherVO
     *
     * @param dto 凭证 DTO
     * @return 凭证 VO
     */
    public static VoucherVO toVO(VoucherDTO dto) {
        if (dto == null) {
            return null;
        }
        VoucherVO vo = new VoucherVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}