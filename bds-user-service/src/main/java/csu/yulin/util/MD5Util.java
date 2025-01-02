package csu.yulin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

/**
 * MD5 工具类
 *
 * @author lp
 * @create 2025-01-02
 */
public class MD5Util {

    @Value("${MD5.SALT}")
    private static String salt;

    /**
     * 对字符串进行 MD5 加盐加密
     *
     * @param rawPassword 明文密码
     * @return 加密后的字符串
     */
    public static String encrypt(String rawPassword) {
        String saltedPassword = rawPassword + salt;
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes());
    }

    /**
     * 校验密码
     *
     * @param rawPassword       明文密码
     * @param encryptedPassword 数据库中的加密密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}
