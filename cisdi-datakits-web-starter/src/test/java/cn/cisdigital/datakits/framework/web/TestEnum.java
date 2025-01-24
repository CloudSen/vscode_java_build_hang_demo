package cn.cisdigital.datakits.framework.web;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xxx
 * @since 2024-03-08
 */
@Getter
@RequiredArgsConstructor
public enum TestEnum implements BaseEnum {

    MAN(1, "sadfafsd"),
    WOMAN(2, "asdfsadfd"),
    ;
    private final int code;
    private final String key;
}
