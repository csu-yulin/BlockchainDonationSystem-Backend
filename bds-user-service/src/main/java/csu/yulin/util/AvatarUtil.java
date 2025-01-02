package csu.yulin.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * 头像工具类
 *
 * @author lp
 * @create 2025-01-02
 */
@Component
@RequiredArgsConstructor
public class AvatarUtil {
    private static final String RANDOM_AVATAR_API = "https://api.uomg.com/api/rand.avatar";
    private static final String[] SORT_OPTIONS = {"男", "女", "动漫男", "动漫女"};

    private final RestTemplate restTemplate;

    public String getRandomAvatar() {
        // 随机选择 sort 参数
        String sort = SORT_OPTIONS[new Random().nextInt(SORT_OPTIONS.length)];

        try {
            // 拼接 URL
            String requestUrl = RANDOM_AVATAR_API + "?sort=" + sort + "&format=json";

            // 强制将响应视为字符串
            String response = restTemplate.getForObject(requestUrl, String.class);

            // 检查响应内容是否有效
            AssertUtil.hasText(response, "获取随机头像失败");

            // 解析 JSON 响应
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            if (jsonObject.get("code").getAsInt() == 1) {
                return jsonObject.get("imgurl").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 默认头像
        return "https://p7.itc.cn/q_70/images03/20230309/bc24a67a4dea4ae38296967a4f8ecea5.png";
    }
}
