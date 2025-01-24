package cn.cisdigital.datakits.framework.mp.properties;

import cn.cisdigital.datakits.framework.mp.MybatisStarterConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * myabtis plus配置参数
 *
 * @author xxx
 * @since 2022-09-21
 */
@Data
@Validated
@ConfigurationProperties(prefix = MybatisStarterConstants.MYBATIS_PLUS_PROPERTIES)
public class MybatisPlusConfigProperties {

    /**
     * 开启分页插件，默认启用
     */
    private Boolean enablePage;

    /**
     * 开启乐观锁插件，默认不启用
     */
    private Boolean enableOptimisticLocker;

    /**
     * 开启防全表删除和更新插件，默认启用
     */
    private Boolean enableBlockAttack;

    /**
     * 开启字段转换拦截器，默认不开启
     */
    private Boolean enableFieldConvertInterceptor;

    /**
     * 制定字段转换拦截器实现类，默认是达梦
     */
    private String fieldConvertInterceptorImpl;


    public MybatisPlusConfigProperties() {
        enablePage = true;
        enableOptimisticLocker = false;
        enableBlockAttack = true;
        enableFieldConvertInterceptor = false;
        fieldConvertInterceptorImpl = "cn.cisdigital.datakits.framework.mp.interceptor.convert.DmDbFieldConvertServiceImpl";
    }
}
