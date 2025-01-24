package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.hutool.core.map.CaseInsensitiveTreeMap;
import cn.hutool.core.map.MapUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static cn.cisdigital.datakits.framework.common.constant.HeaderConstants.LOG_IGNORE_HEADERS;

/**
 * 将feign请求转换为curl, 便于重放请求及格式化请求头和参数
 *
 * @author xxx
 * @since 2023/3/30
 */
@Slf4j
@Component
@RefreshScope
public class FeignRequestLogInterceptor implements RequestInterceptor {

    @Value("${logging.disable-feign-curl:false}")
    private boolean disableCurl;

    static {
        log.info("[ Feign请求拦截器] CURL日志打印已加载.");
    }

    @Override
    public void apply(RequestTemplate template) {
        if (disableCurl) {
            return;
        }
        String method = template.method();
        String path = template.path();

        Target<?> target = template.feignTarget();
        String url = target.url();
        String name = target.name();

        Map<String, Collection<String>> queries = template.queries();

        Map<String, Collection<String>> headers = getHeaders(template);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            stringBuilder.append("curl --location --request ").append(method).append(" '").append(url).append(path);

            // query param
            boolean isFirstParam = true;
            for (Entry<String, Collection<String>> entry : queries.entrySet()) {
                for (String value : entry.getValue()) {
                    String key = entry.getKey();
                    if (isFirstParam) {
                        stringBuilder.append("?");
                        isFirstParam = false;
                    } else {
                        stringBuilder.append("&");
                    }
                    stringBuilder.append(key).append("=").append(value);
                }
            }
            stringBuilder.append("'");

            boolean endWithNewLine = false;

            // header
            if (MapUtil.isNotEmpty(headers)) {
                stringBuilder.append(" \\\n");
                headers.forEach((key, value) -> stringBuilder.append("     --header '").append(key).append(": ")
                        .append(String.join(",", value)).append("'").append(" \\\n"));
                endWithNewLine = true;
            }

            // body
            if (template.body() != null) {
                if (!endWithNewLine) {
                    stringBuilder.append(" \\\n");
                }
                String body = new String(template.body());
                stringBuilder.append("     --data '").append(body).append("'\n");
            }

            log.info("converting curl of feign({}) succeeded, copy to reply: \n{}", name, stringBuilder);
        } catch (Exception e) {
            log.warn("converting curl of feign({}) failed. incomplete curl: \n{}", name, stringBuilder, e);
        }
    }

    private Map<String, Collection<String>> getHeaders(RequestTemplate template) {
        Map<String, Collection<String>> headers = new CaseInsensitiveTreeMap<>(template.headers());
        LOG_IGNORE_HEADERS.forEach(headers::remove);
        return headers;
    }
}
