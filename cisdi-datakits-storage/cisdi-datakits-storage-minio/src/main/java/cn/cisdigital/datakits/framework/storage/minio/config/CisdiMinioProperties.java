package cn.cisdigital.datakits.framework.storage.minio.config;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.cisdigital.datakits.framework.common.constant.SymbolConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公共Minio配置
 *
 * @author xxx
 */
@Data
@Validated
@Accessors(chain = true)
@ConfigurationProperties(prefix = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".storage.minio")
public class CisdiMinioProperties implements Serializable {

    private static final String DEFAULT_ALLOW_VIEW_SUFFIX = "txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js";

    /**
     * 是否启用，默认启用
     */
    private boolean enabled = true;

    /**
     * Minio实例的地址，如：http://10.106.251.249:9000
     */
    @NotBlank(message = "minio.valid.endpoint_not_config")
    private String endpoint;

    /**
     * 用户名
     */
    @NotBlank(message = "minio.valid.access_key_not_config")
    private String accessKey;

    /**
     * 密码
     */
    @NotBlank(message = "minio.valid.secret_key_not_config")
    private String secretKey;

    /**
     * 桶名,一般配置为${spring.application.name}即可
     */
    @NotBlank(message = "minio.valid.bucket_not_config")
    private String bucket;

    /**
     * 区域，默认是us-east-1
     */
    private String region = "us-east-1";

    /**
     * 连接超时时间，默认20秒，最低不小于10秒
     */
    @DurationMin(seconds = 10, message = "minio.valid.invalid_connection_timeout")
    private Duration connectTimeout = Duration.ofSeconds(20);

    /**
     * 写超时时间，默认2分钟，最低不小于10秒
     */
    @DurationMin(seconds = 10, message = "minio.valid.invalid_write_timeout")
    private Duration writeTimeout = Duration.ofMinutes(2);

    /**
     * 读超时时间，默认2分钟，最低不小于10秒
     */
    @DurationMin(seconds = 10, message = "minio.valid.invalid_read_timeout")
    private Duration readTimeout = Duration.ofMinutes(2);

    /**
     * 共享链接（下载、预览等）的超时时间，默认10分钟，最低不小于1分钟
     */
    @DurationMin(minutes = 1, message = "minio.valid.invalid_share_expire")
    private Duration preSignedExpire = Duration.ofMinutes(10);
    /**
     * 允许预览的文件后缀
     * <p>默认为： txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js</p>
     */
    private Set<String> allowViewSuffix = Arrays.stream(DEFAULT_ALLOW_VIEW_SUFFIX.split(SymbolConstants.COMMA))
            .collect(Collectors.toSet());
}
