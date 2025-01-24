package cn.cisdigital.datakits.framework.cloud.alibaba.properties;

import cn.cisdigital.datakits.framework.cloud.alibaba.AlibabaStarterConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import okhttp3.ConnectionPool;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author xxx
 * @since 2024-12-10
 */
@Data
@Validated
@Accessors(chain = true)
@ConfigurationProperties(prefix = AlibabaStarterConstants.REST_TEMPLATE_PROPERTIES_PREFIX)
public class RestTemplateProperties implements Serializable {

    /**
     * {@link okhttp3.OkHttpClient.Builder#connectTimeout(Duration)}的连接超时时间(最小值10s))
     */
    @DurationMin(seconds = 10)
    private Duration connectTimeout;
    /**
     * {@link okhttp3.OkHttpClient.Builder#readTimeout(Duration)}的读超时时间(最小值10s)
     */
    @DurationMin(seconds = 10)
    private Duration readTimeout;
    /**
     * {@link okhttp3.OkHttpClient.Builder#writeTimeout(Duration)}的写超时时间(最小值10s)
     */
    @DurationMin(seconds = 10)
    private Duration writeTimeout;
    /**
     * {@link okhttp3.OkHttpClient.Builder#callTimeout(Duration)}的call超时时间(最小值60s)
     */
    @DurationMin(seconds = 60)
    private Duration callTimeout;
    /**
     * {@link okhttp3.OkHttpClient.Builder#retryOnConnectionFailure(boolean)}是否在连接失败时重试
     */
    @NotNull
    private Boolean retryOnConnectionFailure;
    /**
     * {@link ConnectionPool#ConnectionPool(int, long, TimeUnit)}的最大idle连接数(最小值1)
     */
    @Min(1)
    private Integer maxIdleConnections;
    /**
     * {@link ConnectionPool#ConnectionPool(int, long, TimeUnit)}的idle连接的最小保持时间(分钟)(最小值1m)
     */
    @Min(1)
    private Integer keepAliveMinutes;
    /**
     * {@link okhttp3.Dispatcher#setMaxRequests(int)}的最大请求数(最小值1)
     */
    @Min(1)
    private Integer maxRequests;
    /**
     * {@link okhttp3.Dispatcher#setMaxRequestsPerHost(int)}的每个host的最大请求数(最小值1)
     */
    @Min(1)
    private Integer maxRequestsPerHost;

    public RestTemplateProperties() {
        this.connectTimeout = Duration.ofSeconds(10);
        this.readTimeout = Duration.ofSeconds(10);
        this.writeTimeout = Duration.ofSeconds(10);
        this.callTimeout = Duration.ofSeconds(60);
        this.retryOnConnectionFailure = true;
        this.maxIdleConnections = 1;
        this.keepAliveMinutes = 5;
        this.maxRequests = 100;
        this.maxRequestsPerHost = 10;
    }
}
