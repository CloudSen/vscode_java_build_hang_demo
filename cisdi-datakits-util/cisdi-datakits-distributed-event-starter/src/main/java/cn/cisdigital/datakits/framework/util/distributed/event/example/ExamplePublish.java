package cn.cisdigital.datakits.framework.util.distributed.event.example;

import cn.cisdigital.datakits.framework.util.distributed.event.IDistributedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author xxx
 * @since 2024/6/26 8:37
 */
@Component
@RequiredArgsConstructor
public class ExamplePublish {
    private final IDistributedEventPublisher distributedEventPublisher;

    public void example() {
        // 业务逻辑
        ExampleEvent event = new ExampleEvent("111",1L);
        // 发送事件
        distributedEventPublisher.publishEvent(event);
    }
}
