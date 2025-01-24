package cn.cisdigital.datakits.framework.cache.abs;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author xxx
 * @since 2022-10-12
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = AutoConfigConstants.CONFIG_PREFIX)
public class CacheStarterProperties {

    private Boolean enableCache;

    public CacheStarterProperties() {
        enableCache = true;
    }
}
