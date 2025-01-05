package csu.yulin.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 日志过滤器
 *
 * @author lp
 * @create 2025-01-05
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求信息
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();
        String method = exchange.getRequest().getMethodValue();
        String queryParams = exchange.getRequest().getQueryParams().toString();
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = exchange.getRequest().getHeaders().getFirst("Host");
        }

        Instant start = Instant.now();

        // 打印请求日志
        log.info("请求进入: [客户端IP: {}, 请求方法: {}, 路径: {}, 参数: {}]",
                clientIp, method, requestPath, queryParams);

        // 调用后续过滤器
        String finalClientIp = clientIp;
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    // 获取响应信息
                    Instant end = Instant.now(); // 结束时间
                    Duration duration = Duration.between(start, end);
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 500;

                    // 打印响应日志
                    log.info("响应返回: [客户端IP: {}, 路径: {}, 状态: {}, 耗时: {}ms]",
                            finalClientIp, requestPath, statusCode, duration.toMillis());

                })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
