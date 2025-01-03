package csu.yulin.util;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 身份认证工具类
 *
 * @author lp
 * @create 2025-01-03
 */
@Component
public class IdentityVerificationUtil {
    private static final String URL = "https://idenauthen.market.alicloudapi.com/idenAuthentication";
    private final OkHttpClient client = new OkHttpClient();
    @Value("${identity-verification.app-code}")
    private String appCode;


    public String verifyIdentity(String name, String idNo) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("idNo", idNo)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "APPCODE " + appCode)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码: " + response.code() + ", 消息: " + response.message());
            }

            return response.body() != null ? response.body().string() : null;
        }
    }
}
