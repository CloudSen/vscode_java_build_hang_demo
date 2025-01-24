package cn.cisdigital.datakits.framework.util.distributed.event.example;


import cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： ExampleEvent
 * <p>
 * <p>1. 不要将信息赋值在source中，不会持久化
 * <p>2. 必须提供无参的构造方法
 * <p>3. 跨服务时，确保DistributedEvent子类的路径一致，并存放在cn.cisdigital路径下，否则消费方无法收到事件
 * <p>4. 分布式事件消费时，若中间有流程抛异常，异常会被打印，消息仍然会被消费掉，客户端需自己实现重试
 * <p>5. 适用分钟级产生的业务事件，禁止高频秒级事件接入。
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/3 19:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExampleEvent extends DistributedEvent {

    private static final long serialVersionUID = -3968080722574626248L;

    private String name;

    private long id;

    public ExampleEvent() {

    }

    public ExampleEvent(String name, long id) {
        super();
        this.name = name;
        this.id = id;
    }
}
