package csu.yulin.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csu.yulin.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 通用响应对象
 * 用于返回 Gateway 服务的统一响应格式
 *
 * @author lp
 * @create 2025-01-05
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 排除 null 值字段
public class CommonResponse<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    // 状态码
    private Integer code;
    // 提示信息
    private String message;
    // 数据内容
    private T data;

    /**
     * 构造成功响应
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 构造失败响应
     */
    public static <T> CommonResponse<T> error(ResultCode resultCode) {
        return new CommonResponse<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> CommonResponse<T> error(ResultCode resultCode, String customMessage) {
        return new CommonResponse<>(resultCode.getCode(), customMessage, null);
    }

    public static <T> CommonResponse<T> error(int code, String customMessage) {
        return new CommonResponse<>(code, customMessage, null);
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"code\":500,\"message\":\"JSON序列化失败\",\"data\":null}";
        }
    }
}
