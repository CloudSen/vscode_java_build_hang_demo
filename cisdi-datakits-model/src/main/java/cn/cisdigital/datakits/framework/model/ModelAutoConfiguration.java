package cn.cisdigital.datakits.framework.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxx
 * @since 2022-09-20
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.model")
public class ModelAutoConfiguration {

    static {
        log.info("[ 自动装配 ] 加载统一框架model模块");
    }
}
