package cn.cisdigital.datakits.framework.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 自动装配常量
 *
 * @author xxx
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoConfigConstants {

    /**
     * 自动装配日志前缀
     */
    public static final String LOG_PREFIX = "[ 自动装配 ] ";

    /**
     * 自动装配开关
     */
    public static final String CONFIG_PREFIX = "cisdi.autoconfiguration";

    /**
     * 动态数据源配置前缀
     */
    public static final String DYNAMIC_DS_CONFIG_PREFIX = "dynamic-ds";

    /**
     * Properties配置类前缀
     */
    public static final String CONFIG_PROPERTIES_PREFIX = "datakits.default-config";
}
