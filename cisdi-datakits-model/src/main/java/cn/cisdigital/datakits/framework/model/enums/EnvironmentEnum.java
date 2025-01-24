package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 环境枚举
 *
 * @author xxx
 * @since 2024-03-22
 */
@Getter
@RequiredArgsConstructor
public enum EnvironmentEnum implements BaseEnum {

    /**
     * 生产环境
     */
    PROD(1, "model.env.prod"),
    /**
     * 开发环境
     */
    DEV(2, "model.env.dev"),

    ;

    private final int code;
    private final String key;
}
