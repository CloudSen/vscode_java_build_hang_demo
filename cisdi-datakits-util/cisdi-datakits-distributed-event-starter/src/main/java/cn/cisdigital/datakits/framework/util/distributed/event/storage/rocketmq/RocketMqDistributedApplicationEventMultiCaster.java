package cn.cisdigital.datakits.framework.util.distributed.event.storage.rocketmq;


import cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent;
import cn.cisdigital.datakits.framework.util.distributed.event.configuration.DistributedEventManageProperties;
import cn.cisdigital.datakits.framework.util.distributed.event.storage.DistributedEventStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： RocketEventManager
 * <p>
 * Description： Rocket存储器
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/2 19:59
 */
@Slf4j
@Getter
public class RocketMqDistributedApplicationEventMultiCaster extends SimpleApplicationEventMulticaster implements DistributedEventStorage, MessageListenerOrderly {

    private final ObjectMapper objectMapper;
    private final String topic;
    private final DefaultMQProducer producer;
    private final DefaultMQPushConsumer consumer;
    private final DistributedEventManageProperties.RocketMQ rocketMQ;

    public RocketMqDistributedApplicationEventMultiCaster(ObjectMapper objectMapper, DistributedEventManageProperties eventManageProperties) {
        DistributedEventManageProperties.RocketMQ rocketMQ = eventManageProperties.getRocketMq();
        if (StringUtils.isEmpty(rocketMQ.getAddress())) {
            throw new IllegalArgumentException("rocket mq address not found");
        }

        if (StringUtils.isEmpty(rocketMQ.getGroupId())) {
            throw new IllegalArgumentException("rocket mq groupId not found");
        }

        this.objectMapper = objectMapper;
        this.topic = rocketMQ.getTopic();

        if (eventManageProperties.isProducerEnable()) {
            log.info("开始初始化rocketmq producer");
            this.producer = initProducer(eventManageProperties);
            log.info("初始化rocketmq producer完毕");
        } else {
            this.producer = null;
        }
        this.consumer = initConsumer(eventManageProperties);
        this.rocketMQ = rocketMQ;
    }

    @Override
    public void multicastEvent(@NonNull final ApplicationEvent event, @Nullable ResolvableType eventType) {
        if (event instanceof DistributedEvent && Objects.nonNull(producer)) {
            push((DistributedEvent) event);
            log.info("Event push : {}", event);
            return;
        }
        super.multicastEvent(event, eventType);
    }

    @Override
    public <T extends DistributedEvent> void push(T t) {
        try {
            String value = objectMapper.writeValueAsString(t);
            Message message = new Message(topic, value.getBytes(StandardCharsets.UTF_8));
            producer.send(message);
        } catch (JsonProcessingException | MQClientException | RemotingException | MQBrokerException |
                 InterruptedException e) {
            log.error("Event persistence error", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (Objects.nonNull(producer)) {
                producer.shutdown();
            }

            if (Objects.nonNull(consumer)) {
                consumer.shutdown();
            }
        } catch (Exception e) {
            log.error("close producer error", e);
        }
    }


    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        msgs.forEach(messageExt -> {
            DistributedEvent event;
            try {
                event = objectMapper.readValue(messageExt.getBody(), DistributedEvent.class);
            } catch (IOException e) {
                log.error("deserialization failed -> {}", messageExt.getBody(), e);
                return;
            }
            //流程正常结束的手动ack。若中间有流程抛异常，异常会被打印，消息仍然会被消费掉，客户端需自己实现重试
            super.multicastEvent(event, null);
        });
        return ConsumeOrderlyStatus.SUCCESS;
    }

    /**
     * 初始化消费者
     *
     * @param eventManageProperties 消费者配置信息
     */
    private DefaultMQPushConsumer initConsumer(DistributedEventManageProperties eventManageProperties) {
        DistributedEventManageProperties.RocketMQ rocketMQ = eventManageProperties.getRocketMq();
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMQ.getGroupId());
        consumer.setNamesrvAddr(rocketMQ.getAddress());
        // 从最新开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        log.info("开始初始化rocketmq consumer");
        try {
            consumer.subscribe(rocketMQ.getTopic(), rocketMQ.getTag());

            // 每个消费者会启动一个线程池来进行消费，默认20，由于使用了无界队列，所以这里最大也只能达到consumeThreadMin个线程数量
            int threadNum = Math.max(1, rocketMQ.getConcurrency());
            consumer.setConsumeThreadMax(threadNum);
            consumer.setConsumeThreadMin(threadNum);

            // 消息客户端每次从Broker拉取消息最大条数，默认为32。
            // 当调大这个参数如100时，可能会失效，因为Broker端做了限制。
            // 需要配合broker端 下面2个参数：
            // maxTransferCountOnMessageInMemory:内存允许一次消息拉取的最大条数，默认值为 32 条。
            // maxTransferBytesOnMessageInMemory：内存允许一次消息拉取的最大消息大小，默认为 256K。
            consumer.setPullBatchSize(32);
            // 消息消费监听器中的消息条数，默认为 1。
            consumer.setConsumeMessageBatchMaxSize(1);
            consumer.registerMessageListener(this);
            consumer.start();
            log.info("初始化rocketmq consumer 完毕");
            return consumer;
        } catch (MQClientException e) {
            log.error("rocket mq consumer", e);
            throw new RuntimeException("rocket mq consumer启动失败");
        }
    }

    /**
     * 初始化producer配置
     *
     * @param eventManageProperties mq配置
     * @return producer配置信息
     */
    private DefaultMQProducer initProducer(DistributedEventManageProperties eventManageProperties) {

        DistributedEventManageProperties.RocketMQ rocketMQ = eventManageProperties.getRocketMq();
        try {
            DefaultMQProducer producer = new DefaultMQProducer(rocketMQ.getGroupId());
            producer.setNamesrvAddr(rocketMQ.getAddress());
            producer.setSendMsgTimeout(rocketMQ.getSendTimeOut());
            producer.setDefaultTopicQueueNums(1);
            producer.start();
            log.info("rocketmq分布式事件生产者已启动: group={}, topic={}", producer.getProducerGroup(), producer.getCreateTopicKey());
            return producer;
        } catch (Exception e) {
            log.error("mq生产者启动异常", e);
            return null;
        }
    }


    @Override
    public void afterPropertiesSet() {

    }

}
