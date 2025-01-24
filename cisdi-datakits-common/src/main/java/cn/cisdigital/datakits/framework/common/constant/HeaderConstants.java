package cn.cisdigital.datakits.framework.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * header常量
 *
 * @author xxx
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    public static final String HEADER_X_REAL_IP = "X-Real-IP";

    public static final String HEADER_HOST = "Host";

    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    public static final String HEADER_X_CLIENT_IP = "x-client-ip";

    public static final String TOKEN_HEADER_NAME = "Authorization";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * xxl-job请求token
     */
    public static final String COOKIE = "Cookie";

    /**
     * xxl-job请求token
     */
    public static final String XXL_JOB_TOKEN_HEADER = "XXL-JOB-ACCESS-TOKEN";

    /**
     * auth gateway内部认证token
     */
    public static final String HEADER_ACCESS_TOKEN = "access-token";

    /**
     * 请求id
     */
    public static final String HEADER_REQUEST_ID = "request-id";

    /**
     * header里的用户id
     */
    public static final String HEADER_USER_ID = "userId";

    /**
     * header里的团队id
     */
    public static final String HEADER_PARTY_ID = "partyId";

    /**
     * header里的员工id
     */
    public static final String HEADER_EMPLOYEE_ID = "employeeId";

    /**
     * header里的员工姓名
     */
    public static final String HEADER_EMPLOYEE_NAME = "employeeName";

    /**
     * header里的员工帐号
     */
    public static final String HEADER_ACCOUNT = "account";

    /**
     * 应用id
     */
    public static final String HEADER_APP_ID = "appId";

    /**
     * 应用key
     */
    public static final String HEADER_APP_KEY = "appKey";

    /**
     * header里的superAdmin，是否为超级管理员
     */
    public static final String HEADER_SUPER_ADMIN = "super-admin";

    /**
     * 微服务应用名
     */
    public static final String HEADER_APPLICATION_NAME = "application-name";

    /**
     * 开放网关，认证header
     */
    public static final String HEADER_X_AUTHORIZATION = "x-c-authorization";

    /**
     * 开放网关，时间认证header
     */
    public static final String HEADER_X_C_DATETIME = "x-c-datatime";

    /**
     * 必要的header
     */
    public static final List<String> NECESSARY_HEADERS = Arrays.asList(HEADER_REQUEST_ID, HEADER_USER_ID,
            HEADER_PARTY_ID, HEADER_EMPLOYEE_ID, HEADER_EMPLOYEE_NAME, HEADER_ACCOUNT, HEADER_APP_ID,
            HEADER_SUPER_ADMIN, HEADER_APPLICATION_NAME);

    /**
     * 打印日志需要忽略的header
     */
    public static final List<String> LOG_IGNORE_HEADERS = Arrays.asList(COOKIE, TOKEN_HEADER_NAME, HEADER_ACCESS_TOKEN,
            XXL_JOB_TOKEN_HEADER);
}
