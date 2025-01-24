package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.KafkaDynamicProducerHolder;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.RocketMqDynamicProducerHolder;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * @author xxx
 * @since 2022-08-31-10:40
 */
@Component
public class DestroyConfig {

    @PreDestroy
    public void destroy(){
        RocketMqDynamicProducerHolder.destroy();
        KafkaDynamicProducerHolder.destroy();
    }
}
