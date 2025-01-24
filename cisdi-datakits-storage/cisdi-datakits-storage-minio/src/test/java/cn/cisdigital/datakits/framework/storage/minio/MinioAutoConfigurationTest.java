package cn.cisdigital.datakits.framework.storage.minio;

import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import io.minio.MinioClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class MinioAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MinioAutoConfiguration.class))
            .withPropertyValues(
                    "datakits.default-config.storage.minio.endpoint=http://10.106.253.24:9000",
                    "datakits.default-config.storage.minio.accessKey=datakits",
                    "datakits.default-config.storage.minio.secretKey=Cisdi@123456",
                    "datakits.default-config.storage.minio.bucket=integration-test");

    @Test
    @DisplayName("成功加载到Spring容器中")
    void success_register_beans() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(MinioAutoConfiguration.class);
            assertThat(context).hasSingleBean(CisdiMinioProperties.class);
            assertThat(context).hasSingleBean(MinioClient.class);
        });
    }

    /**
     * minio enabled为false时，不会注册MinioClient Bean，也不会注册minio的resource协议。
     *
     * @param enabled 是否启用minio
     */
    @ParameterizedTest
    @ValueSource(strings = { "true", "false" })
    @DisplayName("minio的enable，能正常工作")
    void testMinioProperties(String enabled) {
        runner.withPropertyValues("datakits.default-config.storage.minio.enabled=" + enabled)
                .run(context -> {
                    if ("true".equals(enabled)) {
                        assertThat(context).hasSingleBean(CisdiMinioProperties.class);
                        assertThat(context).hasSingleBean(MinioClient.class);
                        assertThat(context).hasSingleBean(MinioAutoConfiguration.class);
                    } else {
                        assertThat(context).hasSingleBean(MinioAutoConfiguration.class);
                        assertThat(context).hasSingleBean(CisdiMinioProperties.class);
                        assertThat(context).doesNotHaveBean(MinioClient.class);
                    }
                    CisdiMinioProperties properties = context.getBean(CisdiMinioProperties.class);
                    assertThat(properties.isEnabled()).isEqualTo(Boolean.parseBoolean(enabled));
                    assertThat(properties.getEndpoint()).isEqualTo("http://10.106.253.24:9000");
                    assertThat(properties.getAccessKey()).isEqualTo("datakits");
                    assertThat(properties.getSecretKey()).isEqualTo("Cisdi@123456");
                    assertThat(properties.getBucket()).isEqualTo("integration-test");
                });
    }

}
