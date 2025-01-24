package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import lombok.Data;

/**
 * @author xxx
 * @since 2022-11-08-10:07
 */
@Data
public class TypeConvertException extends RuntimeException {

    private String errorType;

    public TypeConvertException() {
    }

    public TypeConvertException(String message) {
        super(message);
    }

    public TypeConvertException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public TypeConvertException(String message, String errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public TypeConvertException(Throwable cause) {
        super(cause);
    }
}
