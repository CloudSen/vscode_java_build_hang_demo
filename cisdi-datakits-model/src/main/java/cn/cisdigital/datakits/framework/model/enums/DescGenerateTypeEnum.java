package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 字段描述生成策略
 *
 * @author xxx
 * @since 2024-07-22
 */
@Getter
@RequiredArgsConstructor
public enum DescGenerateTypeEnum implements BaseEnum {

    /**
     * 使用字段中文名
     */
    WITH_NAME(1, "column.desc.generate.type.with_name"),
    /**
     * 使用字段描述
     */
    WITH_DESC(2, "column.desc.generate.type.with_desc"),

    /**
     * 描述为空时，使用字段中文名
     */
    WITH_NAME_WHEN_DESC_ISNULL(3, "quality.task.rule.type.with_name_when_desc_isnull"),

    ;

    private final int code;
    private final String key;

    public static DescGenerateTypeEnum of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code)
                .findAny().orElse(null);
    }
}
