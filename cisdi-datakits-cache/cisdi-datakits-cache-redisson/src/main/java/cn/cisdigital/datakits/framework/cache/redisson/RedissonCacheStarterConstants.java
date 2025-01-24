package cn.cisdigital.datakits.framework.cache.redisson;


import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2022-10-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedissonCacheStarterConstants {

    public static final String CACHE_PROPERTIES = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".cache";
    public static final String LOADING_REDISSON_AUTO_CONFIGURE = AutoConfigConstants.LOG_PREFIX + "加载Redisson配置";
    public static final String LOADING_REDISSON_CACHE_MGMT_AUTO_CONFIGURE = AutoConfigConstants.LOG_PREFIX + "加载Redisson cache manager配置";
    public static final String LOADING_REDISSON_CUSTOM_SERIALIZER = AutoConfigConstants.LOG_PREFIX + "加载Redisson 自定义序列化配置";

}
