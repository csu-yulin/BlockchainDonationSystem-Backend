package csu.yulin.service;

import csu.yulin.config.SystemConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务管理器
 * <p>负责管理 CharityDonationService 的实例</p>
 *
 * @author lp
 * @create 2025-03-26
 */
@Data
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ServiceManager {

    private final SystemConfig config;

    private final Client client;

    /**
     * CharityDonationService 管理 Map
     */
    @Bean("CharityDonationService")
    public Map<String, CharityDonationService> initCharityDonationServiceManager() {
        // 解析私钥列表
        List<String> hexPrivateKeyList = Optional.ofNullable(this.config.getHexPrivateKey())
                .map(keys -> Arrays.asList(keys.split(",")))
                .orElse(Collections.emptyList());

        if (hexPrivateKeyList.isEmpty()) {
            log.warn("未从配置文件读取到私钥列表，CharityDonationService 将不会被初始化！");
            return Collections.emptyMap();
        }

        Map<String, CharityDonationService> serviceMap = new ConcurrentHashMap<>(Math.max(hexPrivateKeyList.size(), 16));

        for (int i = 0; i < hexPrivateKeyList.size(); i++) {
            String privateKey = hexPrivateKeyList.get(i).trim();

            // 处理私钥前缀
            if (privateKey.startsWith("0x") || privateKey.startsWith("0X")) {
                privateKey = privateKey.substring(2);
            }

            if (privateKey.isBlank()) {
                log.warn("私钥列表中索引 {} 处的私钥为空，已跳过。", i);
                continue;
            }

            try {
                // 生成密钥对
                org.fisco.bcos.sdk.crypto.CryptoSuite cryptoSuite = new org.fisco.bcos.sdk.crypto.CryptoSuite(this.client.getCryptoType());
                org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair(privateKey);
                String userAddress = cryptoKeyPair.getAddress();

                log.debug("初始化 CharityDonationService: hexPrivateKeyList[{}]: {}, userAddress: {}", i, privateKey, userAddress);

                // 初始化 CharityDonationService
                CharityDonationService charityDonationService = new CharityDonationService();
                charityDonationService.setAddress(this.config.getContract().getCharityDonationAddress());
                charityDonationService.setClient(this.client);

                // 交易处理器
                org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor txProcessor =
                        org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory.createAssembleTransactionProcessor(this.client, cryptoKeyPair);
                charityDonationService.setTxProcessor(txProcessor);

                // 存入 Map
                serviceMap.put(userAddress, charityDonationService);
            } catch (Exception e) {
                log.error("初始化 CharityDonationService 失败，索引 [{}], 私钥 [{}]，错误信息: {}", i, privateKey, e.getMessage(), e);
            }
        }

        log.info("CharityDonationService 初始化完成，共创建 {} 个实例", serviceMap.size());
        return serviceMap;
    }
}
