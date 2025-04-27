package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-user-service")
public interface UserServiceFeignClient {

    /**
     * 更新个体用户信息
     */
    @PutMapping("/user/update/individual")
    CommonResponse<User> updateIndividualUser(@RequestBody UserDTO userDTO);
}
