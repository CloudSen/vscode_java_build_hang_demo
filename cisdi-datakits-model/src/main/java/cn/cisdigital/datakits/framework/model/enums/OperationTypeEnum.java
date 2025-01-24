package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author xxx
 * @Description 授权操作类型
 * @since 2024-04-19
 */
@Getter
@RequiredArgsConstructor
public enum OperationTypeEnum implements BaseEnum {

    READ(1, "读,二进制10000", 16),
    UPDATE(2, "写,二进制1000", 8),
    ALTER(3, "改表,二进制100", 4),
    CREATE(4, "建表,二进制10", 2),
    DROP(5, "删表,二进制1", 1),
    OTHERS(6, "其他,二进制0", 0);

    private final int code;
    private final String desc;
    private final int value;

    public static OperationTypeEnum of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
            .findAny().orElse(null);
    }

    @Override
    public String getKey() {
        return "";
    }
}
