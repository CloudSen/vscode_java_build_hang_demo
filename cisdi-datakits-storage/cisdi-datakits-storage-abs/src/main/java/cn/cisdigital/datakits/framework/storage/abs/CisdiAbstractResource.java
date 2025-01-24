package cn.cisdigital.datakits.framework.storage.abs;

import cn.cisdigital.datakits.framework.common.constant.SymbolConstants;
import cn.cisdigital.datakits.framework.storage.abs.exception.StorageException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 资源抽象
 * <p>
 * 代表自定义资源类型继承了
 * </p>
 * <p>
 * 继承了AbstractResource，并且扩展了一些方法
 * </p>
 *
 * @author xxx
 * @since 2023-05-16
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public abstract class CisdiAbstractResource extends AbstractResource implements Closeable {

    /**
     * 当前资源的路径
     */
    private final ResourceLocation resourceLocation;

    /**
     * 当前资源的输入流
     * <p>
     * 上传资源时，必须设置此输入流，可批量操作
     * </p>
     *
     * @see CisdiAbstractResource#setSelfInputStream(InputStream)
     * @see CisdiAbstractResource#upload()
     * @see CisdiAbstractResource#download()
     */
    @Nullable
    private InputStream selfInputStream;

    /**
     * 相关联的资源
     */
    @Nullable
    private Set<CisdiAbstractResource> relatedResources;

    /**
     * 当前资源是否为文件
     */
    @Override
    public boolean isFile() {
        return resourceLocation.isFile();
    }

    /**
     * 如果是目录，则返回空字符串，否则返回文件名，如test.yaml
     * <p>
     * 注意：不会返回null，不存在都是返回的空字符串
     * </p>
     */
    @Override
    public String getFilename() {
        if (this.resourceLocation.isFolder()) {
            return StringUtils.EMPTY;
        }
        String fullPath = this.resourceLocation.getFullPath();
        String filename = fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
        return StringUtils.isBlank(filename) ? StringUtils.EMPTY : filename;
    }

    @Override
    public boolean exists() {
        try {
            return this.resourceLocation.isExists();
        } catch (Exception e) {
            log.error("[对象存储] 判断资源是否存在异常", e);
            return false;
        }
    }

    @Override
    public URL getURL() throws IOException {
        return getURI().toURL();
    }

    @Override
    public URI getURI() {
        return this.resourceLocation.getUri();
    }

    @Override
    public String getDescription() {
        try {
            return StringUtils.EMPTY;
        } catch (Exception e) {
            log.error("[对象存储] 获取资源描述异常", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.getSelfInputStream();
    }

    /**
     * 获取资源输入流
     */
    public InputStream getSelfInputStream() {
        return selfInputStream == null
                ? new ByteArrayInputStream(StringUtils.EMPTY.getBytes(StandardCharsets.UTF_8))
                : selfInputStream;
    }

    /**
     * 设置相关资源
     */
    public void setRelatedResources(Set<CisdiAbstractResource> relatedResources) {
        Set<String> relatedSchemes = relatedResources.stream().map(r -> r.getResourceLocation().getScheme())
                .collect(Collectors.toSet());
        if (relatedSchemes.size() != 1) {
            throw new StorageException(StorageException.StorageErrorCode.MULTI_SCHEME_ERROR);
        }
        String currentScheme = getResourceLocation().getScheme();
        String existsScheme = relatedSchemes.stream().findFirst()
                .orElseThrow(() -> new StorageException(StorageException.StorageErrorCode.MISSING_SCHEME));
        if (!StringUtils.equalsAnyIgnoreCase(existsScheme, currentScheme)) {
            throw new StorageException(StorageException.StorageErrorCode.MULTI_SCHEME_ERROR);
        }
        this.relatedResources = relatedResources;
    }

    /**
     * 获取资源后缀，如果是目录，返回空字符串
     * <p>因为是从名字上判断的，不保证是真实的后缀，也得不到.tar.gz这种复合后缀，一般场景足够用了</p>
     */
    public String getSuffix() {
        String fullPath = this.getResourceLocation().getFullPath();
        if (!isFile()) {
            return StringUtils.EMPTY;
        }
        return fullPath.substring(fullPath.lastIndexOf(SymbolConstants.DOT) + 1);
    }

    // <editor-fold desc="需要自定义的逻辑">

    /**
     * 获取资源元数据信息
     */
    public abstract Optional<ResourceMetadata> getMetadata();

    /**
     * 通过文件流上传
     */
    public abstract boolean upload();

    /**
     * 通过文件流下载
     * <p>
     * 记得关闭流
     * </p>
     * <p>
     * 下载的文件会通过input stream连接
     * </p>
     * <p>
     * 1. minio时，只有input stream
     * </p>
     * <p>
     * 2. hdfs时，会有input stream和 filesystem
     * </p>
     */
    public abstract ResourceStream download();

    /**
     * 删除资源
     *
     * @param recursive 是否递归删除
     */
    public abstract boolean delete(boolean recursive);

    /**
     * 创建资源路径
     * <p>
     * 授权777
     * </p>
     */
    public abstract boolean mkdir();

    /**
     * 读取某些简单格式的资源内容
     *
     * @param skipLineNums 跳过的行数
     * @param limit        读取行数
     * @return 每一行的内容
     */
    public abstract List<String> readContent(int skipLineNums, int limit);

    /**
     * 写入新的资源内容
     */
    public boolean writeContent(String newContent) {
        if (StringUtils.isBlank(newContent)) {
            newContent = StringUtils.EMPTY;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(newContent.getBytes(StandardCharsets.UTF_8))) {
            this.setSelfInputStream(inputStream);
            return this.upload();
        } catch (Exception e) {
            log.error("[对象存储] 写入资源内容读取异常", e);
            return false;
        }
    }

    // </editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CisdiAbstractResource that = (CisdiAbstractResource) o;
        return resourceLocation.equals(that.resourceLocation) && Objects.equals(selfInputStream, that.selfInputStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resourceLocation, selfInputStream);
    }

    @Override
    public void close() throws IOException {
        // 关闭selfInputStream和relatedResources
        if (selfInputStream != null) {
            selfInputStream.close();
        }
        if (CollectionUtils.isNotEmpty(relatedResources)) {
            for (CisdiAbstractResource relatedResource : relatedResources) {
                relatedResource.close();
            }
        }
    }
}
