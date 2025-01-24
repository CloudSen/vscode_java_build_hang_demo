package cn.cisdigital.datakits.framework.cache.abs;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static cn.cisdigital.datakits.framework.cache.abs.CacheConstants.CACHE_ERROR_CODE_PREFIX;

/**
 * API异常类
 *
 * @author xxx
 */
@Getter
@RequiredArgsConstructor
public enum CacheErrorCode implements ErrorCode {

    /**
     * key不允许为空
     */
    KEY_IS_EMPTY(CACHE_ERROR_CODE_PREFIX + "001", "cache.exception.key_is_empty"),
    /**
     * 不存在的ValueLoader
     */
    NO_VALUE_LOADER(CACHE_ERROR_CODE_PREFIX + "002", "cache.exception.no_value_loader"),
    /**
     * 消息监听器类型转换错误
     */
    MESSAGE_LISTENER_CAST_ERROR(CACHE_ERROR_CODE_PREFIX + "003", "cache.exception.message_listener_cast_error"),
    /**
     * 消息监听器类型转换错误
     */
    SUBSCRIBE_ERROR(CACHE_ERROR_CODE_PREFIX + "004", "cache.exception.subscribe_error"),
    /**
     * semaphore凭证用尽
     */
    NO_SEMAPHORE_PERMITS_AVAILABLE(CACHE_ERROR_CODE_PREFIX + "005", "cache.exception.no_semaphore_permits_available"),
    /**
     * semaphore凭证ID错误或已被释放
     */
    SEMAPHORE_PERMITS_ID_ILLEGAL(CACHE_ERROR_CODE_PREFIX + "006", "cache.exception.semaphore_permits_id_illegal"),
    ;

    private final String code;

    private final String key;

}
