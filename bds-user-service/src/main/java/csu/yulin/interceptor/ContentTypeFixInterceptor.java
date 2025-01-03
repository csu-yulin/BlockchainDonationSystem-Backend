package csu.yulin.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 修复非法 Content-Type 拦截器
 *
 * @author lp
 * @create 2025-01-02
 */
public class ContentTypeFixInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NotNull ClientHttpResponse intercept(@NotNull HttpRequest request,
                                                 @NotNull byte[] body,
                                                 ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        // 修复非法 Content-Type
        HttpHeaders headers = response.getHeaders();
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && contentType.contains(",")) {
            // 替换非法的 ',' 为合法的分号 ';'
            headers.set(HttpHeaders.CONTENT_TYPE, contentType.replace(",", ";"));
        }

        return response;
    }
}
