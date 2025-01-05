package csu.yulin.exception;

import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局异常处理器
 *
 * @author lp
 * @create 2025-01-05
 */
@Slf4j
@Order(-1) // 优先级要高于默认的异常处理器
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 日志记录异常信息
        log.error("网关捕获异常：", ex);

        // 构建通用响应对象
        CommonResponse<Object> response;
        if (ex instanceof ResponseStatusException statusException) {
            response = CommonResponse.error(statusException.getStatus().value(), statusException.getReason());
        } else {
            response = CommonResponse.error(ResultCode.INTERNAL_SERVER_ERROR, "网关层发生未知错误，请联系管理员！");
        }

        // 设置响应头
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 返回响应体
        byte[] responseBody = response.toString().getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody)));
    }
}
