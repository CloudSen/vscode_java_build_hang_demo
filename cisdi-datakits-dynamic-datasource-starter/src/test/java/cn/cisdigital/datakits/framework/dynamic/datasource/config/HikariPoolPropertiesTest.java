package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import cn.cisdigital.datakits.framework.dynamic.datasource.DynamicDataSourceAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class HikariPoolPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamicDataSourceAutoConfiguration.class))
            .withPropertyValues("spring.messages.basename=i18n/framework",
            "spring.application.name=test-dynamic-datasource-application",
            "server.servlet.context-path=/test");

    @Test
    @DisplayName("加载默认配置")
    void loadDefaultConfig() {

        this.runner.run(context -> {
            HikariPoolProperties properties = context.getBean(HikariPoolProperties.class);
            assertThat(properties.getPoolConfigMap()).isNull();
        });
    }

    @Test
    @DisplayName("只配置pool-name")
    void loadOnlyPoolName() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=testPool"
        ).run(context -> {
            HikariPoolProperties properties = context.getBean(HikariPoolProperties.class);
            assertThat(properties.getPoolConfigMap()).isNotEmpty();
            assertThat(properties.getPoolConfigMap().get("test")).isNotNull();
            assertThat(properties.getPoolConfigMap().get("test").getPoolName()).isEqualTo("testPool");
            assertThat(properties.getPoolConfigMap().get("test").getConnectionTimeoutMs()).isEqualTo(15000L);
            assertThat(properties.getPoolConfigMap().get("test").getIdleTimeoutMs()).isEqualTo(600000L);
            assertThat(properties.getPoolConfigMap().get("test").getMaxLifetimeMs()).isEqualTo(1800000L);
            assertThat(properties.getPoolConfigMap().get("test").getKeepAliveTimeMs()).isEqualTo(60000L);
            assertThat(properties.getPoolConfigMap().get("test").getMinimumIdle()).isEqualTo(10);
            assertThat(properties.getPoolConfigMap().get("test").getMaximumPoolSize()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("加载自定义配置")
    void loadCustomConfig() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=testPool2",
                "datakits.default-config.ds.hikari.pool-config-map.test.connection-timeout-ms=1500",
                "datakits.default-config.ds.hikari.pool-config-map.test.idle-timeout-ms=20000",
                "datakits.default-config.ds.hikari.pool-config-map.test.max-lifetime-ms=80000",
                "datakits.default-config.ds.hikari.pool-config-map.test.keep-alive-time-ms=40000",
                "datakits.default-config.ds.hikari.pool-config-map.test.minimum-idle=6",
                "datakits.default-config.ds.hikari.pool-config-map.test.maximum-pool-size=20"
        ).run(context -> {
            HikariPoolProperties properties = context.getBean(HikariPoolProperties.class);
            assertThat(properties.getPoolConfigMap()).isNotEmpty();
            assertThat(properties.getPoolConfigMap().get("test").getPoolName()).isEqualTo("testPool2");
            assertThat(properties.getPoolConfigMap().get("test").getConnectionTimeoutMs()).isEqualTo(1500L);
            assertThat(properties.getPoolConfigMap().get("test").getIdleTimeoutMs()).isEqualTo(20000L);
            assertThat(properties.getPoolConfigMap().get("test").getMaxLifetimeMs()).isEqualTo(80000L);
            assertThat(properties.getPoolConfigMap().get("test").getKeepAliveTimeMs()).isEqualTo(40000L);
            assertThat(properties.getPoolConfigMap().get("test").getMinimumIdle()).isEqualTo(6);
            assertThat(properties.getPoolConfigMap().get("test").getMaximumPoolSize()).isEqualTo(20);
        });
    }

    @Test
    @DisplayName("poolName校验失败")
    void poolNameValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name="
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("connectionTimeout校验失败")
    void connectionTimeoutValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.connection-timeout-ms=100"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("idleTimeout校验失败")
    void idleTimeoutValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.idle-timeout-ms=1000"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("maxLifetime校验失败")
    void maxLifetimeValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.max-lifetime-ms=10000"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("keepAliveTime校验失败")
    void keepAliveTimeValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.keep-alive-time-ms=10000"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("minimumIdle校验失败")
    void minimumIdleValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.minimum-idle=0"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("maximumPoolSize校验失败")
    void maximumPoolSizeValidationFailed() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test.pool-name=test",
                "datakits.default-config.ds.hikari.pool-config-map.test.maximum-pool-size=0"
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).isInstanceOf(ConfigurationPropertiesBindException.class);
        });
    }

    @Test
    @DisplayName("配置多个连接池")
    void multiplePoolsConfiguration() {
        this.runner.withPropertyValues(
                "datakits.default-config.ds.hikari.pool-config-map.test1.pool-name=test1",
                "datakits.default-config.ds.hikari.pool-config-map.test1.maximum-pool-size=10",
                "datakits.default-config.ds.hikari.pool-config-map.test2.pool-name=test2",
                "datakits.default-config.ds.hikari.pool-config-map.test2.maximum-pool-size=20"
        ).run(context -> {
            assertThat(context).hasNotFailed();
            HikariPoolProperties properties = context.getBean(HikariPoolProperties.class);
            assertThat(properties.getPoolConfigMap()).hasSize(2);
            assertThat(properties.getPoolConfigMap().get("test1").getPoolName()).isEqualTo("test1");
            assertThat(properties.getPoolConfigMap().get("test2").getPoolName()).isEqualTo("test2");
        });
    }
}
