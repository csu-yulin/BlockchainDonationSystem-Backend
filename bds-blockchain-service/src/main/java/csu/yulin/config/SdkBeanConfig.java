package csu.yulin.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK Bean 配置类
 * <p>用于初始化 FISCO BCOS 区块链客户端</p>
 *
 * @author lp
 * @create 2025-03-26
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SdkBeanConfig {

    private final SystemConfig config;

    /**
     * 初始化 FISCO BCOS 客户端
     */
    @Bean
    public Client client() throws Exception {
        List<String> certPaths = config.getCertPath();
        Exception lastException = null;

        // 遍历所有可用的证书路径，尝试建立连接
        for (String certPath : certPaths) {
            try {
                log.info("尝试使用证书路径 {} 初始化区块链客户端...", certPath);

                ConfigProperty property = new ConfigProperty();
                // 配置网络参数和证书路径
                configNetwork(property);
                configCryptoMaterial(property, certPath);

                ConfigOption configOption = new ConfigOption(property);
                Client client = new BcosSDK(configOption).getClient(config.getGroupId());

                // 获取当前区块高度，验证链路是否正常
                BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
                log.info("区块链连接成功，当前区块高度: {}", blockNumber);
                // 配置加密密钥对
                configCryptoKeyPair(client);
                log.info("国密模式: {}，钱包地址: {}",
                        client.getCryptoSuite().cryptoTypeConfig == 1,
                        client.getCryptoSuite().getCryptoKeyPair().getAddress());

                return client;
            } catch (Exception ex) {
                log.error("使用证书路径 {} 连接失败，错误信息: {}", certPath, ex.getMessage(), ex);
                lastException = ex;
            }
        }

        // 尝试所有证书路径均失败，抛出异常
        throw new ConfigException("无法连接到区块链节点: " + config.getPeers(), lastException);
    }

    /**
     * 配置区块链网络信息
     */
    private void configNetwork(ConfigProperty configProperty) {
        Map<String, Object> networkConfig = new HashMap<>();
        // 解析节点地址并存入配置
        networkConfig.put("peers", List.of(config.getPeers().split(",")));
        configProperty.setNetwork(networkConfig);
    }

    /**
     * 配置加密材料（证书路径）
     */
    private void configCryptoMaterial(ConfigProperty configProperty, String certPath) {
        Map<String, Object> cryptoMaterials = new HashMap<>();
        // 设置证书路径
        cryptoMaterials.put("certPath", certPath);
        configProperty.setCryptoMaterial(cryptoMaterials);
    }

    /**
     * 配置密钥对
     */
    private void configCryptoKeyPair(Client client) {
        String hexPrivateKey = config.getHexPrivateKey();
        if (hexPrivateKey == null || hexPrivateKey.isEmpty()) {
            // 如果未提供私钥，则生成新的密钥对
            client.getCryptoSuite().setCryptoKeyPair(client.getCryptoSuite().createKeyPair());
            return;
        }

        // 解析多个私钥，仅使用第一个
        String privateKey = hexPrivateKey.contains(",") ? hexPrivateKey.split(",")[0] : hexPrivateKey;

        // 处理 16 进制私钥前缀（去掉 "0x" 或 "0X"）
        if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
            privateKey = privateKey.substring(2);
        }

        // 使用指定私钥创建密钥对
        client.getCryptoSuite().setCryptoKeyPair(client.getCryptoSuite().createKeyPair(privateKey));
    }
}
