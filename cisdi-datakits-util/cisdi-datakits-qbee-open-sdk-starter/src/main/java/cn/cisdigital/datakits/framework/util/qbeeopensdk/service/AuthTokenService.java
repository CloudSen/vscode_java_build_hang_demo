package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;

/**
 * 认证token服务
 *
 * @author xxx
 */
public interface AuthTokenService {

    /**
     * 校验token，如果需要会刷新token
     *
     * @param token 待校验的token
     * @return 如果存在刷新token则返回最新token，否则返回当前token
     */
    String verifyAndRefreshTokenIfNeed(String token);

    /**
     * 刷新token
     *
     * @param token 待校验的token
     */
    String refreshTokenIfNeed(String token);
}
