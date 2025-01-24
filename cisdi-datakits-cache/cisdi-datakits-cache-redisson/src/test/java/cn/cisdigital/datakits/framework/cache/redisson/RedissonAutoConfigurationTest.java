package cn.cisdigital.datakits.framework.cache.redisson;

import cn.cisdigital.datakits.framework.cache.abs.CacheAutoConfiguration;
import cn.cisdigital.datakits.framework.cache.abs.CacheStarterProperties;
import cn.cisdigital.datakits.framework.cache.redisson.customizer.SerializerCustomizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

/**
 * @author xxx
 * @since 2024-12-10
 */
public class RedissonAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class))
            .withUserConfiguration(CacheStarterProperties.class)
            .withPropertyValues("spring.redis.redisson.config=\n" +
                    "        {\"threads\": ${app.redis.threads:1},\n" +
                    "        \"nettyThreads\": ${app.redis.netty-threads:3},\n" +
                    "        \"singleServerConfig\":{\n" +
                    "          \"idleConnectionTimeout\": ${app.redis.idle-connection-timeout:60000},\n" +
                    "          \"connectTimeout\": ${app.redis.connect-timeout:30000},\n" +
                    "          \"timeout\": \"${app.redis.timeout:30000}\",\n" +
                    "          \"retryAttempts\": 1,\n" +
                    "          \"retryInterval\": 1000,\n" +
                    "          \"password\": \"UecV8s0XxBuXJu8D\",\n" +
                    "          \"subscriptionsPerConnection\": 1,\n" +
                    "          \"clientName\": \"${spring.application.name}-redisson\",\n" +
                    "          \"address\": \"redis://10.106.253.24:6379\",\n" +
                    "          \"subscriptionConnectionMinimumIdleSize\": ${app.redis.sub-connection-mini-idle-size:1},\n" +
                    "          \"subscriptionConnectionPoolSize\": ${app.redis.sub-connection-pool-size:5},\n" +
                    "          \"connectionMinimumIdleSize\": ${app.redis.connection-mini-idle-size:1},\n" +
                    "          \"connectionPoolSize\": ${app.redis.connection-pool-size:5},\n" +
                    "          \"database\": ${app.redis.database:0},\n" +
                    "          \"dnsMonitoringInterval\": 5000}\n" +
                    "        }");

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load_without_cache_manager(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .withPropertyValues("datakits.default-config.cache.enable-cache-manager=false")
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheManager.class);
                    Assertions.assertThat(context.getBean(CacheManager.class)).isExactlyInstanceOf(NoOpCacheManager.class);
                    Assertions.assertThat(context).hasSingleBean(SerializerCustomizer.class);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load_without_cache_manager2(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheManager.class);
                    Assertions.assertThat(context.getBean(CacheManager.class)).isExactlyInstanceOf(NoOpCacheManager.class);
                    Assertions.assertThat(context).hasSingleBean(SerializerCustomizer.class);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load_without_any_config_without_cache_manager(String flag) {
        runner
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheManager.class);
                    Assertions.assertThat(context.getBean(CacheManager.class)).isExactlyInstanceOf(NoOpCacheManager.class);
                    Assertions.assertThat(context).hasSingleBean(SerializerCustomizer.class);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load_with_cache_manager(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .withPropertyValues("datakits.default-config.cache.enable-cache-manager=" + flag)
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheManager.class);
                    Assertions.assertThat(context.getBean(CacheManager.class)).isExactlyInstanceOf(RedissonSpringCacheManager.class);
                    Assertions.assertThat(context).hasSingleBean(SerializerCustomizer.class);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load_without_any_config_contains_cache_manager(String flag) {
        runner.withPropertyValues("datakits.default-config.cache.enable-cache-manager=" + flag)
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheManager.class);
                    Assertions.assertThat(context.getBean(CacheManager.class)).isExactlyInstanceOf(RedissonSpringCacheManager.class);
                    Assertions.assertThat(context).hasSingleBean(SerializerCustomizer.class);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "off", "OFF", "0", "", " ", "   ", "null", "on", "ON", "1"})
    void not_load(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .run(context -> {
                    Assertions.assertThat(context).doesNotHaveBean(DefaultRedissonAutoConfiguration.class);
                    Assertions.assertThat(context).doesNotHaveBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).doesNotHaveBean(CacheManager.class);
                    Assertions.assertThat(context).doesNotHaveBean(SerializerCustomizer.class);
                });
    }
}
