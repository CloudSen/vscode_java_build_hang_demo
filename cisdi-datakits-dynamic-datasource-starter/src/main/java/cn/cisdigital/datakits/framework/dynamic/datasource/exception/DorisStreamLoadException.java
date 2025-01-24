package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;

/**
 * Doris SteamLoad 异常
 *
 * @author xxx
 * @since 2024-05-11
 */
public class DorisStreamLoadException extends BusinessException {

    public DorisStreamLoadException(String msg) {
        super(msg);
    }

    public DorisStreamLoadException(Throwable e) {
        super(e);
    }

    public DorisStreamLoadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DorisStreamLoadException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public DorisStreamLoadException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public DorisStreamLoadException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }
}
