package cn.cisdigital.datakits.framework.model.dto.distributed.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.ApplicationEvent;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： DistributedEvent
 * Description： 分布式事件
 * <p>1. 不要将信息赋值在source中，不会持久化
 * <p>2. 必须提供无参的构造方法
 * <p>4. 当进行反序列话时，JSON数据匹配的对象可能有多个子类型，为了正确的读取对象的类型，使用@JsonTypeInfo，指定根据class name区分
 * <p>5. 跨服务时，确保DistributedEvent子类的路径一致，并存放在cn.cisdigital路径下，否则消费方无法收到事件
 * <p>6、分布式事件消费时，若中间有流程抛异常，异常会被打印，消息仍然会被消费掉，客户端需自己实现重试
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/2 19:38
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className")
public class DistributedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -8645138397694590663L;

    public DistributedEvent() {
        super(true);
    }

}
