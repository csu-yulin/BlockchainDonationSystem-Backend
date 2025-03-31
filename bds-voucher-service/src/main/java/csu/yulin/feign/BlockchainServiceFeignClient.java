package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.dto.CharityDonationUploadVoucherInputDTO;
import csu.yulin.model.vo.VoucherResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-blockchain-service")
public interface BlockchainServiceFeignClient {

    /**
     * 上传凭证
     */
    @PostMapping("/blockchain/voucher/upload")
    CommonResponse<VoucherResponseVO> uploadVoucher(@RequestBody CharityDonationUploadVoucherInputDTO inputDTO);
}
