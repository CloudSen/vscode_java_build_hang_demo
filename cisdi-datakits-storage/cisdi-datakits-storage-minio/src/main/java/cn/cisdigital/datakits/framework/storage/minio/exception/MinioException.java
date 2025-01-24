package cn.cisdigital.datakits.framework.storage.minio.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.exception.ExceptionCodeConstants;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Minio异常类
 *
 * @author xxx
 */
public class MinioException extends BusinessException {

    public MinioException(String msg) {
        super(msg);
    }

    public MinioException(Throwable e) {
        super(e);
    }

    public MinioException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MinioException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public MinioException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public MinioException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }

    /**
     * 对象存储错误码
     */
    @Getter
    @AllArgsConstructor
    public static enum MinioErrorCode implements ErrorCode {
        /**
         * minio请求异常
         */
        REQUEST_ERROR(ExceptionCodeConstants.MINIO_STARTER_MODULE_CODE + "001", "minio.exception.request_error"),
        /**
         * minio配置不存在
         */
        NULL_PROPERTIES(ExceptionCodeConstants.MINIO_STARTER_MODULE_CODE + "002", "minio.exception.null_properties"),
        /**
         * 不支持的操作
         */
        UNSUPPORTED_OPERATION(ExceptionCodeConstants.MINIO_STARTER_MODULE_CODE + "003", "minio.exception.unsupported_operation"),
        /**
         * 非法的minio全路径
         */
        ILLEGAL_FULL_PATH(ExceptionCodeConstants.MINIO_STARTER_MODULE_CODE + "004", "minio.exception.illegal_full_path"),
        ;
        private final String code;
        private final String key;
    }
}
