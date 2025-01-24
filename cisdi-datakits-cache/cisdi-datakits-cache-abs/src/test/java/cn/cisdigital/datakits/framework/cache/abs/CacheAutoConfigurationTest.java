package cn.cisdigital.datakits.framework.cache.abs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Cache 自动装配加载测试
 *
 * @author xxx
 * @since 2024-12-10
 */
class CacheAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class))
            .withUserConfiguration(CacheStarterProperties.class);

    @ParameterizedTest
    @ValueSource(strings = {"false", "on", "ON", "1", "", " ", "   ", "null"})
    void not_load(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .run(context -> Assertions.assertThat(context).doesNotHaveBean(CacheAutoConfiguration.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"true"})
    void success_load(String flag) {
        runner.withPropertyValues("cisdi.autoconfiguration.enable-cache=" + flag)
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
                    Assertions.assertThat(context).hasSingleBean(CacheStarterProperties.class);
                });
    }

    @Test
    void success_load_without_config() {
        runner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(CacheAutoConfiguration.class);
            Assertions.assertThat(context).hasSingleBean(CacheStarterProperties.class);
        });
    }
}
