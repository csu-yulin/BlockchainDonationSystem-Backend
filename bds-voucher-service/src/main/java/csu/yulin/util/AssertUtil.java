package csu.yulin.util;

import csu.yulin.enums.ResultCode;
import csu.yulin.exception.BusinessException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 断言工具类，用于抛出 BusinessException 异常
 *
 * @author lp
 * @create 2024-12-30
 */
public class AssertUtil {

    /**
     * 断言对象不为空
     */
    public static void notNull(Object obj, ResultCode resultCode, String message) {
        if (obj == null) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言对象不为空
     */
    public static void notNull(Object obj, String message) {
        notNull(obj, ResultCode.BAD_REQUEST, message);
    }

    /**
     * 断言字符串不为空
     */
    public static void hasText(String text, ResultCode resultCode, String message) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言字符串不为空或空白
     */
    public static void notBlank(String str, ResultCode resultCode, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言字符串不为空
     */
    public static void hasText(String text, String message) {
        hasText(text, ResultCode.BAD_REQUEST, message);
    }

    /**
     * 断言集合不为空
     */
    public static void notEmpty(Collection<?> collection, ResultCode resultCode, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言集合不为空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, ResultCode.BAD_REQUEST, message);
    }

    /**
     * 断言表达式为真
     */
    public static void isTrue(boolean expression, ResultCode resultCode, String message) {
        if (!expression) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言表达式为真
     */
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, ResultCode.BAD_REQUEST, message);
    }

    /**
     * 断言表达式为假
     */
    public static void isFalse(boolean expression, ResultCode resultCode, String message) {
        if (expression) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 断言表达式为假
     */
    public static void isFalse(boolean expression, String message) {
        isFalse(expression, ResultCode.BAD_REQUEST, message);
    }

    /**
     * 直接抛出业务异常
     */
    public static void fail(ResultCode resultCode, String message) {
        throw new BusinessException(resultCode, message);
    }

    /**
     * 直接抛出业务异常
     */
    public static void fail(String message) {
        fail(ResultCode.BAD_REQUEST, message);
    }
} 