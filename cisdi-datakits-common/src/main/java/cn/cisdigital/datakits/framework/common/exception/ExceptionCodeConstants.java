package cn.cisdigital.datakits.framework.common.exception;

/**
 * 异常code = SERVICE_CODE + X_MODULE_CODE + ERROR_CODE
 */
public class ExceptionCodeConstants {

    /**
     * 服务编码 000
     */
    public static final String SERVICE_CODE = "000";

    /**
     * web starter模块编码 001
     */
    public static final String WEB_STARTER_MODULE_CODE = SERVICE_CODE + "001";

    /**
     * cache starter模块编码 002
     */
    public static final String CACHE_STARTER_MODULE_CODE = SERVICE_CODE + "002";

    /**
     * datasource starter模块编码 003
     */
    public static final String DATASOURCE_STARTER_MODULE_CODE = SERVICE_CODE + "003";

    /**
     * workflow starter模块编码 004
     */
    public static final String WORKFLOW_STARTER_MODULE_CODE = SERVICE_CODE + "004";

    /**
     * opensdk starter模块编码 005
     */
    public static final String OPENSDK_STARTER_MODULE_CODE = SERVICE_CODE + "005";

    /**
     * storage starter模块编码 006
     */
    public static final String STORAGE_STARTER_MODULE_CODE = SERVICE_CODE + "006";

    /**
     * minio starter模块编码 007
     */
    public static final String MINIO_STARTER_MODULE_CODE = SERVICE_CODE + "007";

    /**
     * crypto starter模块编码 008
     */
    public static final String CRYPTO_STARTER_MODULE_CODE = SERVICE_CODE + "008";
}
