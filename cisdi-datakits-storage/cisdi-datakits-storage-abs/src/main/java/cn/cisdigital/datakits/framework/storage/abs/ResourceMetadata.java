package cn.cisdigital.datakits.framework.storage.abs;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源元数据
 *
 * @author xxx
 * @since 2023-05-25
 */
@Data
@Accessors(chain = true)
public class ResourceMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源字节大小
     */
    private Long size;

    /**
     * 资源带单位的大小
     */
    private String sizeWithUnit;

    /**
     * 资源名
     */
    private String name;

    /**
     * 最近一次修改时间
     */
    private LocalDateTime lastModified;

    /**
     * 资源路径
     */
    private String path;

    /**
     * 是否为目录
     */
    private boolean isFolder;

    /**
     * 媒体类型
     */
    private String mimeType;
}
