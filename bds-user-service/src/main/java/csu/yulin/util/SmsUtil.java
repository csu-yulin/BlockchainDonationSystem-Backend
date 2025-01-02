package csu.yulin.util;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import csu.yulin.constants.RedisKeyConstants;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 短信工具类
 *
 * @author lp
 * @create 2025-01-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsUtil {

    private static AsyncClient client;

    private final RedisUtil redisUtil;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signName}")
    private String signName;

    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    /**
     * 初始化短信客户端
     */
    @PostConstruct
    public void init() {
        // 配置凭据认证信息
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        // 配置客户端
        client = AsyncClient.builder()
                .region("cn-qingdao")
                .credentialsProvider(provider)
                .overrideConfiguration(ClientOverrideConfiguration.create()
                        .setEndpointOverride("dysmsapi.aliyuncs.com"))
                .build();
        log.info("短信客户端初始化成功");
    }

    /**
     * 关闭短信客户端
     */
    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
            log.info("短信客户端已关闭");
        }
    }

    /**
     * 发送短信验证码
     */
    public void sendSms(String phoneNumber) {
        // 设置 API 请求的参数
        String code = generateVerificationCode();
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(signName)
                .templateCode(templateCode)
                .phoneNumbers(phoneNumber)
                .templateParam("{\"code\":\"" + code + "\"}")
                .build();
        try {
            // 异步获取 API 请求的返回值
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse resp = response.get();

            // 检查返回值
            AssertUtil.isTrue("OK".equals(resp.getBody().getCode()),
                    "短信发送失败: " + resp.getBody().getMessage());

            // 存入 Redis，设置5分钟过期
            String redisKey = RedisKeyConstants.SMS_CODE_PREFIX + phoneNumber;
            redisUtil.set(redisKey, code, 5, TimeUnit.MINUTES);
            log.info("短信发送成功，手机号: {}, 验证码: {}", phoneNumber, code);
        } catch (Exception e) {
            throw new RuntimeException("发送短信验证码失败", e);
        }
    }

    /**
     * 生成随机验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
