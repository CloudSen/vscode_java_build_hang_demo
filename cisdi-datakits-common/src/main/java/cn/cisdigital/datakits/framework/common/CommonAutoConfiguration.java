package cn.cisdigital.datakits.framework.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxx
 * @since 2022-09-20
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.common")
public class CommonAutoConfiguration {

    static {
        log.info(AutoConfigConstants.LOG_PREFIX + "加载统一框架common模块");
    }
}
