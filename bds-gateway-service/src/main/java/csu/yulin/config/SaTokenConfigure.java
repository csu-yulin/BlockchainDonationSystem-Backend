package csu.yulin.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import csu.yulin.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限认证的配置器
 *
 * @author lp
 * @create 2025-01-05
 */
@Slf4j
@Configuration
public class SaTokenConfigure {

    /**
     * 注册 Sa-Token全局过滤器
     *
     * @return SaReactorFilter
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路径
                .addInclude("/**")
                // 放行路径：登录、注册、验证码等接口
                .addExclude(
                        "/user/login/phone",
                        "/user/register/individual",
                        "/user/register/organization",
                        "/user/captcha",
                        "/user/validateCaptcha",
                        "/user/smsCode"
                )
                // 鉴权逻辑
                .setAuth(obj -> {
                    // 打印当前访问的路径
                    log.info("-------- 前端访问path：{}", SaHolder.getRequest().getRequestPath());

                    // 校验登录：拦截除放行路径外的所有接口
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                })
                // 异常处理逻辑
                .setError(e -> {
                    // 返回错误信息
                    return "{\"code\": " + ResultCode.UNAUTHORIZED.getCode() + ", \"message\": \"" + ResultCode.UNAUTHORIZED.getMessage() + "\"}";
                });
    }
}
