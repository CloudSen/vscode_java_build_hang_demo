package cn.cisdigital.datakits.framework.util.distributed.event.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author xxx
 * @since 2024/6/25 16:51
 */
@Component
@Slf4j
public class ExampleListener {

    /**
     * 事件消息处理
     * <p>长耗时业务逻辑建议异步方式处理，否则会影响后续事件消费
     * <p>业务方自己实现重试
     *
     * @param event event
     */
    @EventListener
    public void exampleEventProcess(ExampleEvent event){
        log.info("process event {}",event);
    }

}
