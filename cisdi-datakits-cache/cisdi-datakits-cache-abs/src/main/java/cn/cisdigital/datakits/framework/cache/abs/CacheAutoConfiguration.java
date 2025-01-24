package cn.cisdigital.datakits.framework.cache.abs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxx
 * @since 2022-10-13
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.cache")
@ConditionalOnProperty(name = CacheConstants.ENABLE_CACHE, havingValue = "true", matchIfMissing = true)
public class CacheAutoConfiguration {
    static {
        log.info(CacheConstants.LOADING_CACHE);
    }
}
