package cn.cisdigital.datakits.framework.cache.redisson.customizer;

import cn.cisdigital.datakits.framework.cache.abs.CacheAutoConfiguration;
import cn.cisdigital.datakits.framework.cache.redisson.RedissonCacheStarterConstants;
import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Component;

/**
 * 序列化配置
 *
 * @author xxx
 * @since 2021-08-01 16:15
 */
@Slf4j
@Component
@SuppressWarnings("DuplicatedCode")
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class SerializerCustomizer implements RedissonAutoConfigurationCustomizer {

    @Override
    public void customize(Config configuration) {
        log.info(RedissonCacheStarterConstants.LOADING_REDISSON_CUSTOM_SERIALIZER);
        configuration.setCodec(new JsonJacksonCodec(JsonUtils.OBJECT_MAPPER));
    }
}
