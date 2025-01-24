package cn.cisdigital.datakits.framework.storage.abs;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 资源路径抽象
 *
 * <p>代表了资源的存储位置</p>
 * <p>路径规范：scheme://rootPath/serviceName/path/filename</p>
 *
 * @author xxx
 * @since 2023-05-16
 */
public interface ResourceLocation {

    String URI_TEMPLATE = "${scheme}://${authority}/${serviceName}/${path}/${filename}";

    /**
     * 获取URI的scheme
     */
    String getScheme();

    /**
     * 是否为文件
     */
    boolean isFile();

    /**
     * 是否为文件夹
     */
    boolean isFolder();

    /**
     * 是否存在
     */
    boolean isExists();

    /**
     * 是否为根路径
     */
    boolean isRootPath();

    /**
     * 获取根目录名
     */
    String getRootPathName();

    /**
     * 获取完整全路径(URI字符串)
     * <p>scheme://authority/path[?query][#fragment]</p>
     */
    String getFullPath();

    /**
     * 获取上一级全路径
     * <p>如果已经是根路径，则返回的是空字符串</p>
     */
    String getParentPath();

    /**
     * 获取跟目录之后的全路径
     */
    String getFullPathAfterRoot();

    /**
     * 获取URI对象
     */
    URI getUri();

    /**
     * 对全路径进行编码，解决中文和富符号问题
     */
    static String encodeUri(String uri) {
        return UriComponentsBuilder.fromUriString(uri).build().encode(StandardCharsets.UTF_8).toString();
    }

    /**
     * 对编码后的全路径解码
     */
    static String decodeUri(String encodedUri) {
        return UriUtils.decode(encodedUri, StandardCharsets.UTF_8.name());
    }
}
