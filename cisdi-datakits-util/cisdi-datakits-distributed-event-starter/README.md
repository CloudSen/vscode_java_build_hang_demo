分布式事件SDK
================================

介绍
------------
将SpringBoot中的事件机制与消息队列组件结合，实现跨服务的分布式事件生产、消费。

注意事项
------------
1. 适用分钟级产生的业务事件，禁止高频秒级事件接入。
2. 所有事件共用1个Topic，消费端串行消费，业务逻辑复杂时，可以考虑异步处理。
3. 目前消息队列类型仅支持kafka

接入流程
------------
确保nacos上public-datakits-config.yaml配置文件中有如下配置：
````
datakits:
  sdk:
    distributed-event:
      kafka:
        address: ${public.kafka.bootstrap-server}
        groupId: ${spring.application.name}_${spring.profiles.active}
        topic: datakits-distributed-event-topic-${spring.profiles.active}
````
业务配置文件中加入如下配置：
````
datakits:
 sdk:
  distributed-event:
   # 开启分布式消费
   consumeEnable: true
   # 开启分布式生产
   producerEnable: true
````
参考example包下样例使用
