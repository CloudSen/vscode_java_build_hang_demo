package cn.cisdigital.datakits.framework.web;

import cn.cisdigital.datakits.framework.model.properties.RedisKeyProperties;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WebMvc自动装配
 *
 * @author xxx
 * @since 2024-04-10
 */
@EnableRetry
@EnableAsync
@EnableScheduling
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.web")
@EnableConfigurationProperties(RedisKeyProperties.class)
@ConditionalOnProperty(name = WebConstants.ENABLE_WEB, havingValue = "true", matchIfMissing = true)
public class WebAutoConfiguration {

}
