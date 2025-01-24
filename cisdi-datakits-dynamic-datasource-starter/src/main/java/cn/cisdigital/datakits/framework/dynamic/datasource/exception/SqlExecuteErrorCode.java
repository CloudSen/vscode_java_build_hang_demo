package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants;
import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * sql执行错误码枚举
 *
 * @author xxx
 * @since 2024-05-29
 */
@Getter
@RequiredArgsConstructor
public enum SqlExecuteErrorCode implements ErrorCode {

    EXECUTE_DDL_FAIL(Constants.DATASOURCE_ERROR_CODE_PREFIX + "001", "sql-execute.exception.ddl_data_access"),
    EXECUTE_DML_FAIL(Constants.DATASOURCE_ERROR_CODE_PREFIX + "002", "sql-execute.exception.dml_data_access"),
    EXECUTE_QUERY_FAIL(Constants.DATASOURCE_ERROR_CODE_PREFIX + "003", "sql-execute.exception.query_data_access"),
    EXECUTE_FAIL(Constants.DATASOURCE_ERROR_CODE_PREFIX + "004", "sql-execute.exception"),


    ;

    private final String code;
    private final String key;

    public static SqlExecuteErrorCode ofNonNull(String code) {
        return Arrays.stream(values()).filter(v -> CharSequenceUtil.equals(code, v.getCode()))
                .findAny().orElseThrow(
                        () -> new IllegalArgumentException("枚举值不存在: " + code));
    }

    public static SqlExecuteErrorCode of(String code) {
        return Arrays.stream(values()).filter(v -> CharSequenceUtil.equals(code, v.getCode()))
                .findAny().orElse(null);
    }
}
