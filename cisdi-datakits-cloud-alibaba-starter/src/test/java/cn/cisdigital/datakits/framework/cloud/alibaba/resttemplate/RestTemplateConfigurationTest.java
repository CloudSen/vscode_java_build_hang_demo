package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate;

import cn.cisdigital.datakits.framework.cloud.alibaba.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.cloud.alibaba.convertor.RestTemplatePropertiesConvertor;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.CustomHttpProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RestTemplateProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Collections;

/**
 * @author xxx
 * @since 2025-01-08
 */
@Slf4j
class RestTemplateConfigurationTest extends BaseIntegrationTest {

    @Autowired
    private RestTemplateConfiguration restTemplateConfiguration;
    @Autowired
    private RestTemplateProperties restTemplateProperties;


    /**
     * 测试自定义readTime超时时间是否生效
     * 该url是一个等待15s才会返回结果的接口，<a href="https://qa-platform.cisdigital.cn/dataKitsOpen/data-service/openApi/custom/1848275344986955777/sleep15second">...</a>
     */
    @ParameterizedTest
    @ValueSource(ints = {16})
    void testReadTimeWithoutException(int readTimeoutSeconds) {
        CustomHttpProperties customHttpProperties = RestTemplatePropertiesConvertor.convertToCustomHttpProperties(restTemplateProperties);
        customHttpProperties.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
        OkHttpClient client = restTemplateConfiguration.okHttpConfigClient(customHttpProperties, restTemplateConfiguration.okHttpMetricsEventListener(Collections.emptySet()));
        Request request = buildRequest();
        long startTime = System.currentTimeMillis();
        Assertions.assertDoesNotThrow(() -> {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody body = response.body();
                assert body != null;
                System.out.println(body.string());
            }
        });
        // 校验超时时间是否符合要求
        long spendTime = System.currentTimeMillis() - startTime;
        System.out.println("cost time: " + spendTime + " ms");
        boolean result = spendTime < (readTimeoutSeconds + 1) * 1000L;
        Assertions.assertTrue(result);
    }


    /**
     * 测试自定义readTime超时时间是否生效
     * 该url是一个等待15s才会返回结果的接口，<a href="https://qa-platform.cisdigital.cn/dataKitsOpen/data-service/openApi/custom/1848275344986955777/sleep15second">...</a>
     */
    @ParameterizedTest
    @ValueSource(ints = {3})
    void testReadTimeWithException(int readTimeoutSeconds) {
        // 默认值配置readTime=10s，手动设置为3s，检查超时时间是否是3s左右
        CustomHttpProperties customHttpProperties = RestTemplatePropertiesConvertor.convertToCustomHttpProperties(restTemplateProperties);
        customHttpProperties.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
        OkHttpClient client = restTemplateConfiguration.okHttpConfigClient(customHttpProperties, restTemplateConfiguration.okHttpMetricsEventListener(Collections.emptySet()));
        Request request = buildRequest();
        long startTime = System.currentTimeMillis();
        Assertions.assertThrows(SocketTimeoutException.class, () -> {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody body = response.body();
                assert body != null;
                System.out.println(body.string());
            }
        });
        // 校验超时时间是否符合要求
        long spendTime = System.currentTimeMillis() - startTime;
        System.out.println("cost time: " + spendTime + " ms");
        boolean result = spendTime > readTimeoutSeconds * 1000L && spendTime < (readTimeoutSeconds + 1) * 1000L;
        Assertions.assertTrue(result);
    }


    /**
     * 默认值配置connectTime=10s，手动设置为3s，检查超时时间是否是3s左右
     */
    @ParameterizedTest
    @ValueSource(ints = {3})
    void testConnectTimeWithException(int connectTimeoutSeconds) {
        CustomHttpProperties customHttpProperties = RestTemplatePropertiesConvertor.convertToCustomHttpProperties(restTemplateProperties);
        customHttpProperties.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        OkHttpClient client = restTemplateConfiguration.okHttpConfigClient(customHttpProperties, restTemplateConfiguration.okHttpMetricsEventListener(Collections.emptySet()));
        Request request = new Request.Builder().url("http://127.0.0.12:8080").build();
        long startTime = System.currentTimeMillis();
        Assertions.assertThrows(SocketTimeoutException.class, () -> {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody body = response.body();
                assert body != null;
                System.out.println(body.string());
            }
        });
        // 校验超时时间是否符合要求
        long spendTime = System.currentTimeMillis() - startTime;
        System.out.println("cost time: " + spendTime + " ms");
        boolean result = spendTime > connectTimeoutSeconds * 1000L && spendTime < (connectTimeoutSeconds + 1) * 1000L;
        Assertions.assertTrue(result);
    }


    private static Request buildRequest() {
        return new Request.Builder().get()
            .url("https://qa-platform.cisdigital.cn/dataKitsOpen/data-service/openApi/custom/1848275344986955777/sleep15second")
            .header("Content-Type", "application/json")
            .header("x-c-authorization", "authType=SIMPLE,appKey=1876837410002046977")
            .build();
    }


}
