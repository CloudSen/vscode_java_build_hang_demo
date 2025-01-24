package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 模型物化类型枚举
 *
 * @author xxx
 * @since 2024-05-14
 */
@Getter
@RequiredArgsConstructor
public enum MaterializeTypeEnum implements BaseEnum {

    /**
     * 表
     */
    TABLE(0, "table", "BASE TABLE"),
    /**
     * 逻辑视图
     */
    VIEW(1, "view", "VIEW"),

    ;

    private final int code;
    private final String key;
    /**
     * doris数据类型在 information_schema 中的类型编码
     */
    private final String dorisCode;

    public static MaterializeTypeEnum parse(String dorisCode) {
        return Arrays.stream(MaterializeTypeEnum.values())
                .filter(type -> type.getDorisCode().equals(dorisCode))
                .findFirst()
                .orElse(TABLE);
    }
}
