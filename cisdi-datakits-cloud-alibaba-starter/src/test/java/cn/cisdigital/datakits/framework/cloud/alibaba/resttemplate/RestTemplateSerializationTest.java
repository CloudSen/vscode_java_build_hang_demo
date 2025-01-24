package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate;

import cn.cisdigital.datakits.framework.cloud.alibaba.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.vo.TestVo;
import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author xxx
 * @since 2024-05-24
 */
@Slf4j
class RestTemplateSerializationTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    /**
     * Cisdi RestTemplate 默认只使用:<br/>
     * 1. {@link StringHttpMessageConverter}<br/>
     * 2. 包含框架统一object mapper配置的{@link MappingJackson2HttpMessageConverter}
     */
    @Test
    @DisplayName("正确加载message converter")
    void success_load_cisdi_message_converter() {
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            System.out.println("Converter: " + converter.getClass().getName());
        }

        assertThat(restTemplate.getMessageConverters()).hasSize(2);
        boolean cisdiConverterExists = restTemplate.getMessageConverters().stream()
                .map(HttpMessageConverter::getClass).map(Class::getName)
                .anyMatch(s -> s.contains("MappingJackson2HttpMessageConverter"));
        boolean stringConverterExists = restTemplate.getMessageConverters().stream()
                .map(HttpMessageConverter::getClass).map(Class::getName)
                .anyMatch(s -> s.contains("StringHttpMessageConverter"));

        assertThat(cisdiConverterExists).isTrue();
        assertThat(stringConverterExists).isTrue();

        assertThat(restTemplate.getMessageConverters().get(0))
                .isInstanceOf(StringHttpMessageConverter.class);
        assertThat(restTemplate.getMessageConverters().get(1))
                .isInstanceOf(MappingJackson2HttpMessageConverter.class);

        MappingJackson2HttpMessageConverter rtMsgConverter = (MappingJackson2HttpMessageConverter) restTemplate
                .getMessageConverters().get(1);
        assertThat(rtMsgConverter.getObjectMapper()).isEqualTo(JsonUtils.OBJECT_MAPPER);
    }

    @Test
    @DisplayName("getForObject，对特殊类型反序列化成功")
    void success_getForObject() {
        String jsonResponse = restTemplate.getForObject(
                "http://localhost:" + port + "/test/serialize",
                String.class);
        assertThat(jsonResponse).isNotNull();
        System.out.println("响应json" + jsonResponse);

        assertDoesNotThrow(() -> {
            ResVo<TestVo> res = JsonUtils.parseObject(jsonResponse, new TypeReference<ResVo<TestVo>>() {
            });
            System.out.println("响应：" + res);
            assertThat(res).isNotNull();
            res.setRequestId("test-request-id");
            assertThat(res).hasNoNullFieldsOrProperties();
        });

    }

    @Test
    @DisplayName("exchange，对特殊类型反序列化成功")
    void success_exchange() {
        ResponseEntity<ResVo<TestVo>> response = restTemplate.exchange(
                "http://localhost:" + port + "/test/serialize",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ResVo<TestVo>>() {
                });
        assertThat(response).isNotNull();
        ResVo<TestVo> res = response.getBody();
        System.out.println("响应：" + res);
        assertThat(res).isNotNull();
        res.setRequestId("test-request-id");
        assertThat(res).hasNoNullFieldsOrProperties();
    }
}
