package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author xxx
 * @Description 域类型
 * @since 2024-05-08
 */
@Getter
@RequiredArgsConstructor
public enum DomainTypeEnum implements BaseEnum {

    TOPIC_DOMAIN(0, "主题域"),
    APPLICATION_DOMAIN(1, "应用域");

    private final int code;
    private final String key;

    public static DomainTypeEnum of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
            .findAny().orElse(null);
    }
}
