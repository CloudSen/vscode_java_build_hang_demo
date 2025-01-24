package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.MAX_EXPIRE_TIME_TO_CACHE_ACCESS;

import cn.cisdigital.datakits.framework.dynamic.datasource.config.RMQConfigure;
import cn.cisdigital.datakits.framework.model.dto.database.MqSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.TestResultDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.tools.admin.MQAdminExt;

/**
 * @author xxx
 * @since 2022-08-19-8:42
 */
@Slf4j
public class RocketMqDynamicProducerHolder {
    /**
     * 默认cache过期时间
     */
    private static Long expireTime = MAX_EXPIRE_TIME_TO_CACHE_ACCESS;
    /**
     * 数据源池子
     */
    private static LoadingCache<MqSourceDto, DefaultMQProducer> mqProducerMapCache;

    static {
        init();
    }

    private static void init() {
        mqProducerMapCache = CacheBuilder.newBuilder()
                                         .expireAfterAccess(expireTime, TimeUnit.MINUTES)
                                         .removalListener(
                                             (RemovalListener<MqSourceDto, DefaultMQProducer>) notification -> {
                                                 DefaultMQProducer producer = notification.getValue();
                                                 if (producer != null) {
                                                     producer.shutdown();
                                                 }
                                                 MqSourceDto sourceDto = notification.getKey();
                                                 log.info(notification.getCause() + "--->  RocketMQ源[ {} ]已被移除...",
                                                          sourceDto == null ? "" : sourceDto.getUrl());
                                             })
                                         .build(new CacheLoader<MqSourceDto, DefaultMQProducer>() {
                                             @Override
                                             public DefaultMQProducer load(MqSourceDto mqSourceDto) throws Exception {
                                                 log.info("新增MQ数据源[ {} ]...", mqSourceDto.getUrl());
                                                 return createDefaultMQProducer(mqSourceDto);
                                             }
                                         });
    }

    public static TestResultDto testConnection(MqSourceDto mqSourceDto) {
        TestResultDto resultDto = new TestResultDto();
        RMQConfigure rmqConfigure = new RMQConfigure();
        rmqConfigure.setAccessKey(mqSourceDto.getUsername());
        rmqConfigure.setNamesrvAddr(mqSourceDto.getUrl());
        rmqConfigure.setSecretKey(mqSourceDto.getPassword());
        MQAdminFactory mqAdminFactory = new MQAdminFactory(rmqConfigure);
        MQAdminExt instance = null;
        try {
            instance = mqAdminFactory.getInstance();
            instance.examineBrokerClusterInfo();
            resultDto.setResult(true);
            return resultDto;
        } catch (Exception e) {
            log.error("MQ数据源[ " + mqSourceDto.getUrl() + " ]连接测试未成功...", e);
            resultDto.setResult(false);
            resultDto.setErrorMsg(e.getMessage());
            return resultDto;
        }finally {
            if(instance != null){
                try {
                    instance.shutdown();
                } catch (Exception e) {
                    log.error("MQ数据源[ " + mqSourceDto.getUrl() + " ]关闭失败...", e);
                }
            }
        }
    }

    public static TopicList getAllTopics(MqSourceDto mqSourceDto) {
        RMQConfigure rmqConfigure = new RMQConfigure();
        rmqConfigure.setAccessKey(mqSourceDto.getUsername());
        rmqConfigure.setNamesrvAddr(mqSourceDto.getUrl());
        rmqConfigure.setSecretKey(mqSourceDto.getPassword());
        MQAdminFactory mqAdminFactory = new MQAdminFactory(rmqConfigure);
        try {
            MQAdminExt instance = mqAdminFactory.getInstance();
            return instance.fetchAllTopicList();
        } catch (Exception e) {
            log.error("MQ数据源[ {} ]获取topic失败...", mqSourceDto.getUrl(),e);
            throw new RuntimeException("MQ数据源连接[ " + mqSourceDto.getUrl() + " ]错误..." + e.getMessage(), e);
        }
    }

    public static DefaultMQProducer getProducer(MqSourceDto mqSourceDto) {
        try {
            return mqProducerMapCache.get(mqSourceDto);
        } catch (ExecutionException e) {
            log.error("MQ数据源连接[ " + mqSourceDto.getUrl() + " ]错误...",e);
            throw new RuntimeException("MQ数据源连接[ " + mqSourceDto.getUrl() + " ]错误..." + e.getMessage(), e);
        }
    }

    public static boolean isExist(MqSourceDto mqSourceDto) {
        return !Objects.isNull(mqProducerMapCache.getIfPresent(mqSourceDto));
    }

    public static void remove(MqSourceDto mqSourceDto) {
        mqProducerMapCache.invalidate(mqSourceDto);
    }

    public static void destroy() {
        if (mqProducerMapCache.size() > 0) {
            log.info("RocketMqDynamicProducerHolder destroy start...");
            mqProducerMapCache.asMap().values().forEach(producer -> {
                log.info("关闭生产者-->NamesrvAddr:[{}]， producerGroup:[{}] ...", producer.getNamesrvAddr(), producer.getProducerGroup());
                producer.shutdown();
            });
            log.info("RocketMqDynamicProducerHolder destroy finish...");
        }
    }

    private static DefaultMQProducer createDefaultMQProducer(MqSourceDto mqSourceDto) throws MQClientException {
        String accessKey = mqSourceDto.getUsername();
        String secretKey = mqSourceDto.getPassword();
        DefaultMQProducer defaultMQProducer;
        if (StringUtils.isNoneBlank(accessKey, secretKey)) {
            SessionCredentials sessionCredentials = new SessionCredentials(accessKey, secretKey);
            RPCHook rpcHook = new AclClientRPCHook(sessionCredentials);
            defaultMQProducer = new DefaultMQProducer(rpcHook);
        } else {
            defaultMQProducer = new DefaultMQProducer();
        }
        defaultMQProducer.setNamesrvAddr(mqSourceDto.getUrl());
        defaultMQProducer.setProducerGroup(mqSourceDto.getProducerGroup());
        defaultMQProducer.start();
        return defaultMQProducer;
    }
}
