package cn.cisdigital.datakits.framework.common.identity;

import cn.cisdigital.datakits.framework.common.constant.HeaderConstants;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 封装从请求头中提取出来的用户标识信息的容器
 *
 * @author xxx
 */
@Slf4j
@UtilityClass
public class AuthHolder {

    /**
     * 封装从请求头中提取出来的用户标识信息
     * <p>多线程环境，使用TtlExecutor包装线程池</p>
     */
    private static final TransmittableThreadLocal<UserIdentity> userIdentityHolder = new TransmittableThreadLocal<>();

    /**
     * 清理本次请求解析的所有用户信息标识
     *
     * @apiNote 基于ThreadLocal做的保存, 所以必须在请求结束时调用此方法进行清理
     */
    public static void clear() {
        userIdentityHolder.remove();
    }

    /**
     * 从ThreadLocal中取出用户标识信息
     *
     * @return 用户标识信息, 此方法绝对不会返回null
     * @apiNote 如果里面还没有用户标识信息就初始化一个空的进去
     */
    private static UserIdentity getOrInitUserIdentity() {
        UserIdentity userIdentity = userIdentityHolder.get();
        if (userIdentity == null) {
            userIdentityHolder.set(new UserIdentity());
        }

        return userIdentityHolder.get();
    }

    public static String getRequestId() {
        return getOrInitUserIdentity().getRequestId();
    }

    public static void setRequestId(String requestId) {
        userIdentityHolder.get().setRequestId(requestId);
    }

    public static String getAppId() {
        return getOrInitUserIdentity().getAppId();
    }

    public static void setAppId(String appId) {
        userIdentityHolder.get().setAppId(appId);
    }

    public static String getAppKey() {
        return getOrInitUserIdentity().getAppKey();
    }

    public static void setApplicationName(String applicationName) {
        userIdentityHolder.get().setApplicationName(applicationName);
    }

    public static String getApplicationName() {
        return getOrInitUserIdentity().getApplicationName();
    }

    public static void setAppKey(String appKey) {
        userIdentityHolder.get().setAppKey(appKey);
    }

    public static String getUserId() {
        return getOrInitUserIdentity().getUserId();
    }

    public static void setUserId(String userId) {
        userIdentityHolder.get().setUserId(userId);
    }

    public static String getEmployeeId() {
        return getOrInitUserIdentity().getEmployeeId();
    }

    public static void setEmployeeId(String employeeId) {
        userIdentityHolder.get().setEmployeeId(employeeId);
    }

    public static String getPartyId() {
        return getOrInitUserIdentity().getPartyId();
    }

    public static void setPartyId(String partyId) {
        userIdentityHolder.get().setPartyId(partyId);
    }

    public static String getEmployeeName() {
        if (CharSequenceUtil.isBlank(getOrInitUserIdentity().getEmployeeName())) {
            return CharSequenceUtil.EMPTY;
        }
        try {
            return  URLDecoder.decode(getOrInitUserIdentity().getEmployeeName(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
           log.error("common.log.employee_name_codec_error");
        }
        return CharSequenceUtil.EMPTY;
    }

    public static void setEmployeeName(String employeeName) {
        userIdentityHolder.get().setEmployeeName(employeeName);
    }

    public static String getAccount() {
        return getOrInitUserIdentity().getAccount();
    }

    public static void setAccount(String account) {
        userIdentityHolder.get().setAccount(account);
    }

    public static boolean isAdmin() {
        return getOrInitUserIdentity().isAdmin();
    }

    public static void setAdmin(boolean isAdmin) {
        userIdentityHolder.get().setAdmin(isAdmin);
    }

    /**
     * 从http请求中获得用户信息
     *
     * @param request HTTP请求
     */
    public static void initUserIdentityFromRequest(HttpServletRequest request) {
        UserIdentity userIdentity = getOrInitUserIdentity();
        String adminStr = request.getHeader(HeaderConstants.HEADER_SUPER_ADMIN);
        userIdentity.setRequestId(request.getHeader(HeaderConstants.HEADER_REQUEST_ID));
        userIdentity.setEmployeeId(request.getHeader(HeaderConstants.HEADER_EMPLOYEE_ID));
        userIdentity.setPartyId(request.getHeader(HeaderConstants.HEADER_PARTY_ID));
        userIdentity.setAppId(request.getHeader(HeaderConstants.HEADER_APP_ID));
        userIdentity.setAppKey(request.getHeader(HeaderConstants.HEADER_APP_KEY));
        userIdentity.setAccount(request.getHeader(HeaderConstants.HEADER_ACCOUNT));
        userIdentity.setEmployeeName(request.getHeader(HeaderConstants.HEADER_EMPLOYEE_NAME));
        userIdentity.setAdmin(!CharSequenceUtil.isBlank(adminStr) && Boolean.parseBoolean(adminStr));
        userIdentity.setUserId(request.getHeader(HeaderConstants.HEADER_USER_ID));
        userIdentity.setApplicationName(request.getHeader(HeaderConstants.HEADER_APPLICATION_NAME));
        userIdentityHolder.set(userIdentity);
    }

    /**
     * 认证信息是否无效
     * <p>
     * 有效的条件是：请求头中的employeeId、appId不能同时为空
     * </p>
     *
     * @return true: 认证无效 false：认证有效
     */
    public static boolean isInvalid() {
        String employeeId = getOrInitUserIdentity().getEmployeeId();
        String appId = getOrInitUserIdentity().getAppId();
        String appKey = getOrInitUserIdentity().getAppKey();
        return CharSequenceUtil.isAllBlank(employeeId, appId, appKey);
    }
}
