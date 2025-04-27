package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.config.FeignConfig;
import csu.yulin.model.dto.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-project-service", configuration = {FeignConfig.class})
public interface ProjectServiceFeignClient {

    /**
     * 更新用户信息
     */
    @PutMapping("/project/update")
    CommonResponse<String> updateProject(@RequestBody ProjectDTO projectDTO);
}

