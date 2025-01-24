//package cn.cisdigital.datakits.framework.util.qbeetoken.service.impl;
//
//import cn.cisdigital.datakits.framework.common.interfaces.Executable;
//import cn.cisdigital.datakits.framework.util.qbeetoken.AuthTokenAutoConfiguration;
//import cn.cisdigital.datakits.framework.util.qbeetoken.constants.AuthTokenConstants;
//import cn.cisdigital.datakits.framework.util.qbeetoken.exception.AuthTokenException;
//import cn.cisdigital.datakits.framework.util.qbeetoken.service.AuthTokenService;
//import cn.cisdigital.datakits.framework.util.qbeetoken.utils.AuthTokenJwtUtils;
//import cn.cisdigital.datakits.framework.util.qbeetoken.utils.AuthTokenUtils;
//import cn.cisdigital.framework.qbee.starter.cache.abs.CacheKeyProvider;
//import cn.cisdigital.framework.qbee.starter.cache.abs.CacheService;
//import cn.hutool.core.text.CharSequenceUtil;
//import java.time.Duration;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.stereotype.Service;
//
///**
// * 认证token服务接口
// *
// * @author xxx
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@AutoConfigureAfter(AuthTokenAutoConfiguration.class)
//public class AuthTokenServiceImpl implements AuthTokenService {
//
//    private final CacheService cacheService;
//
//    @Override
//    public String verifyAndRefreshTokenIfNeed(String token) {
//        String tokenExceptPrefix = AuthTokenUtils.exceptTokenPrefix(token);
//        AuthTokenUtils.verifyToken(tokenExceptPrefix);
//        return refreshTokenIfNeed(tokenExceptPrefix);
//    }
//
//    @Override
//    public String refreshTokenIfNeed(String token) {
//        long currentTimeMillis = System.currentTimeMillis();
//        String currentToken = token;
//        String userId = AuthTokenJwtUtils.getClaim(currentToken, AuthTokenConstants.ACCOUNT_ID);
//        boolean needRefresh = Boolean.parseBoolean(Optional.ofNullable(AuthTokenJwtUtils.getClaim(currentToken, AuthTokenConstants.NEED_REFRESH)).orElse(String.valueOf(true)));
//        // 不需要刷新则返回当前token
//        if (!needRefresh) {
//            return currentToken;
//        }
//        String partyId = null;
//        boolean tokenLenValid = CharSequenceUtil.isNotBlank(currentToken)
//            && currentToken.length()
//            - currentToken.replaceAll(AuthTokenConstants.POINT_REGX, CharSequenceUtil.EMPTY).length()
//            == AuthTokenConstants.TOKEN_LENGTH;
//        if (tokenLenValid) {
//            partyId = AuthTokenJwtUtils.decode(currentToken.substring(currentToken.lastIndexOf(AuthTokenConstants.POINT_STRING) + 1));
//            currentToken = currentToken.substring(0, currentToken.lastIndexOf(AuthTokenConstants.POINT_STRING));
//        }
//        //检查刷新规则
//        if (AuthTokenUtils.refreshCheck(currentToken, currentTimeMillis)) {
//            String lockName = AuthTokenConstants.PREFIX_SHIRO_REFRESH_CHECK + userId;
//            String finalCurrentToken = currentToken;
//            String finalPartyId = partyId;
//            final Executable executable = () -> refreshToken(finalCurrentToken, userId, currentTimeMillis, finalPartyId, token);
//            try {
//                cacheService.doInLock(lockName, 5L, 10L, executable);
//            } catch (InterruptedException ie) {
//                log.error("[ AUTH TOKEN ] 获取锁超时", ie);
//                Thread.currentThread().interrupt();
//            } catch (Exception e) {
//                throw new AuthTokenException(e);
//            }
//        }
//        return token;
//    }
//
//    private String refreshToken(String currentToken, String userId,
//                                long currentTimeMillis, String partyId, String token) {
//        try {
//            //获取到锁
//            CacheKeyProvider refreshTokenKey = new CacheKeyProvider(
//                AuthTokenConstants.REDIS_KE_REFRESH_TOKEN, userId) {
//                @Override
//                public boolean cacheNull() {
//                    return true;
//                }
//            };
//            if (cacheService.exists(refreshTokenKey)) {
//                //检查redis中的时间戳与token的时间戳是否一致
//                String tokenTimeStamp = cacheService.get(refreshTokenKey);
//                String tokenMillis = AuthTokenJwtUtils.getClaim(currentToken,
//                    AuthTokenConstants.CURRENT_TIME_MILLIS);
//                if (tokenMillis == null || !tokenMillis.equals(tokenTimeStamp)) {
//                    throw new AuthTokenException(String.format("[ AUTH TOKEN ] 账户%s的令牌无效", userId));
//                }
//            }
//            //时间戳一致，则颁发新的令牌
//            String strCurrentTimeMillis = String.valueOf(currentTimeMillis);
//            String newToken = AuthTokenJwtUtils.sign(userId, strCurrentTimeMillis, partyId);
//            //更新缓存中的token时间戳
//            cacheService.set(refreshTokenKey, strCurrentTimeMillis);
//            cacheService.expire(refreshTokenKey, Duration.ofMinutes(AuthTokenConstants.TOKEN_EXPIRE_TIME));
//            log.info("[ AUTH TOKEN ] 更新token成功,userId={},oldTokenParam={},ewTokenParam={}", userId, AuthTokenUtils.getTokenParameter(token), AuthTokenUtils.getTokenParameter(newToken));
//            return newToken;
//        } catch (Exception ex) {
//            log.error("[ AUTH TOKEN ] 检查刷新规则异常", ex);
//        }
//        return token;
//    }
//
//}
