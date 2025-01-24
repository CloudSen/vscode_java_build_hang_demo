package cn.cisdigital.datakits.framework.cloud.alibaba.properties;

import cn.cisdigital.datakits.framework.cloud.alibaba.AlibabaStarterConstants;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xxx
 * @since 2024-03-21
 */
@Data
@ConfigurationProperties(prefix = AlibabaStarterConstants.REMOTE_COUNT_PROPERTIES_PREFIX)
public class RemoteCountProperties {

    /**
     * 远程服务列表
     */
    @Valid
    private List<RemoteServiceInfo> services;
}
