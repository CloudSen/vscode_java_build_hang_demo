package cn.cisdigital.datakits.framework.cache.abs;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2022-09-23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheConstants {
    public static final String COLON = ":";

    public static final String ENABLE_CACHE = AutoConfigConstants.CONFIG_PREFIX + ".enable-cache";
    public static final String LOADING_CACHE = AutoConfigConstants.LOG_PREFIX + "加载缓存服务";

    public static final String CACHE_ERROR_CODE_PREFIX ="000002";
}
