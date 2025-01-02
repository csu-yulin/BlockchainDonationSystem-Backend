package csu.yulin.constants;

/**
 * Redis Key 常量类
 *
 * @author lp
 * @create 2025-01-02
 */
public final class RedisKeyConstants {

    /**
     * 短信验证码 Redis Key 前缀
     */
    public static final String SMS_CODE_PREFIX = "sms:code:";
    /**
     * 图形验证码 Redis Key 前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    private RedisKeyConstants() {
        // 私有化构造器，防止实例化
    }
}
