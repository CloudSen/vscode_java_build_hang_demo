package cn.cisdigital.datakits.framework.cache.redisson;

import cn.cisdigital.datakits.framework.cache.abs.CacheAutoConfiguration;
import cn.cisdigital.datakits.framework.cache.abs.CacheService;
import cn.cisdigital.datakits.framework.cache.redisson.properties.RedissonCacheManagerProperties;
import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Redisson客户端的Spring Cache配置
 *
 * @author xxx
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(RedissonCacheManagerProperties.class)
@ConditionalOnClass({RedissonClient.class})
@AutoConfigureAfter({CacheAutoConfiguration.class, RedissonAutoConfiguration.class})
@SuppressWarnings({"RedundantThrows"})
public class DefaultRedissonAutoConfiguration {

    static {
        log.info(RedissonCacheStarterConstants.LOADING_REDISSON_AUTO_CONFIGURE);
    }

    private final RedissonCacheManagerProperties redissonCacheManagerProperties;

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) throws IOException {
        log.info(RedissonCacheStarterConstants.LOADING_REDISSON_CACHE_MGMT_AUTO_CONFIGURE);
        if (redissonCacheManagerProperties.isEnableCacheManager()) {
            Map<String, CacheConfig> cacheConfigMap = new HashMap<>(16);
            Map<String, RedissonCacheManagerProperties.RedissonCacheManagerExpireTime> cacheManagerExpireTimeMap =
                redissonCacheManagerProperties.getKeyExpireTimeMap();
            if (MapUtil.isNotEmpty(cacheManagerExpireTimeMap)) {
                cacheManagerExpireTimeMap.forEach((k, v) -> cacheConfigMap.put(k, new CacheConfig(v.getTtl().toMillis(), v.getMaxIdleTime().toMillis())));
            }
            return new RedissonSpringCacheManager(redissonClient, cacheConfigMap);
        } else {
            return new NoOpCacheManager();
        }
    }
    @Primary
    @Bean
    public CacheService cacheService(RedissonClient redissonClient) {
        return new CacheServiceImpl(redissonClient);
    }
}
