package cn.cisdigital.datakits.framework.web;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 自动装配开关
 * @author xxx
 * @since 2022-10-12
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = AutoConfigConstants.CONFIG_PREFIX)
public class WebStarterProperties {

    private Boolean enableWeb;

    public WebStarterProperties() {
        enableWeb = true;
    }
}
