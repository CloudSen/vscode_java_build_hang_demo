package cn.cisdigital.datakits.framework.util.distributed.event.storage;


import cn.cisdigital.datakits.framework.util.distributed.event.configuration.DistributedEventManageProperties;
import cn.cisdigital.datakits.framework.util.distributed.event.storage.kafka.KafkaDistributedApplicationEventMultiCaster;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.support.TaskUtils;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： MultiCasterFactory
 * <p>
 * Description：
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/4 11:51
 */

public class MultiCasterFactory {

    private MultiCasterFactory() {
    }

    public static SimpleApplicationEventMulticaster build(ObjectMapper objectMapper, DistributedEventManageProperties eventManageProperties) {
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster;
        switch (eventManageProperties.getMqType()) {
            case KAFKA: {
                simpleApplicationEventMulticaster = new KafkaDistributedApplicationEventMultiCaster(objectMapper, eventManageProperties);
                break;
            }
            case ROCKETMQ: {
                // 未判断事件类是否存在，暂不支持该类型
//                simpleApplicationEventMulticaster = new RocketMqDistributedApplicationEventMultiCaster(objectMapper, eventManageProperties);
//                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        simpleApplicationEventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
        return simpleApplicationEventMulticaster;
    }

}
