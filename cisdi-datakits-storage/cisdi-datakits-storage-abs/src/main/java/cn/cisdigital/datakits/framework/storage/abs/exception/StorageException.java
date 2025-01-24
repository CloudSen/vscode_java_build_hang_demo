package cn.cisdigital.datakits.framework.storage.abs.exception;


import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.exception.ExceptionCodeConstants;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象存储异常
 *
 * @auther clouds3n
 * @since 2025-01-03
 */
public class StorageException extends BusinessException {

    public StorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public StorageException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public StorageException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }

    /**
     * 对象存储错误码
     */
    @Getter
    @AllArgsConstructor
    public static enum StorageErrorCode implements ErrorCode {
        /**
         * 多个资源同时处理时，必须都为相同的shceme协议
         */
        MULTI_SCHEME_ERROR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "001", "storage.exception.multi_scheme_error"),
        /**
         * 缺失scheme协议
         */
        MISSING_SCHEME(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "002", "storage.exception.missing_scheme"),
        /**
         * 无法从目录读取内容
         */
        CAN_NOT_READ_CONTENT_FROM_DIR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "003", "storage.exception.can_not_read_content_from_dir"),
        /**
         * 当前文件类型不支持预览和在线编辑
         */
        UNSUPPORTED_VIEW_EDIT(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "003", "storage.exception.unsupported_view"),
        /**
         * 获取输入流异常
         */
        GET_INPUTSTREAM_ERROR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "004", "storage.exception.get_inputstream_error"),
        /**
         * 资源已存在
         */
        RESOURCE_EXITED(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "005", "storage.exception.resource_exited"),
        /**
         * 资源上传失败
         */
        RESOURCE_UPLOAD_ERROR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "006", "storage.exception.resource_upload_error"),
        /**
         * 资源不存在
         */
        RESOURCE_NOT_EXITED(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "007", "storage.exception.resource_not_exited"),
        /**
         * 资源删除失败
         */
        RESOURCE_DELETE_ERROR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "008", "storage.exception.resource_delete_error"),
        /**
         * 获取资源失败
         */
        GET_RESOURCE_ERROR(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "009", "storage.exception.get_resource_error"),
        /**
         * 暂不支持的资源类型
         */
        RESOURCE_TYPE_NOT_SUPPORT(ExceptionCodeConstants.STORAGE_STARTER_MODULE_CODE + "010", "storage.exception.resource_type_not_support"),
        ;

        /**
         * 错误码
         */
        private final String code;

        /**
         * 异常信息
         */
        private final String key;
    }
}
