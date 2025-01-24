package cn.cisdigital.datakits.framework.cache.abs;


import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;

/**
 * 缓存异常
 *
 * @author xxx
 * @since 2022-09-23
 */
public class CacheException extends BusinessException {
    public CacheException(String msg) {
        super(msg);
    }

    public CacheException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CacheException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }
}
