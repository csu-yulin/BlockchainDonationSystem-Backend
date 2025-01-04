package csu.yulin.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 头像工具类
 *
 * @author lp
 * @create 2025-01-02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarUtil {
    private static final String RANDOM_AVATAR_API = "https://api.uomg.com/api/rand.avatar";
    private static final String[] SORT_OPTIONS = {"男", "女", "动漫男", "动漫女"};
    private final OkHttpClient httpClient;

    public String getRandomAvatar() {
        // 随机选择 sort 参数
        String sort = SORT_OPTIONS[new Random().nextInt(SORT_OPTIONS.length)];

        // 默认头像 URL
        String defaultAvatar = "https://p7.itc.cn/q_70/images03/20230309/bc24a67a4dea4ae38296967a4f8ecea5.png";

        try {
            // 构建请求 URL
            String requestUrl = RANDOM_AVATAR_API + "?sort=" + sort + "&format=json";

            // 创建请求对象
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .get()
                    .build();

            // 执行请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    // 日志记录错误
                    System.err.println("请求失败，状态码: " + response.code());
                    return defaultAvatar;
                }

                // 解析响应
                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody == null) {
                    // 如果响应为空，返回默认头像
                    System.err.println("响应为空，返回默认头像");
                    return defaultAvatar;
                }

                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 1) {
                    return jsonObject.get("imgurl").getAsString();
                } else {
                    // 如果响应 JSON 格式不符合预期，返回默认头像
                    System.err.println("响应格式不符合预期，返回默认头像");
                    return defaultAvatar;
                }
            }
        } catch (Exception e) {
            // 捕获所有异常并返回默认头像
            log.error("获取随机头像失败：{}", e.getMessage());
            return defaultAvatar;
        }
    }
}
