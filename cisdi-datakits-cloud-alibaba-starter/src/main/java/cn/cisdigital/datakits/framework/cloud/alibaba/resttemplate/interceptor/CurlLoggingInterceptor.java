package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author xxx
 * @since 2024-12-10
 */
@Slf4j
@RequiredArgsConstructor
public class CurlLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logCurl(request, body);
        return execution.execute(request, body);
    }

    private void logCurl(HttpRequest request, byte[] body) {
        StringBuilder curlCommand = new StringBuilder("curl -X " + request.getMethod() + " '" + request.getURI() + "'");

        // 添加请求头
        request.getHeaders().forEach((key, values) ->
            curlCommand.append(" -H '").append(key).append(": ").append(String.join(", ", values)).append("'")
        );

        // 根据 Content-Type 决定 body 的打印格式
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

        if (contentType != null) {
            switch (contentType) {
                case MediaType.APPLICATION_FORM_URLENCODED_VALUE:
                    curlCommand.append(" --data-urlencode '")
                        .append(new String(body, StandardCharsets.UTF_8))
                        .append("'");
                    break;
                case MediaType.MULTIPART_FORM_DATA_VALUE:
                    String bodyString = new String(body, StandardCharsets.UTF_8);
                    Arrays.stream(bodyString.split("&")).forEach(param -> {
                        String[] keyValue = param.split("=", 2);
                        if (keyValue.length == 2) {
                            curlCommand.append(" --form '").append(keyValue[0]).append("=").append(keyValue[1]).append("'");
                        }
                    });
                    break;
                case MediaType.APPLICATION_JSON_VALUE:
                    curlCommand.append(" -d '").append(new String(body, StandardCharsets.UTF_8)).append("'");
                    break;
                default:
                    if (body.length > 0) {
                        curlCommand.append(" --data '").append(new String(body, StandardCharsets.UTF_8)).append("'");
                    }
                    break;
            }
        }

        log.info("Generated curl: {}", curlCommand);
    }
}
