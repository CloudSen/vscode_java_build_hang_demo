package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HikariPoolProperties.class)
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.dynamic.datasource.config")
public class DynamicDataSourceAutoConfigurationTest {

}
