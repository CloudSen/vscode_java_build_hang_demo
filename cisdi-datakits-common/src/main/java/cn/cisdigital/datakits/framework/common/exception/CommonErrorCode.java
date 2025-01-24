package cn.cisdigital.datakits.framework.common.exception;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通用错误码
 *
 * @author xxx
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    /**
     * 系统未知错误 500
     */
    INTERNAL_ERROR("-1", "common.exception.internal_error"),

    /**
     * 无法获取用户信息，请确保token的有效性 80001
     */
    TOKEN_INVALIDED("80001", "common.exception.token_invalided"),

    /**
     * 认证失败 80002
     */
    UNAUTHORIZED("80002", "common.exception.unauthorized"),

    /**
     * 无操作权限 80003
     */
    ACCESS_DENIED("80003", "common.exception.access_denied"),

    /**
     * 参数错误 80004
     */
    PARAM_ERROR("80004", "common.exception.param_error"),
    ;

    private final String code;
    private final String key;


    public static String parseKey(String code) {
        return Arrays.stream(values())
            .filter(e -> CharSequenceUtil.equals(e.code, code))
            .findAny()
            .map(CommonErrorCode::getKey)
            .orElse("common.exception.internal_error");
    }

    public static ErrorCode parseEnum(String code) {
        return Arrays.stream(values())
            .filter(e -> CharSequenceUtil.equals(e.code, code))
            .findAny()
            .orElse(INTERNAL_ERROR);
    }
}
