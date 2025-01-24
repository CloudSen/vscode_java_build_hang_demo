package cn.cisdigital.datakits.framework.cloud.alibaba.convertor;

import cn.cisdigital.datakits.framework.cloud.alibaba.properties.CustomHttpProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RestTemplateProperties;
import lombok.experimental.UtilityClass;


/**
 * @author xxx
 */
@UtilityClass
public class RestTemplatePropertiesConvertor {

    public static CustomHttpProperties convertToCustomHttpProperties(RestTemplateProperties restTemplateProperties) {
        return new CustomHttpProperties()
            .setConnectTimeout(restTemplateProperties.getConnectTimeout())
            .setReadTimeout(restTemplateProperties.getReadTimeout())
            .setWriteTimeout(restTemplateProperties.getWriteTimeout())
            .setCallTimeout(restTemplateProperties.getCallTimeout())
            .setRetryOnConnectionFailure(restTemplateProperties.getRetryOnConnectionFailure())
            .setMaxIdleConnections(restTemplateProperties.getMaxIdleConnections())
            .setKeepAliveMinutes(restTemplateProperties.getKeepAliveMinutes())
            .setMaxRequests(restTemplateProperties.getMaxRequests())
            .setMaxRequestsPerHost(restTemplateProperties.getMaxRequestsPerHost());
    }

}
