package cn.cisdigital.datakits.framework.web.mvc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 如何对注册到spring中的object mapper做后置配置
 *
 * @author xxx
 */
@FunctionalInterface
public interface ObjectMapperPostProcessor {

    void postConfig(ObjectMapper objectMapper);
}
