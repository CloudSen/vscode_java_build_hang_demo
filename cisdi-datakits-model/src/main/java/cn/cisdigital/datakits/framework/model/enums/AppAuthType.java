package cn.cisdigital.datakits.framework.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 应用认证方式
 *
 * @author xxx
 * @since 2025-01-02
 */
@Getter
@RequiredArgsConstructor
public enum AppAuthType {

    SIMPLE(1, "简单认证"),
    CRYPTO(2, "加密认证"),
    SIGN(3, "签名认证"),

    ;
    @EnumValue
    @JsonValue
    private final int code;

    private final String key;

    public static AppAuthType of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
                .findAny().orElse(null);
    }

}
