package cn.cisdigital.datakits.framework.mp;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xxx
 */
@Slf4j
@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableTransactionManagement
public class MpTransactionAutoConfiguration {

    public MpTransactionAutoConfiguration() {
        log.info(MybatisStarterConstants.LOADING_TRANSACTION_AUTO_CONFIGURE);
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
