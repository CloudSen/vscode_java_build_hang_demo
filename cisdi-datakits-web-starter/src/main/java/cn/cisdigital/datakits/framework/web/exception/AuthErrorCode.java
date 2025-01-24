package cn.cisdigital.datakits.framework.web.exception;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 认证相关的错误码
 *
 * @author xxx
 * @since 2024-03-07
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_AUTH("1000", "common.exception.unauthorized"),
    ;

    private final String code;
    private final String key;
}
