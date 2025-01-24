package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xxx
 * @since 2024-05-11
 */
@Getter
@RequiredArgsConstructor
public enum DorisStreamLoadErrorCode implements ErrorCode {

    WRITE_DATA_FAIL("-1", "doris写入数据失败, result: {0}, detail: {1}"),
    ;

    private final String code;
    private final String key;

    public static DorisStreamLoadErrorCode ofNonNull(String code){
        return Arrays.stream(values()).filter(v -> CharSequenceUtil.equals(code, v.getCode()))
            .findAny().orElseThrow(
                () -> new IllegalArgumentException("枚举值不存在: " + code));
    }

    public static DorisStreamLoadErrorCode of(String code){
        return Arrays.stream(values()).filter(v -> CharSequenceUtil.equals(code, v.getCode()))
            .findAny().orElse(null);
    }
}
