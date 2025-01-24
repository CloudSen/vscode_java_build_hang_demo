package cn.cisdigital.datakits.framework.web.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;

/**
 * 认证异常
 *
 * @author xxx
 * @since 2024-03-07
 */
public class AuthException extends BusinessException {

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(Throwable e) {
        super(e);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public AuthException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public AuthException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }
}
