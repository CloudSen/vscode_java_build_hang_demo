package cn.cisdigital.datakits.framework.mp;

import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.mp.handler.CustomMetaObjectHandler;
import cn.cisdigital.datakits.framework.mp.properties.MybatisPlusConfigProperties;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus自动装配
 *
 * @author xxx
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.mp")
@MapperScan(basePackages = {"cn.cisdigital.**.repository.**"})
@EnableConfigurationProperties({MybatisPlusConfigProperties.class})
@ConditionalOnProperty(name = MybatisStarterConstants.ENABLE_MYBATIS, havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({HikariDataSource.class, MybatisConfiguration.class})
public class DefaultMybatisPlusAutoConfiguration {

    static {
        // 统一 Jackson 对象
        JacksonTypeHandler.setObjectMapper(JsonUtils.OBJECT_MAPPER);
    }

    public DefaultMybatisPlusAutoConfiguration() {
        log.info(MybatisStarterConstants.LOADING_MYBATIS_PLUS_AUTO_CONFIGURE);
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor paginationInterceptor(MybatisPlusConfigProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (BooleanUtil.isTrue(properties.getEnablePage())) {
            // 分页
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
            log.info(MybatisStarterConstants.LOADING_MYBATIS_PLUS_PAGE_PLUGIN);
        }
        if (BooleanUtil.isTrue(properties.getEnableOptimisticLocker())) {
            // 乐观锁
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
            log.info(MybatisStarterConstants.LOADING_MYBATIS_PLUS_LOCK_PLUGIN);
        }
        if (BooleanUtil.isTrue(properties.getEnableBlockAttack())) {
            // 防全表删除和更新
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
            log.info(MybatisStarterConstants.LOADING_MYBATIS_PLUS_ATTACK_PLUGIN);
        }
        return interceptor;
    }

    /**
     * 实体类自动填充
     */
    @Bean
    public CustomMetaObjectHandler customMetaObjectHandler() {
        return new CustomMetaObjectHandler();
    }
}
