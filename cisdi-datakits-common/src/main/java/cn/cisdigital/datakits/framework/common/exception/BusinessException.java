package cn.cisdigital.datakits.framework.common.exception;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import lombok.Getter;

/**
 * 业务异常类
 *
 * @author xxx
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(String msg) {
        super(I18nUtils.getOriginMessageIfNotExits(msg));
        this.errorCode = CommonErrorCode.INTERNAL_ERROR;
    }

    public BusinessException(Throwable e) {
        super(e);
        this.errorCode = CommonErrorCode.INTERNAL_ERROR;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg(null));
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode.getMsg(msgFormats));
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable e) {
        super(errorCode.getMsg(null), e);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode.getMsg(msgFormats), e);
        this.errorCode = errorCode;
    }
}
