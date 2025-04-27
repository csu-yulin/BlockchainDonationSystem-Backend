package csu.yulin.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import csu.yulin.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应对象
 *
 * @author lp
 * @create 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 排除 null 值字段
public class CommonResponse<T> {
    // 状态码
    private Integer code;
    // 提示信息
    private String message;
    // 泛型数据
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResponse<T> error(ResultCode resultCode) {
        return new CommonResponse<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> CommonResponse<T> error(ResultCode resultCode, String customMessage) {
        return new CommonResponse<>(resultCode.getCode(), customMessage, null);
    }
}
