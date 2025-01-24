package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * sql 执行异常
 *
 * @author xxx
 */
public class SqlExecuteException extends BusinessException {

    public SqlExecuteException(String msg) {
        super(msg);
    }

    public SqlExecuteException(Throwable e) {
        super(e);
    }

    public SqlExecuteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SqlExecuteException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public SqlExecuteException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public SqlExecuteException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }
}
