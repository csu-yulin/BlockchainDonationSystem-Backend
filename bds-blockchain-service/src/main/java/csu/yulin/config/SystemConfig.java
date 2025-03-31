package csu.yulin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 系统配置
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemConfig {

    private String peers;

    private int groupId = 1;

    private List<String> certPath;

    private String hexPrivateKey;

    @NestedConfigurationProperty
    private ContractConfig contract;

}
