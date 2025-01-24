package cn.cisdigital.datakits.framework.storage.abs;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.storage")
@ConditionalOnProperty(name = "enable-storage", havingValue = "true", matchIfMissing = true)
public class StorageAutoConfiguration {

    private static final String LOADING_STORAGE = AutoConfigConstants.LOG_PREFIX + "加载对象存储服务";

    static {
        log.info(LOADING_STORAGE);
    }
}
