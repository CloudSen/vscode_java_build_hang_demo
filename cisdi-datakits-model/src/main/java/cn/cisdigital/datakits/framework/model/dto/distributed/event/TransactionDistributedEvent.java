package cn.cisdigital.datakits.framework.model.dto.distributed.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： TransactionDistributedEvent
 * <p>
 * Description： 带事务的分布式事件
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/3 19:20
 */
@Getter
public class TransactionDistributedEvent extends ApplicationEvent {
    private static final long serialVersionUID = -2549295264948994036L;

    private final DistributedEvent event;

    public TransactionDistributedEvent(DistributedEvent event) {
        super(true);
        this.event = event;
    }
}
