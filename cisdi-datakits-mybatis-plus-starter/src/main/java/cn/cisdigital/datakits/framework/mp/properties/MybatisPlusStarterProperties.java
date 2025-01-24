package cn.cisdigital.datakits.framework.mp.properties;

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
public class MybatisPlusStarterProperties {

    /**
     * 是否启用默认配置
     */
    private Boolean enableMybatis;

    public MybatisPlusStarterProperties() {
        this.enableMybatis = false;
    }
}
