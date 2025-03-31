package csu.yulin.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Redis 测试启动类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisTestRunner {

    private final RedisUtil redisUtil;

    @Bean
    public ApplicationRunner testRedis() {
        return args -> {
            log.info("========== Redis 测试开始 ==========");

            // 测试 set 和 get
            redisUtil.set("testKey", "Hello Redis!", 60, TimeUnit.SECONDS);
            String value = (String) redisUtil.get("testKey");
            log.info("testKey: {}", value);

            // 测试过期时间
            long expire = redisUtil.getExpire("testKey");
            log.info("testKey 过期时间: {} 秒", expire);

            // 测试是否存在
            boolean exists = redisUtil.hasKey("testKey");
            log.info("testKey 是否存在: {}", exists);

            // 测试自增
            redisUtil.set("counter", 1, 0, TimeUnit.SECONDS);
            redisUtil.increment("counter", 5);
            log.info("counter: {}", redisUtil.get("counter"));

            // 测试 Hash 操作
            redisUtil.hSet("user:1", "name", "Alice");
            log.info("user:1 name: {}", redisUtil.hGet("user:1", "name"));

            // 测试列表操作
            redisUtil.lPush("taskQueue", "task1");
            redisUtil.lPush("taskQueue", "task2");
            log.info("taskQueue rPop: {}", redisUtil.rPop("taskQueue"));

            log.info("========== Redis 测试完成 ==========");
        };
    }
}
