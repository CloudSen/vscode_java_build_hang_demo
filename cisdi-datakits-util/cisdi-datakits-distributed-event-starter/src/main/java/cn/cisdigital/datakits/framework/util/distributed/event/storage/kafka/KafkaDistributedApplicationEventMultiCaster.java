package cn.cisdigital.datakits.framework.util.distributed.event.storage.kafka;


import cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent;
import cn.cisdigital.datakits.framework.util.distributed.event.configuration.DistributedEventManageProperties;
import cn.cisdigital.datakits.framework.util.distributed.event.storage.AbstractDistributedApplicationEventMultiCaster;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： KafkaEventManager
 * <p>
 * Description： Kafka存储器
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/2 19:59
 */
@Slf4j
@Getter
public class KafkaDistributedApplicationEventMultiCaster extends AbstractDistributedApplicationEventMultiCaster
        implements AcknowledgingMessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final String topic;
    private final KafkaProducer<String, String> producer;
    private final Properties consumerProperties;
    private final DistributedEventManageProperties.Kafka kafka;

    public KafkaDistributedApplicationEventMultiCaster(ObjectMapper objectMapper, DistributedEventManageProperties eventManageProperties) {
        DistributedEventManageProperties.Kafka kafka = eventManageProperties.getKafka();
        if (StringUtils.isEmpty(kafka.getAddress())) {
            throw new IllegalArgumentException("kafka mq address not found");
        }

        if (StringUtils.isEmpty(kafka.getGroupId())) {
            throw new IllegalArgumentException("kafka mq groupId not found");
        }

        this.objectMapper = objectMapper;
        this.topic = kafka.getTopic();

        if (eventManageProperties.isProducerEnable()) {
            log.info("开始初始化kafka producer");
            this.producer = initProducer(kafka);
            log.info("初始化kafka producer完毕");
        } else {
            this.producer = null;
        }
        this.consumerProperties = buildConsumerProperties(kafka);
        this.kafka = kafka;
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
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, t.getClass().getName(), value);
            producer.send(record);
        } catch (JsonProcessingException e) {
            log.error("Event persistence error: {}", e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (Objects.nonNull(producer)) {
                producer.close();
            }
        } catch (Exception e) {
            log.error("close producer error", e);
        }
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        // 检查事件类是否存在
        final boolean eventClassExists = checkEventExists(consumerRecord.key());
        if (Objects.equals(eventClassExists, false)) {
            log.debug("unknown event class {}", consumerRecord.key());
            acknowledge(acknowledgment);
            return;
        }
        log.info("receive event : {}", consumerRecord.value());
        DistributedEvent event;
        try {
            event = objectMapper.readValue(consumerRecord.value(), DistributedEvent.class);
        } catch (JsonProcessingException e) {
            // 无效数据格式，直接消费掉
            acknowledge(acknowledgment);
            return;
        }
        //流程正常结束的手动ack。若中间有流程抛异常，异常会被打印，消息仍然会被消费掉，客户端需自己实现重试
        super.multicastEvent(event, null);
        acknowledge(acknowledgment);
    }

    /**
     * 手动提交ack
     *
     * @param acknowledgment ack
     */
    private void acknowledge(Acknowledgment acknowledgment) {
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }

    /**
     * 初始化kafkaProducer配置
     *
     * @param mq mq配置
     * @return producer配置信息
     */
    private KafkaProducer<String, String> initProducer(DistributedEventManageProperties.Kafka mq) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, mq.getAddress());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "50");
        props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        props.setProperty(CommonClientConfigs.RETRIES_CONFIG, "3");
        props.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        props.putAll(mq.getProducer());
        return new KafkaProducer<>(props);
    }

    /**
     * 初始化kafkaConsumer配置
     *
     * @param mq mq
     * @return 配置
     */
    private Properties buildConsumerProperties(DistributedEventManageProperties.Kafka mq) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, mq.getAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, mq.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.putAll(mq.getConsumer());
        return props;
    }

    /**
     * consumer properties to map
     *
     * @return map
     */
    public Map<String, Object> getConsumerConfigMap() {
        Map<String, Object> map = new HashMap<>(consumerProperties.size());
        for (final String name : consumerProperties.stringPropertyNames()) {
            map.put(name, consumerProperties.getProperty(name));
        }
        return map;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        registerSingletonBean("concurrentMessageListenerContainer", messageListenerContainer(), applicationContext);
    }


    /**
     * 动态注入单例bean实例
     *
     * @param beanName        bean名称
     * @param singletonObject 单例bean实例
     * @return 注入实例
     */
    private Object registerSingletonBean(String beanName, Object singletonObject, ApplicationContext applicationContext) {

        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        //获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();

        //动态注册bean.
        defaultListableBeanFactory.registerSingleton(beanName, singletonObject);

        //获取动态注册的bean.
        return configurableApplicationContext.getBean(beanName);
    }

    /**
     * 创建kafka message listener
     *
     * @return listener
     */
    public ConcurrentMessageListenerContainer<String, String> messageListenerContainer() {
        log.info("开始初始化分布式事件kafka consumer");
        ContainerProperties containerProperties = new ContainerProperties(getTopic());
        containerProperties.setMessageListener(this);
        containerProperties.setKafkaConsumerProperties(getConsumerProperties());
        // 每处理完业务手动调用Acknowledgment.acknowledge()后立即提交
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setPollTimeout(kafka.getPollTimeout());

        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerConfigMap());
        ConcurrentMessageListenerContainer<String, String> listenerContainer = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        // 默认设置1次重试，业务异常不会触发重试，业务异常都在SimpleApplicationEventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER)中处理了
        // 这个重试只是防止网络抖动，ack提交失败。
        int retry = Math.max(this.kafka.getRetry(), this.kafka.getMaxRetry());
        retryConfig(retry, listenerContainer);
        listenerContainer.setConcurrency(this.kafka.getConcurrency());
        log.info("初始化分布式事件kafka consumer完毕");
        return listenerContainer;
    }

    /**
     * 设置kafka自动重试
     *
     * @param retry             重试次数
     * @param listenerContainer listener
     */
    private void retryConfig(int retry, ConcurrentMessageListenerContainer<String, String> listenerContainer) {
        // 设置重试次数
        ExponentialBackOffWithMaxRetries backOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(retry);

        // 每次间隔时间递增1.5倍，最大间隔时间为maxInterval
        // 如下配置重试的间隔时间为：500，500*1.5=750，750*1.5.....，180000,180000
        backOffWithMaxRetries.setInitialInterval(500);
        backOffWithMaxRetries.setMultiplier(1.5);
        backOffWithMaxRetries.setMaxInterval(TimeUnit.MINUTES.toMillis(3));

        listenerContainer.setCommonErrorHandler(new DefaultErrorHandler(backOffWithMaxRetries));
    }
}
