package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.MAX_EXPIRE_TIME_TO_CACHE_ACCESS;

import cn.cisdigital.datakits.framework.model.dto.database.MqSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.TestResultDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;

/**
 * @author xxx
 * @since 2024-02-18-9:06
 */
@Slf4j
public class KafkaDynamicProducerHolder {

    /**
     * 默认cache过期时间
     */
    private static Long expireTime = MAX_EXPIRE_TIME_TO_CACHE_ACCESS;

    /**
     * 数据源池子
     */
    private static LoadingCache<MqSourceDto, KafkaProducer> kafkaProducerMapCache;

    private static final String NUM_PARTITIONS = "num.partitions";

    static {
        init();
    }

    private static void init() {
        kafkaProducerMapCache = CacheBuilder.newBuilder()
                .expireAfterAccess(expireTime, TimeUnit.MINUTES)
                .removalListener(
                        (RemovalListener<MqSourceDto, KafkaProducer>) notification -> {
                            KafkaProducer producer = notification.getValue();
                            if (producer != null) {
                                producer.close();
                            }
                            MqSourceDto sourceDto = notification.getKey();
                            log.info(notification.getCause() + "--->  Kafka源[ {} ]已被移除...",
                                    sourceDto == null ? "" : sourceDto.getUrl());
                        })
                .build(new CacheLoader<MqSourceDto, KafkaProducer>() {
                    @Override
                    public KafkaProducer load(MqSourceDto mqSourceDto) throws Exception {
                        log.info("新增MQ数据源[ {} ]...", mqSourceDto.getUrl());
                        return createKafkaProducer(mqSourceDto);
                    }
                });
    }


    public static TestResultDto testConnection(MqSourceDto mqSourceDto) {
        TestResultDto resultDto = new TestResultDto();
        Properties kafkaProperty = getKafkaProperty(mqSourceDto);
        AdminClient adminClient = null;
        try {
            adminClient = AdminClient.create(kafkaProperty);
            adminClient.listTopics().names().get();
            resultDto.setResult(true);
            return resultDto;
        } catch (Exception e) {
            log.error("kafka数据源[ " + mqSourceDto.getUrl() + " ]连接测试未成功...", e);
            resultDto.setResult(false);
            resultDto.setErrorMsg(e.getMessage());
            return resultDto;
        } finally {
            if (adminClient != null) {
                adminClient.close();
            }
        }
    }

    public static <K, V> KafkaProducer<K, V> getProducer(MqSourceDto mqSourceDto) {
        try {
            return kafkaProducerMapCache.get(mqSourceDto);
        } catch (ExecutionException e) {
            log.error("Kafka数据源连接[ " + mqSourceDto.getUrl() + " ]错误...", e);
            throw new RuntimeException("Kafka数据源连接[ " + mqSourceDto.getUrl() + " ]错误..." + e.getMessage(), e);
        }
    }

    public static Set<String> getAllTopics(MqSourceDto mqSourceDto) {
        Properties kafkaProperty = getKafkaProperty(mqSourceDto);
        try (AdminClient adminClient = AdminClient.create(kafkaProperty)) {
            return getAllTopics(adminClient, mqSourceDto.getUrl());
        }
    }

    private static Set<String> getAllTopics(AdminClient adminClient, String url) {
        try {
            // 列出所有的 topics 并且排除内部 topics
            ListTopicsOptions options = new ListTopicsOptions().listInternal(false);
            ListTopicsResult topicsResult = adminClient.listTopics(options);
            KafkaFuture<Set<String>> topicNamesFuture = topicsResult.names();
            return topicNamesFuture.get();
        } catch (Exception e) {
            log.error("Kafka数据源[ {} ]获取topic失败...", url, e);
            throw new RuntimeException("Kafka数据源连接[ " + url + " ]错误..." + e.getMessage(), e);
        }
    }

    public static int getKafkaPartitionCount(MqSourceDto mqSourceDto, String topicName) {
        Properties kafkaProperty = getKafkaProperty(mqSourceDto);
        String url = mqSourceDto.getUrl();
        try (AdminClient adminClient = AdminClient.create(kafkaProperty)) {
            Set<String> allTopics = getAllTopics(adminClient, url);
            if (CollectionUtils.isNotEmpty(allTopics) && allTopics.contains(topicName)) {
                TopicDescription topicDescription = getTopicDescription(topicName, adminClient, url);
                return topicDescription.partitions().size();
            } else {
                return Integer.parseInt(getBrokerConfig(adminClient, NUM_PARTITIONS, url));
            }
        }
    }

    private static TopicDescription getTopicDescription(String topicName, AdminClient adminClient, String url) {
        try {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(topicName));
            return describeTopicsResult.values().get(topicName).get();
        } catch (Exception e) {
            log.error("Kafka数据源[ {} ]获取TopicDescription失败...", url, e);
            throw new RuntimeException("Kafka数据源获取TopicDescription[ " + url + " ]错误..." + e.getMessage(), e);
        }
    }


    private static String getBrokerConfig(AdminClient adminClient, String key, String url) {
        try {
            DescribeClusterResult describeClusterResult = adminClient.describeCluster();
            Collection<Node> nodes = describeClusterResult.nodes().get();
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, nodes.iterator().next().idString());
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Collections.singleton(configResource));
            Config nodeConfig = describeConfigsResult.values().get(configResource).get();
            return nodeConfig.get(key).value();
        } catch (Exception e) {
            log.error("Kafka数据源[ {} ]获取BrokerConfig失败...", url, e);
            throw new RuntimeException("Kafka数据源获取BrokerConfig[ " + url + " ]错误..." + e.getMessage(), e);
        }
    }

    public static boolean isExist(MqSourceDto mqSourceDto) {
        return !Objects.isNull(kafkaProducerMapCache.getIfPresent(mqSourceDto));
    }

    public static void remove(MqSourceDto mqSourceDto) {
        kafkaProducerMapCache.invalidate(mqSourceDto);
    }

    public static void destroy() {
        if (kafkaProducerMapCache.size() > 0) {
            log.info("KafkaDynamicProducerHolder destroy start...");
            kafkaProducerMapCache.asMap().values().forEach(KafkaProducer::close);
            log.info("RocketMqDynamicProducerHolder destroy finish...");
        }
    }


    private static KafkaProducer createKafkaProducer(MqSourceDto mqSourceDto) {
        // 创建 Kafka 生产者实例
        return new KafkaProducer<>(getKafkaProperty(mqSourceDto));
    }

    private static Properties getKafkaProperty(MqSourceDto mqSourceDto) {
        String accessKey = mqSourceDto.getUsername();
        String secretKey = mqSourceDto.getPassword();
        // 创建生产者属性
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, mqSourceDto.getUrl());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
            //todo Kafka 服务器端配置觉得加密方式 这里待定
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + accessKey + "\" password=\"" + secretKey + "\";");
        }
        return props;
    }
}
