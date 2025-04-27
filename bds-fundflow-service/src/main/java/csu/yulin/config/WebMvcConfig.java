package csu.yulin.config;

import csu.yulin.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author lp
 * @create 2024-12-30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLogInterceptor())
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除错误页面的请求
                .excludePathPatterns("/error");
    }
} 