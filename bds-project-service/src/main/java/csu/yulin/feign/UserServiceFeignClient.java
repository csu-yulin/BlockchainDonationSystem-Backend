package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient("bds-user-service")
public interface UserServiceFeignClient {

    @GetMapping("/user/{userId}/canCreateProject")
    CommonResponse<Map<String, Object>> canCreateProject(@PathVariable Long userId);
}
