package cn.cisdigital.datakits.framework.util.distributed.event.storage;


import  cn.cisdigital.datakits.framework.model.dto.distributed.event.DistributedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： DistributedEventStorage
 * <p>
 * Description： 事件仓库接口类
 *
 * @author xxx
 * @version 1.0.0
 * @since 2022/11/2 19:49
 */

public interface DistributedEventStorage extends InitializingBean, DisposableBean {

    /**
     * 存储
     *
     * @param t   事件
     * @param <T> 范型
     */
    <T extends DistributedEvent> void push(T t);
}
