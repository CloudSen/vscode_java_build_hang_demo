package cn.cisdigital.datakits.framework.util.distributed.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xxx
 * @since 2024/6/25 11:06
 */
@Service
@Slf4j
public class DistributedEventPublisher implements IDistributedEventPublisher {

    @Resource(name = "applicationEventMulticaster")
    private SimpleApplicationEventMulticaster multicaster;

    @Override
    public void publishEvent(ApplicationEvent event) {
        multicaster.multicastEvent(event);
    }

    @Override
    public void publishEventWithTransaction(ApplicationEvent event) {
        throw new UnsupportedOperationException();
    }
}
