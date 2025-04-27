package csu.yulin.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法工具类
 *
 * @author lp
 * @create 2025-01-02
 */
public class SnowflakeUtil {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    public static String generateId() {
        return SNOWFLAKE.nextIdStr();
    }
}
