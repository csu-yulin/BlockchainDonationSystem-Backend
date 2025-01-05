package csu.yulin.controller;

import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务降级处理
 *
 * @author lp
 * @create 2025-01-05
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/user")
    public CommonResponse<String> userFallback() {
        return CommonResponse.error(ResultCode.INTERNAL_SERVER_ERROR,
                "用户服务暂不可用，请稍后再试");
    }
}
