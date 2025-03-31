package csu.yulin.exception;

import csu.yulin.enums.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @author lp
 * @create 2024-12-30
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
} 