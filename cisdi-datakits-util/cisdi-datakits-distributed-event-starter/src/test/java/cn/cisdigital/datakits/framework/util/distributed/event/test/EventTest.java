package cn.cisdigital.datakits.framework.util.distributed.event.test;

import cn.cisdigital.datakits.framework.util.distributed.event.DistributedEventAutoConfiguration;
import cn.cisdigital.datakits.framework.util.distributed.event.IDistributedEventPublisher;
import cn.cisdigital.datakits.framework.util.distributed.event.example.ExampleEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;

/**
 * @author xxx
 * @since 2024/6/25 11:44
 */
@SpringBootTest(classes = {DistributedEventAutoConfiguration.class})
@TestPropertySource("classpath:application.yaml")
class EventTest {

    @Resource
    private IDistributedEventPublisher distributedEventPublisher;

    @Test
    void sendEventTest(){
        ExampleEvent event = new ExampleEvent();
        event.setName("test hhh 222");
        event.setId(111222L);
        distributedEventPublisher.publishEvent(event);
    }


}
