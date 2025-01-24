package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;

import lombok.*;

/**
 * 运行日志信息
 *
 * @author xxx
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteLog {

    public static final String LOG_TAG_ERROR = "[ERROR]";
    public static final String LOG_TAG_INFO = "[INFO]";
    /**
     * 字段名字
     */
    private String tag;

    /**
     * 字段类型
     */
    private Long timestamp;

    /**
     * 字段注释
     */
    private String log;

    public static ExecuteLog newErrorLog(String message) {
        return new ExecuteLog(LOG_TAG_ERROR, System.currentTimeMillis(), message);
    }

    public static ExecuteLog newInfoLog(String message) {
        return new ExecuteLog(LOG_TAG_INFO, System.currentTimeMillis(), message);
    }
}
