package cn.cisdigital.datakits.framework.storage.minio;

import cn.cisdigital.datakits.framework.common.constant.SymbolConstants;
import cn.cisdigital.datakits.framework.storage.abs.ResourceLocation;
import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException.MinioErrorCode;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Minio资源路径实现
 *
 * @author xxx
 * @since 2023-05-18
 */
@Slf4j
public class MinioLocation implements ResourceLocation {

    private final URI uri;
    /**
     * MINIO blob name
     */
    private final String blobName;
    private final MinioClient minioClient;
    private final CisdiMinioProperties properties;
    private static final String PARENT_PATH_REGX = "(.*?)[^/]+/?$";

    public MinioLocation(CisdiMinioProperties properties, MinioClient minioClient, String location) {
        try {
            Assert.isTrue(
                location.startsWith(MinioProtocolResolver.SCHEME),
                "Minio全路径必须有minio://前缀");
            Assert.notNull(minioClient, "缺少Minio客户端");
            this.properties = properties;
            this.minioClient = minioClient;
            this.uri = new URI(location);
            this.blobName = getBlobPathFromUri(uri);
        } catch (URISyntaxException e) {
            throw new MinioException(MinioErrorCode.ILLEGAL_FULL_PATH, e);
        }
    }

    @Override
    public String getScheme() {
        return uri.getScheme();
    }

    @Override
    public boolean isFile() {
        return StringUtils.isNotBlank(this.blobName) && !this.blobName.endsWith(SymbolConstants.SLASH);
    }

    @Override
    public boolean isFolder() {
        return StringUtils.isBlank(this.blobName) || this.blobName.endsWith(SymbolConstants.SLASH);
    }

    @Override
    public boolean isExists() {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(this.getRootPathName())
                .object(this.blobName).build());
            return true;
        } catch (Exception e) {
            log.error(MinioException.MinioErrorCode.REQUEST_ERROR.getMsg(), e);
            return false;
        }
    }

    @Override
    public boolean isRootPath() {
        String actualPath = uri.getPath();
        String expectedPath = properties.getBucket();
        return StringUtils.equals(expectedPath, SymbolConstants.SLASH + actualPath);
    }

    @Override
    public String getRootPathName() {
        return properties.getBucket();
    }

    @Override
    public String getFullPath() {
        return ResourceLocation.encodeUri(uri.toString());
    }

    @Override
    public String getParentPath() {
        if (isRootPath()) {
            return StringUtils.EMPTY;
        }
        return getFullPath().replaceAll(PARENT_PATH_REGX, "$1");
    }

    @Override
    public String getFullPathAfterRoot() {
        return ResourceLocation.encodeUri(uri.toString().substring(uri.toString().indexOf(properties.getBucket()) + properties.getBucket().length()));
    }

    @Override
    public URI getUri() {
        return uri;
    }

    private String getBlobPathFromUri(URI minioUri) {
        String uriPath = minioUri.getPath();
        if (StringUtils.isBlank(uriPath) || uriPath.equals(SymbolConstants.SLASH)) {
            // 返回空字符串代表当前就是根目录（桶）
            return StringUtils.EMPTY;
        } else {
            return ResourceLocation.encodeUri(uriPath);
        }
    }
}
