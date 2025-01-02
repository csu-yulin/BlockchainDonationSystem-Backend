package csu.yulin.config;

import csu.yulin.interceptor.ContentTypeFixInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lp
 * @create 2025-01-02
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 添加拦截器
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(new ContentTypeFixInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}
