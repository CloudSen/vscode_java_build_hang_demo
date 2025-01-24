package cn.cisdigital.datakits.framework.util.sqlparser.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * Description： ApplicationProperties
 *
 * @author xxx
 * @version 1.0.0
 * @date 2022/6/28 09:55
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "datakits.sql.parser")
@Slf4j
public class ConfigProperties {

    private DatasourceProperties datasource = new DatasourceProperties();

    @Bean
    public DatasourceProperties datasourceProperties() {
        return datasource;
    }
}
