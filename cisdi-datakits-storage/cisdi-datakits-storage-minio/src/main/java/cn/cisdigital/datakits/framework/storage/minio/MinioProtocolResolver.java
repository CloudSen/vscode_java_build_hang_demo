package cn.cisdigital.datakits.framework.storage.minio;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException.MinioErrorCode;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * MINIO存储协议解析器
 *
 * @author xxx
 * @since 2023-05-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioProtocolResolver implements ProtocolResolver, ResourceLoaderAware {

    /**
     * HDFS协议
     */
    public static final String SCHEME = "minio://";
    @Nullable
    private final MinioClient minioClient;
    @Nullable
    private final CisdiMinioProperties properties;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if (this.properties == null) {
            throw new MinioException(MinioErrorCode.NULL_PROPERTIES);
        }
        if (!Objects.requireNonNull(properties).isEnabled()) {
            log.warn("当前已禁用minio://资源协议: 用户主动禁止");
            return;
        }
        if (DefaultResourceLoader.class.isAssignableFrom(resourceLoader.getClass())) {
            ((DefaultResourceLoader) resourceLoader).addProtocolResolver(this);
            log.info(AutoConfigConstants.CONFIG_PREFIX + "注册资源解析器：{}", SCHEME);
        } else {
            log.warn("当前已禁用minio://资源协议: ResourceLoader不是DefaultResourceLoader的实现");
        }
    }

    @Nullable
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (!location.startsWith(SCHEME)) {
            return null;
        }
        Assert.notNull(properties, "加载Minio配置失败");
        Assert.notNull(minioClient, "加载MinioClient失败");
        MinioLocation minioLocation = new MinioLocation(properties, minioClient, location);
        return new MinioResource(minioLocation, minioClient, properties);
    }
}
