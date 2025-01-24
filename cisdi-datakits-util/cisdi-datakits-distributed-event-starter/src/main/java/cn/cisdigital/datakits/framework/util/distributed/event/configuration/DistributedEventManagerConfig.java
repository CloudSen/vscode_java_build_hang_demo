package cn.cisdigital.datakits.framework.util.distributed.event.configuration;


import cn.cisdigital.datakits.framework.util.distributed.event.storage.MultiCasterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.support.TaskUtils;

import javax.annotation.Resource;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： EventManagerConfig
 * <p>
 * Description：
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/3 13:56
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DistributedEventManageProperties.class)
@Slf4j
public class DistributedEventManagerConfig {

    @Resource
    private ObjectMapper objectMapper;


    @Bean("applicationEventMulticaster")
    @ConditionalOnProperty(value = {"datakits.sdk.distributed-event.consumeEnable"}, matchIfMissing = false)
    public SimpleApplicationEventMulticaster distributedApplicationEventMultiCaster(DistributedEventManageProperties eventManageProperties) {
        log.info("开启分布式事件");
        return MultiCasterFactory.build(objectMapper, eventManageProperties);
    }

    @Bean("applicationEventMulticaster")
    @ConditionalOnMissingBean(SimpleApplicationEventMulticaster.class)
    public SimpleApplicationEventMulticaster localEventMultiCaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        // 不要设置setTaskExecutor否则TransactionalEventListener不会生效
        eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
        return eventMulticaster;
    }
}
