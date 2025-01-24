package cn.cisdigital.datakits.framework.storage.minio;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(CisdiMinioProperties.class)
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.storage.minio")
public class MinioAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".storage.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
    public MinioClient minioClient(CisdiMinioProperties properties) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                // todo 使用okhttp连接池
                .build();
        minioClient.setTimeout(
                properties.getConnectTimeout().toMillis(),
                properties.getWriteTimeout().toMillis(),
                properties.getReadTimeout().toMillis());
        log.info(AutoConfigConstants.LOG_PREFIX + "已注册MinioClient");
        return minioClient;
    }
}
