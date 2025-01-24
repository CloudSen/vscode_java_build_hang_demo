package cn.cisdigital.datakits.framework.util.distributed.event;

import org.springframework.context.ApplicationEvent;

/**
 * 分布式事件推送接口定义
 * @author xxx
 * @since 2024/6/25 10:54
 */
public interface IDistributedEventPublisher {

    /**
     * 一次性推送事件
     * <p>1、无事务保障，无重试机制，可能存在消息发送失败，<b>不可用于强一致性业务场景</b>
     * <p>2、{@link cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent}的子类且开启了分布式事件
     * 才会根据配置推送到消息队列
     * <p>3、推送失败会抛出异常
     *
     * @param event event
     */
    void publishEvent(ApplicationEvent event);

    /**
     *
     * @param event
     */
    void publishEventWithTransaction(ApplicationEvent event);
}
