package cn.cisdigital.datakits.framework.crypto.exception;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.exception.ExceptionCodeConstants;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加解密异常
 *
 * @author xxx
 */
public class CryptoException extends BusinessException {

    public CryptoException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CryptoException(ErrorCode errorCode, Object... msgFormats) {
        super(errorCode, msgFormats);
    }

    public CryptoException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    public CryptoException(ErrorCode errorCode, Throwable e, Object... msgFormats) {
        super(errorCode, e, msgFormats);
    }

    /**
     * 加解密错误码
     */
    @Getter
    @AllArgsConstructor
    public enum CryptoErrorCode implements ErrorCode {
        /**
         * 加解密初始化异常
         */
        INIT_EXCEPTION(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "001",
                "crypto.exception.init_exception"),

        /**
         * 暂不支持的算法类型
         */
        UNSUPPORTED_ALGORITHM(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "002",
                "crypto.exception.unsupported_operation"),

        /**
         * 算法配置缺少参数
         */
        CRYPTO_CONFIG_MISSING_PARAM(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "003",
                "crypto.exception.config_missing_parameter"),

        /**
         * 算法不存在
         */
        ALGORITHM_NOT_EXISTS(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "004",
                "crypto.exception.algorithm_not_exists"),

        /**
         * 加解密配置校验器初始化异常
         */
        CRYPTO_CONFIG_VALIDATOR_INIT_EXCEPTION(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "005",
                "crypto.exception.validator_init_exception"),

        /**
         * 加解密配置校验器不存在
         */
        CRYPTO_CONFIG_VALIDATOR_NOT_EXISTS(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "006",
                "crypto.exception.validator_not_exists"),

        /**
         * 盐值长度不合法，长度需要在1～50之间
         */
        INVALID_SALT_LENGTH(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "007",
                "crypto.exception.invalid_salt_length"),

        /**
         * Hmac必须有key值
         */
        HMAC_MISSING_KEY(ExceptionCodeConstants.CRYPTO_STARTER_MODULE_CODE + "008",
                "crypto.exception.hmac_missing_key"),

        ;

        private final String code;
        private final String key;
    }
}
