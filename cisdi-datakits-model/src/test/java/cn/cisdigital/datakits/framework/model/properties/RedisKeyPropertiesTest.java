package cn.cisdigital.datakits.framework.model.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisKeyPropertiesTest {

    @EnableConfigurationProperties(RedisKeyProperties.class)
    private static class RedisKeyAutoConfiguration {

    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisKeyAutoConfiguration.class))
            .withPropertyValues(
            // 模拟yaml中没有配置
            );

    @Test
    @DisplayName("没有任何配置，上下文启动失败")
    public void whenPropertiesNotSet_thenContextFailed() {
        contextRunner.run(context -> {
            assertThat(context).hasFailed();
        });
    }
    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    @DisplayName("没有cdc-doris-tx-semaphore-key配置，上下文启动失败")
    public void whenCdcKeyNotSet_thenContextFailed(String key) {
        contextRunner.withPropertyValues(
                "public.redis.business-keys.cdc-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.etl-doris-tx-semaphore-key=etlKey",
                "public.redis.business-keys.collie-doris-tx-semaphore-key=collieKey",
                "public.redis.business-keys.doris-available-key=dorisKey").run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasRootCauseInstanceOf(
                                    org.springframework.boot.context.properties.bind.validation.BindValidationException.class)
                            .hasMessageContaining(
                                    "Failed to bind properties under 'public.redis.business-keys' to cn.cisdigital.datakits.framework.model.properties.RedisKeyProperties");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    @DisplayName("没有etl-doris-tx-semaphore-key配置，上下文启动失败")
    public void whenEtlKeyNotSet_thenContextFailed(String key) {
        contextRunner.withPropertyValues(
                "public.redis.business-keys.cdc-doris-tx-semaphore-key=cdcKey",
                "public.redis.business-keys.etl-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.collie-doris-tx-semaphore-key=collieKey",
                "public.redis.business-keys.doris-available-key=dorisKey").run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasRootCauseInstanceOf(
                                    org.springframework.boot.context.properties.bind.validation.BindValidationException.class)
                            .hasMessageContaining(
                                    "Failed to bind properties under 'public.redis.business-keys' to cn.cisdigital.datakits.framework.model.properties.RedisKeyProperties");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    @DisplayName("没有collie-doris-tx-semaphore-key配置，上下文启动失败")
    public void whenCollieKeyNotSet_thenContextFailed(String key) {
        contextRunner.withPropertyValues(
                "public.redis.business-keys.collie-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.cdc-doris-tx-semaphore-key=cdcKey",
                "public.redis.business-keys.etl-doris-tx-semaphore-key=etlKey",
                "public.redis.business-keys.doris-available-key=dorisKey").run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasRootCauseInstanceOf(
                                    org.springframework.boot.context.properties.bind.validation.BindValidationException.class)
                            .hasMessageContaining(
                                    "Failed to bind properties under 'public.redis.business-keys' to cn.cisdigital.datakits.framework.model.properties.RedisKeyProperties");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    @DisplayName("没有doris-available-key配置，上下文启动失败")
    public void whenDorisKeyNotSet_thenContextFailed(String key) {
        contextRunner.withPropertyValues(
                "public.redis.business-keys.doris-available-key=" + key,
                "public.redis.business-keys.cdc-doris-tx-semaphore-key=cdcKey",
                "public.redis.business-keys.etl-doris-tx-semaphore-key=etlKey",
                "public.redis.business-keys.collie-doris-tx-semaphore-key=collieKey").run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasRootCauseInstanceOf(
                                    org.springframework.boot.context.properties.bind.validation.BindValidationException.class)
                            .hasMessageContaining(
                                    "Failed to bind properties under 'public.redis.business-keys' to cn.cisdigital.datakits.framework.model.properties.RedisKeyProperties");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = { "key1", "asldkfjal", "asfs_sadf" })
    @DisplayName("所有配置均配置，上下文正常启动，properties类绑定的值正确")
    public void whenAllPropertiesSet_thenNoException(String key) {
        contextRunner.withPropertyValues(
                "public.redis.business-keys.cdc-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.etl-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.collie-doris-tx-semaphore-key=" + key,
                "public.redis.business-keys.doris-available-key=" + key).run(context -> {
                    assertThat(context).hasNotFailed();
                    RedisKeyProperties properties = context.getBean(RedisKeyProperties.class);
                    assertThat(properties).isNotNull();
                    assertThat(properties.getCdcDorisTxSemaphoreKey()).isEqualTo(key);
                    assertThat(properties.getEtlDorisTxSemaphoreKey()).isEqualTo(key);
                    assertThat(properties.getCollieDorisTxSemaphoreKey()).isEqualTo(key);
                    assertThat(properties.getDorisAvailableKey()).isEqualTo(key);
                });
    }
}
