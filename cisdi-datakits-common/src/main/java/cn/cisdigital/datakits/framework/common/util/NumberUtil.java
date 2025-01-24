package cn.cisdigital.datakits.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * @author xxx
 */
@Slf4j
public class NumberUtil extends cn.hutool.core.util.NumberUtil {

    public static <R> R parseWithoutException(Function<String, R> parseFunction, String number, R defaultValue) {
        try {
            return parseFunction.apply(number);
        } catch (Exception e) {
            log.error("数字解析失败", e);
        }
        return defaultValue;
    }
}
