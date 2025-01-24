package cn.cisdigital.datakits.framework.mp;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2022-10-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MybatisStarterConstants {

    public static final String ENABLE_MYBATIS = AutoConfigConstants.CONFIG_PREFIX + ".enable-mybatis";
    public static final String MYBATIS_PLUS_PROPERTIES = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".mp";
    public static final String MYBATIS_PLUS_INTERCEPTOR_PROPERTIES =
        AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".mp.enable-field-convert-interceptor";

    public static final String LOADING_TRANSACTION_AUTO_CONFIGURE = AutoConfigConstants.LOG_PREFIX + "加载MP事务配置";
    public static final String LOADING_MYBATIS_PLUS_AUTO_CONFIGURE = AutoConfigConstants.LOG_PREFIX + "加载MP配置";
    public static final String LOADING_MYBATIS_PLUS_PAGE_PLUGIN = AutoConfigConstants.LOG_PREFIX + "加载MP分页插件";
    public static final String LOADING_MYBATIS_PLUS_LOCK_PLUGIN = AutoConfigConstants.LOG_PREFIX + "加载MP乐观锁插件";
    public static final String LOADING_MYBATIS_PLUS_ATTACK_PLUGIN = AutoConfigConstants.LOG_PREFIX + "加载MP防全表更新和删除插件";
    public static final String LOADING_MYBATIS_PLUS_MAPPERS = AutoConfigConstants.LOG_PREFIX + "加载MP Mappers: {}";
    public static final String LOADING_MYBATIS_FIELD_INTERCEPTOR = AutoConfigConstants.LOG_PREFIX + "加载MP 字段拦截器： {}";
}
