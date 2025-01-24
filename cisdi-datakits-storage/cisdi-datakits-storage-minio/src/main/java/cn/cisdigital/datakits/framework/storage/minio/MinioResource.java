package cn.cisdigital.datakits.framework.storage.minio;

import cn.cisdigital.datakits.framework.common.constant.HeaderConstants;
import cn.cisdigital.datakits.framework.common.util.UnitUtils;
import cn.cisdigital.datakits.framework.storage.abs.CisdiAbstractResource;
import cn.cisdigital.datakits.framework.storage.abs.ResourceLocation;
import cn.cisdigital.datakits.framework.storage.abs.ResourceMetadata;
import cn.cisdigital.datakits.framework.storage.abs.ResourceStream;
import cn.cisdigital.datakits.framework.storage.abs.exception.StorageException;
import cn.cisdigital.datakits.framework.storage.abs.exception.StorageException.StorageErrorCode;
import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException;
import cn.cisdigital.datakits.framework.storage.minio.exception.MinioException.MinioErrorCode;
import io.minio.*;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Minio资源
 *
 * @author xxx
 * @since 2023-05-25
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class MinioResource extends CisdiAbstractResource {

    private final MinioClient minioClient;
    private final CisdiMinioProperties properties;

    public MinioResource(
        ResourceLocation resourceLocation,
        MinioClient minioClient,
        CisdiMinioProperties properties) {
        super(resourceLocation);
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public Optional<ResourceMetadata> getMetadata() {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                .bucket(getResourceLocation().getRootPathName())
                .object(getResourceLocation().getFullPathAfterRoot()).build());
            ResourceMetadata resourceMetadata = new ResourceMetadata()
                .setMimeType(statObjectResponse.headers().get(HeaderConstants.HEADER_CONTENT_TYPE))
                .setPath(getResourceLocation().getFullPath())
                .setSize(statObjectResponse.size())
                .setSizeWithUnit(UnitUtils.readableFileSize(statObjectResponse.size()))
                .setFolder(getResourceLocation().isFolder())
                .setName(getFilename())
                .setLastModified(statObjectResponse.lastModified().toLocalDateTime())
                .setMimeType(statObjectResponse.contentType());
            return Optional.of(resourceMetadata);
        } catch (Exception e) {
            log.error("获取资源元数据，异常", e);
            return Optional.empty();
        }
    }

    /**
     * 分块上传，任意块上传失败则全部失败
     */
    @Override
    public boolean upload() {
        try (InputStream selfInputStream = getSelfInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(getResourceLocation().getRootPathName())
                .object(getResourceLocation().getFullPathAfterRoot())
                .stream(selfInputStream, -1, 5243000L)
                .build());
            return true;
        } catch (Exception e) {
            log.error("资源上传异常", e);
            return false;
        }
    }

    @Override
    public ResourceStream download() {
        if (getResourceLocation().isFolder()) {
            throw new IllegalStateException("当前资源为文件夹，暂不支持下载: '" + getURI() + "'");
        }
        try {
            GetObjectResponse minioStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(getResourceLocation().getRootPathName())
                .object(getResourceLocation().getFullPathAfterRoot())
                .build());
            return new ResourceStream(minioStream, null);
        } catch (Exception e) {
            log.error("资源下载异常", e);
            InputStream emptyInStream = IOUtils.toInputStream("", StandardCharsets.UTF_8);
            return new ResourceStream(emptyInStream, null);
        }
    }

    @Override
    public boolean delete(boolean recursive) {
        try {
            if (getResourceLocation().isFolder()) {
                throw new MinioException(MinioErrorCode.UNSUPPORTED_OPERATION);
            }
            if (!this.exists()) {
                return true;
            }
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(getResourceLocation().getRootPathName())
                .object(getResourceLocation().getFullPathAfterRoot()).build());
            return true;
        } catch (Exception e) {
            log.error("资源删除异常", e);
            return false;
        }
    }

    @Override
    public boolean mkdir() {
        try {
            if (getResourceLocation().isFolder()) {
                minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getResourceLocation().getRootPathName())
                    .object(getResourceLocation().getFullPathAfterRoot())
                    .stream(IOUtils.toInputStream("", StandardCharsets.UTF_8), -1, 5243000L)
                    .build());
                return true;
            }
            String objectName = getResourceLocation().getFullPathAfterRoot()
                .substring(0, getResourceLocation().getFullPathAfterRoot().indexOf(getFilename()));
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(getResourceLocation().getRootPathName())
                .object(objectName)
                .stream(IOUtils.toInputStream("", StandardCharsets.UTF_8), -1, 5243000L)
                .build());
            return true;
        } catch (Exception e) {
            log.error("创建资源目录异常", e);
            return false;
        }
    }

    @Override
    public List<String> readContent(int skipLineNums, int limit) {
        String actualSuffix = Optional.of(super.getSuffix())
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new StorageException(StorageErrorCode.MULTI_SCHEME_ERROR));
        if (!properties.getAllowViewSuffix().contains(actualSuffix)) {
            throw new MinioException(MinioErrorCode.UNSUPPORTED_OPERATION);
        }
        try (ResourceStream rs = download()) {
            if (rs.getInputStream() == null) {
                log.warn("读取文件内容失败，文件流为空");
                return Collections.emptyList();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(rs.getInputStream(), StandardCharsets.UTF_8));
            Stream<String> stream = br.lines().skip(skipLineNums).limit(limit);
            return stream.collect(Collectors.toList());
        } catch (Exception e) {
            log.error("资源内容读取异常", e);
            return Collections.emptyList();
        }
    }
}
