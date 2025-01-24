package cn.cisdigital.datakits.framework.util.distributed.event.configuration;


import cn.cisdigital.datakits.framework.util.distributed.event.declare.enums.MQType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Properties;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： EventManageProperties
 * <p>
 * Description：事件管理配置类，目前支持kafka和rocketmq作为消息中间件
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/3 9:15
 */
@Data
@ConfigurationProperties(prefix = DistributedEventManageProperties.PREFIX)
public class DistributedEventManageProperties {

    public static final String PREFIX = "datakits.sdk.distributed-event";

    /**
     * 队列类型，默认是kafka
     */
    private MQType mqType = MQType.KAFKA;

    /**
     * 分布式事件开关，默认为false
     * 设置为true后，可以从kafka队列中消费到{@link  cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent}的子类
     */
    private boolean consumeEnable = false;

    /**
     * 当前应用是否需要开启分布式事件推送，默认是true
     * 设置为true后,{@link  cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent}的子类，才会发送到配置的kafka队列中
     * 如果当前应用只是一个分布式事件的消费端，可以设置为false，就不会启动一个kafka producer
     */
    private boolean producerEnable = true;

    /**
     * mq配置
     */
    private Kafka kafka = new Kafka();

    /**
     * rocketMq
     */
    private RocketMQ rocketMq = new RocketMQ();

    @Data
    public static class RocketMQ{
        /**
         * 尽量保持默认
         */
        private String topic = "datakits-distributed-event-topic";

        /**
         * 并行度消费
         */
        private int concurrency = 1;

        /**
         * 重试次数,目前只重试ack提交失败，最少为1次
         */
        private int retry = 1;

        /**
         * 最大重试次数
         */
        private int maxRetry = 3;

        /**
         * mq地址
         */
        private String address;

        /**
         * tag
         */
        private String tag =  "*";

        /**
         * 消费组id,建议设置为应用名+环境名，避免重复
         */
        private String groupId;

        /**
         * 推送超时时间
         */
        private int sendTimeOut = 10000;
    }

    @Data
    public static class Kafka {
        /**
         * 尽量保持默认
         */
        private String topic = "datakits-distributed-event-topic";

        /**
         * 并行度消费
         */
        private int concurrency = 1;

        /**
         * 重试次数,目前只重试ack提交失败，最多为3次
         */
        private int retry = 1;

        /**
         * 最大重试次数
         */
        private int maxRetry = 3;

        /**
         * mq地址
         */
        private String address;

        /**
         * 消费组id,建议设置为应用名+环境名，避免重复
         */
        private String groupId;

        /**
         * 消费者等待消息时阻塞超时事件
         */
        private long pollTimeout = ConsumerProperties.DEFAULT_POLL_TIMEOUT;
        /**
         * 生产者信息
         */
        private Properties producer = new Properties();
        /**
         * 消费者信息
         */
        private Properties consumer = new Properties();
    }
}
