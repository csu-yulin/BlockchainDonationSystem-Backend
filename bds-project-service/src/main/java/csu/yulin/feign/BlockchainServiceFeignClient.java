package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.dto.CharityDonationCreateProjectInputDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-blockchain-service")
public interface BlockchainServiceFeignClient {

    /**
     * 捐款
     */
    @PostMapping("/blockchain/project/create")
    CommonResponse<Map<String, Object>> createProject(@RequestBody CharityDonationCreateProjectInputDTO inputDTO);
}
