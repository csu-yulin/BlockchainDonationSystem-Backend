package csu.yulin.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
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
                        "/user/login/email",
                        "/user/register/individual",
                        "/user/register/organization",
                        "/user/captcha",
                        "/user/validateCaptcha",
                        "/user/smsCode",
                        "/user/count",
                        "/donation/amount",
                        "/project/count",
                        "/donation/notify"
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
                })// 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {

                    // 获得客户端domain
                    SaRequest request = SaHolder.getRequest();
                    String origin = request.getHeader("Origin");
                    if (origin == null) {
                        origin = request.getHeader("Referer");
                    }

                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许第三方 Cookie
                            .setHeader("Access-Control-Allow-Credentials", "true")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", origin)
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEAD,PUT")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, X-Requested-With,satoken")
                            .setHeader("Access-Control-Allow-Credentials", "true")
                            .setHeader("Access-Control-Max-Age", "3600")
                    ;

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
                            .back();
                })
                ;

    }
}
