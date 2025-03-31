package csu.yulin.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * @author lp
 * @create 2025-01-02
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置键值对
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间（秒）。如果 time <= 0，则不过期。
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        if (time <= 0) {
            // 如果时间小于等于0，则不设置过期时间
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        }
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     *
     * @param key 键
     * @return 是否成功删除
     */
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key  键
     * @param time 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean expire(String key, long time) {
        if (time > 0) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return false;
    }

    /**
     * 获取键的过期时间
     *
     * @param key 键
     * @return 过期时间（秒）。返回 -1 表示永久有效，-2 表示键不存在。
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 自增（计数器）
     *
     * @param key   键
     * @param delta 增量（必须大于 0）
     * @return 自增后的值
     */
    public long increment(String key, long delta) {
        if (delta <= 0) {
            throw new IllegalArgumentException("增量必须大于 0");
        }
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return Objects.requireNonNullElse(result, 0L);
    }

    /**
     * 自减
     *
     * @param key   键
     * @param delta 减量（必须大于 0）
     * @return 自减后的值
     */
    public long decrement(String key, long delta) {
        if (delta <= 0) {
            throw new IllegalArgumentException("减量必须大于 0");
        }
        Long result = redisTemplate.opsForValue().increment(key, -delta);
        return Objects.requireNonNullElse(result, 0L);
    }

    /**
     * 设置 Hash 值
     *
     * @param key     键
     * @param hashKey Hash 的字段
     * @param value   值
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取 Hash 值
     *
     * @param key     键
     * @param hashKey Hash 的字段
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除 Hash 的字段
     *
     * @param key     键
     * @param hashKey Hash 的字段
     */
    public void hDelete(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 推入列表（从左侧）
     *
     * @param key   键
     * @param value 值
     */
    public void lPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 弹出列表（从右侧）
     *
     * @param key 键
     * @return 值
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
}
