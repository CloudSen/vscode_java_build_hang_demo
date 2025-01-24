package cn.cisdigital.datakits.framework.cache.redisson.properties;

import cn.cisdigital.datakits.framework.cache.redisson.RedissonCacheStarterConstants;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;
import java.util.Map;

/**
 * @author xxx
 */
@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = RedissonCacheStarterConstants.CACHE_PROPERTIES)
public class RedissonCacheManagerProperties {

    private final boolean enableCacheManager;
    private final Map<String, RedissonCacheManagerExpireTime> keyExpireTimeMap;

    @Data
    public static class RedissonCacheManagerExpireTime {
        private Duration ttl;
        private Duration maxIdleTime;
    }
}
