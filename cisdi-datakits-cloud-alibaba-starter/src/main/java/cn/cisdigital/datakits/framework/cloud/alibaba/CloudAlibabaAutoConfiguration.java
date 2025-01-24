package cn.cisdigital.datakits.framework.cloud.alibaba;

import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RemoteCountProperties;
import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xxx
 * @since 2022-09-20
 */
@Slf4j
@Configuration
@EnableFeignClients(basePackages = {
    "cn.cisdigital.datakits",
    "org.apache.dolphinscheduler",
    "cn.cisdigital.datagovernance",
    "cn.cisdigital.datapulse",
    "com.xxl.job.core",
})
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.cloud.alibaba")
@ConditionalOnClass(FeignClient.class)
@EnableConfigurationProperties({RemoteCountProperties.class})
@RequiredArgsConstructor
public class CloudAlibabaAutoConfiguration {

    static {
        log.info(AutoConfigConstants.LOG_PREFIX + "加载Cloud Alibaba配置");
    }
}
