package cn.cisdigital.datakits.framework.web.constant;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * web配置常量类
 *
 * @author xxx
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebConstants {

    public static final String WEB_CONFIG_PROPERTIES_PREFIX = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".web";
    public static final String ASYNC_POOL_CONFIG_PROPERTIES_PREFIX = WEB_CONFIG_PROPERTIES_PREFIX + ".async-pool";
    public static final String EXCEPTION_CONFIG_PROPERTIES_PREFIX =
        WEB_CONFIG_PROPERTIES_PREFIX + ".global-exception-handler";
    public static final String MVC_CONFIG_PROPERTIES_PREFIX = WEB_CONFIG_PROPERTIES_PREFIX + ".mvc";
    public static final String SERIALIZER_CONFIG_PROPERTIES_PREFIX = WEB_CONFIG_PROPERTIES_PREFIX + ".serializer";

    //<editor-fold desc="装配常量">

    public static final String LOADING_EXCEPTION_HANDLER_AUTO_CONFIGURE =
        AutoConfigConstants.LOG_PREFIX + "加载默认全局异常处理配置";
    public static final String LOADING_MVC_AUTO_CONFIGURE = AutoConfigConstants.LOG_PREFIX + "加载默认MVC配置";
    public static final String LOADING_HEADER_FILTER = AutoConfigConstants.LOG_PREFIX + "加载Header过滤器";
    public static final String LOADING_XSS_SQL_FILTER = AutoConfigConstants.LOG_PREFIX + "加载XSS&SQL INJECTION 过滤器";
    public static final String LOADING_MDC_FILTER = AutoConfigConstants.LOG_PREFIX + "加载MDC过滤器";
    public static final String LOADING_ASYNC_THREAD_POOL = AutoConfigConstants.LOG_PREFIX + "加载异步任务线程池配置";
    public static final String LOADING_TOKEN_INTERCEPTOR = AutoConfigConstants.LOG_PREFIX + "加载Token拦截器";

    //</editor-fold>

    //<editor-fold desc="开关常量">

    public static final String ENABLE_WEB = AutoConfigConstants.CONFIG_PREFIX + ".enable-web";
    public static final String ENABLE_EXCEPTION_HANDLER = EXCEPTION_CONFIG_PROPERTIES_PREFIX + ".enabled";
    public static final String ENABLE_SERIALIZER = SERIALIZER_CONFIG_PROPERTIES_PREFIX + ".enabled";
    public static final String ENABLE_ASYNC_THREAD_POOL = ASYNC_POOL_CONFIG_PROPERTIES_PREFIX + ".enabled";
    public static final String ENABLE_MVC = MVC_CONFIG_PROPERTIES_PREFIX + ".enabled";
    public static final String ENABLE_XSS_SQL_FILTER = MVC_CONFIG_PROPERTIES_PREFIX + ".xss-sql-filter-enabled";

    //</editor-fold>


    //<editor-fold desc="URL前缀常量">
    public static final String FILTER_EXCEPTION_PREFIX = "/filter/error";
    public static final String API_PREFIX = "/api/";
    public static final String INNER_API_PREFIX = "/innerApi/";
    public static final String OPEN_API_PREFIX = "/openApi/";
    //</editor-fold>

    /**
     * 日志打印参数的分割符
     */
    public static final String LOG_PARAMETER_SEPARATOR = ", ";
}
