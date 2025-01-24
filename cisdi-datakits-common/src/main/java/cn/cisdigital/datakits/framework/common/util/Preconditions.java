package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * 用于验证异常条件触发的静态方法的集合
 *
 * @author xxx
 * @since 2024/4/17 14:18
 */
public class Preconditions {

    private Preconditions() {
    }


    /**
     * 检查给定的布尔条件，如果不满足条件则抛出业务异常{@link BusinessException}
     *
     * @param condition  条件
     * @param errorCode  errorCode
     * @param msgFormats msg
     */
    public static void checkArgument(boolean condition, ErrorCode errorCode, Object... msgFormats) {
        if (!condition) {
            throw new BusinessException(errorCode, msgFormats);
        }
    }

    /**
     * 检查给定的布尔条件，如果不满足条件则抛出业务异常{@link BusinessException}
     *
     * @param condition  条件
     * @param errorCode  errorCode
     */
    public static void checkArgument(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 检查给定的布尔条件，如果不满足条件则抛出业务异常{@link RuntimeException}
     *
     * @param condition    条件
     * @param errorMessage msg
     */
    public static void checkArgument(boolean condition, String errorMessage) {
        if (!condition) {
            throw new RuntimeException(errorMessage);
        }
    }

    public static void checkArgument(boolean condition, @Nullable Object errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static <T> T checkNotNull(@CheckForNull T reference, @CheckForNull Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }
}
