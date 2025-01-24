package cn.cisdigital.datakits.framework.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AlertStatusEnum {
    /**
     * 1 恢复
     */
    RECOVER(1, "alert.enum.recover-state"),

    /**
     * 2 告警
     */
    ALERT(2, "alert.enum.alert-state"),

    ;

    private final int code;
    private final String key;

    public static AlertStatusEnum parseCodeNonNull(Integer code) {
        return Arrays.stream(AlertStatusEnum.values())
        .filter(e -> e.code == code)
        .findFirst().orElseThrow(() -> new IllegalArgumentException());
    }

    public static AlertStatusEnum parseCode(Integer code) {
        return Arrays.stream(AlertStatusEnum.values())
        .filter(e -> e.code == code)
        .findFirst().orElse(null);
    }
}
