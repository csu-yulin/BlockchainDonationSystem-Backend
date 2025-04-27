package csu.yulin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户启动类
 *
 * @author lp
 * @create 2025-01-07
 */
@SpringBootApplication
@MapperScan("csu.yulin.mapper")
@EnableFeignClients
public class FundFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(FundFlowApplication.class, args);
    }
}
