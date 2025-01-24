package cn.cisdigital.datakits.framework.util.distributed.event;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.util.distributed.event")
public class DistributedEventAutoConfiguration {
    static {
        log.info(AutoConfigConstants.LOG_PREFIX + "加载分布式事件模块");
    }
}
