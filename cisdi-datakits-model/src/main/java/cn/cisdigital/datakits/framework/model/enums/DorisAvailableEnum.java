package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DorisAvailableEnum implements BaseEnum {

    /**
     * 1 Doris可用
     */
    AVAILABLE(1, "doris.enum.is_available"),

    /**
     * 0 Doris不可用
     */
    UNAVAILABLE(0, "doris.enum.unavailable"),

    ;

    private final int code;
    private final String key;

    public static DorisAvailableEnum parseCodeNonNull(Integer code) {
        return Arrays.stream(DorisAvailableEnum.values())
        .filter(e -> e.code == code)
        .findFirst().orElseThrow(() -> new IllegalArgumentException());
    }

    public static DorisAvailableEnum parseCode(Integer code) {
        return Arrays.stream(DorisAvailableEnum.values())
        .filter(e -> e.code == code)
        .findFirst().orElse(null);
    }

}
