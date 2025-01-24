package cn.cisdigital.datakits.framework.util.qbeeopensdk.excption;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;

/**
 * @author xxx
 * @date 2024-07-29-16:41
 */
public class OpenSdkException extends BusinessException {
    public OpenSdkException(String msg) {
        super(msg);
    }

    public OpenSdkException(Throwable e) {
        super(e);
    }

    public OpenSdkException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OpenSdkException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public OpenSdkException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public OpenSdkException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }
}
