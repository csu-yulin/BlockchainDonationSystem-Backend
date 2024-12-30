package csu.yulin.exception;

import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author lp
 * @create 2024-12-30
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResponse<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return new CommonResponse<>(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResponse<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return CommonResponse.error(ResultCode.INTERNAL_SERVER_ERROR, "系统异常，请联系管理员");
    }
} 