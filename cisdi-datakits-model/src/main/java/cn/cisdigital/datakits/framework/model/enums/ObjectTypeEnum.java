package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author xxx
 * @Description 授权对象类型
 * @since 2024-04-19
 */
@Getter
@AllArgsConstructor
public enum ObjectTypeEnum implements BaseEnum {

    /**
     * 个人用户
     */
    PERSONAL(1, "个人"),

    /**
     * 项目用户
     */
    PROJECT(2, "项目");

    private final int code;
    private final String desc;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getKey() {
        return this.name();
    }
    public static ObjectTypeEnum of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
            .findAny().orElse(null);
    }
}
