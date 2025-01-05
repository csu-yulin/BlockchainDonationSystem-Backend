package csu.yulin.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 限流器配置
 *
 * @author lp
 * @create 2025-01-05
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 基于客户端 IP 地址的限流器
     *
     * @return KeyResolver
     */
    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }
}
