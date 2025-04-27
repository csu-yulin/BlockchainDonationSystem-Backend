package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.bo.CharityDonationRecordFundFlowInputBO;
import csu.yulin.model.vo.FundFlowResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 区块链服务 Feign Client
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-blockchain-service")
public interface BlockchainServiceFeignClient {

    /**
     * 记录资金流转
     */
    @PostMapping("/blockchain/fundflow/record")
    CommonResponse<FundFlowResponseVO> recordFundFlow(@RequestBody CharityDonationRecordFundFlowInputBO inputBO);
}