package cn.cisdigital.datakits.framework.storage.abs;

import cn.cisdigital.datakits.framework.storage.abs.exception.StorageException;
import cn.cisdigital.datakits.framework.storage.abs.exception.StorageException.StorageErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 资源工具类
 *
 * @author xxx
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceUtils {

    /**
     * 通过资源绝对路径，获取抽象资源
     *
     * @param resourceLoader spring的resource加载器
     * @param location 资源绝对路径，比如minio://bucketName/目录1/目录2/xxx.py
     * @return 抽象资源
     */
    public static CisdiAbstractResource getResourceFrom(ResourceLoader resourceLoader, String location) {
        Resource resource = resourceLoader.getResource(location);
        boolean isCisdiResource = resource instanceof CisdiAbstractResource;
        if (!isCisdiResource) {
            log.info("[对象存储] 请求资源：{}", location);
            throw new StorageException(StorageErrorCode.RESOURCE_TYPE_NOT_SUPPORT, location);
        }
        return (CisdiAbstractResource) resource;
    }
}
