package cn.cisdigital.datakits.framework.dynamic.datasource;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.cisdigital.datakits.framework.dynamic.datasource.config.HikariPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxx
 * @since 2024-03-11
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HikariPoolProperties.class)
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.dynamic.datasource")
public class DynamicDataSourceAutoConfiguration {

    static {
      log.info(AutoConfigConstants.LOG_PREFIX + "加载多源异构数据库引擎模块");
    }
}
