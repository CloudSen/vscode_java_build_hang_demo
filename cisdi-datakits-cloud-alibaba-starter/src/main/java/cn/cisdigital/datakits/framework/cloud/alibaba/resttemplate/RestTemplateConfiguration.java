package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate;

import cn.cisdigital.datakits.framework.cloud.alibaba.convertor.RestTemplatePropertiesConvertor;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.CustomHttpProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RestTemplateProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.interceptor.CurlLoggingInterceptor;
import cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.interceptor.CustomLoadBalancerInterceptor;
import cn.cisdigital.datakits.framework.web.mvc.MvcAutoConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024-12-10
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RestTemplateProperties.class)
@RequiredArgsConstructor
@AutoConfigureAfter(MvcAutoConfiguration.class)
public class RestTemplateConfiguration {

    private final MeterRegistry meterRegistry;
    private final LoadBalancerClient loadBalancerClient;
    private final MappingJackson2HttpMessageConverter cisdiMsgConverter;
    private final RestTemplateProperties restTemplateProperties;

    private final static String HTTP_METRIC_NAME = "okhttp_client";
    private final static String LOAD_BALANCED_REST_TEMPLATE_METRIC = "loadBalancedRestTemplate_metric";
    public static final String MODULE = "module";

    /**
     * 基于okhttp的，兼容负载均衡和host调用的RestTemplate
     */
    @Bean
    public RestTemplate loadBalancedRestTemplate() {
        ClientHttpRequestFactory factory = httpRequestFactory(this.restTemplateProperties, Collections.singleton(LOAD_BALANCED_REST_TEMPLATE_METRIC));
        RestTemplate restTemplate = new RestTemplate(factory);
        // 注册拦截器
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new CurlLoggingInterceptor());
        interceptors.add(new CustomLoadBalancerInterceptor(loadBalancerClient, factory));
        restTemplate.setInterceptors(interceptors);
        // rest template禁用uri编码能力，uri的编码让用户控制
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        restTemplate.setMessageConverters(Arrays.asList(new StringHttpMessageConverter(), cisdiMsgConverter));
        return restTemplate;
    }

    public ClientHttpRequestFactory httpRequestFactory(RestTemplateProperties restTemplateProperties, Set<String> tags) {
        CustomHttpProperties customHttpProperties = RestTemplatePropertiesConvertor.convertToCustomHttpProperties(restTemplateProperties);
        OkHttpMetricsEventListener okHttpMetricsEventListener = okHttpMetricsEventListener(tags);
        return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient(customHttpProperties, okHttpMetricsEventListener));
    }

    public OkHttpClient okHttpConfigClient(CustomHttpProperties httpProperties, EventListener eventListener) {
        return new OkHttpClient().newBuilder()
            .dispatcher(dispatcher(httpProperties.getMaxRequests(), httpProperties.getMaxRequestsPerHost()))
            .connectionPool(pool(httpProperties.getMaxIdleConnections(), httpProperties.getKeepAliveMinutes()))
            .connectTimeout(httpProperties.getConnectTimeout())
            .readTimeout(httpProperties.getReadTimeout())
            .writeTimeout(httpProperties.getWriteTimeout())
            .retryOnConnectionFailure(httpProperties.getRetryOnConnectionFailure())
            .callTimeout(httpProperties.getCallTimeout())
            .eventListener(eventListener)
            .build();
    }

    public ConnectionPool pool(int maxIdleConnections, int keepAliveMinutes) {
        return new ConnectionPool(maxIdleConnections, keepAliveMinutes, TimeUnit.MINUTES);
    }

    public Dispatcher dispatcher(int maxRequests, int maxRequestsPerHost) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
        dispatcher.setIdleCallback(() -> log.info("okhttp dispatcher is idle now!"));
        return dispatcher;
    }

    public OkHttpMetricsEventListener okHttpMetricsEventListener(Set<String> tags) {
        Assert.notNull(tags, "tags must not be null");
        List<Tag> tagList = tags.stream().map(tagValue -> Tag.of(MODULE, tagValue))
            .collect(Collectors.toList());
        return OkHttpMetricsEventListener.builder(meterRegistry, HTTP_METRIC_NAME)
            .tags(tagList)
            .build();
    }
}
