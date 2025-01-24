package cn.cisdigital.datakits.framework.util.sqlparser.config;


import lombok.Data;

/**
 * @author xxx
 * @since 2024/04/17 9:12
 */
@Data
public class DatasourceProperties {

    /**
     * 数据源连接池大小
     */
    private int maxPoolSize = 3;
    /**
     * 缓存池大小
     */
    private int maxCacheSize = 10;
    /**
     * 缓存过期时间，（min）
     */
    private long expireTime = 360L;


}
