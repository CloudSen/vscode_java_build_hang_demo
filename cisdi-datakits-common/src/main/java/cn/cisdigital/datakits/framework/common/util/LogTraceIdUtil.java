package cn.cisdigital.datakits.framework.common.util;

import java.util.Optional;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 日志追踪id工具类
 *
 * @author xxx
 */
public class LogTraceIdUtil {

    public static final String LOG_TRACE_ID_KEY = "request-id";

    /**
     * 将追踪id写入日志MDC，使用完成后，同线程内一定要调用 {@link LogTraceIdUtil#clearTraceIdIfMatch(String)} 清理变量
     */
    public static String setTraceIdIfNotExist() {
        String traceId = getTraceId();
        if (!StringUtils.hasText(traceId)) {
            traceId = setTraceId(generateTraceId());
        }
        return traceId;
    }

    /**
     * 将追踪id写入日志MDC
     */
    public static void setTraceIdIfNotExist(String traceId) {
        if (!StringUtils.hasText(getTraceId())) {
            setTraceId(traceId);
        }
    }

    /**
     * 从日志MDC中获取追踪id
     */
    public static String getTraceId() {
        return MDC.get(LOG_TRACE_ID_KEY);
    }

    /**
     * 清除MDC中的匹配的追踪id
     */
    public static void clearTraceIdIfMatch(String traceId) {
        if (Optional.ofNullable(traceId).orElse("").equals(getTraceId())) {
            MDC.remove(LOG_TRACE_ID_KEY);
        }
    }

    /**
     * 设置traceId
     */
    public static String setTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            MDC.put(LOG_TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    /**
     * 生成一个traceId
     */
    public static String generateTraceId() {
        return IdentifierGenerator.generateRequestCode();
    }
}
