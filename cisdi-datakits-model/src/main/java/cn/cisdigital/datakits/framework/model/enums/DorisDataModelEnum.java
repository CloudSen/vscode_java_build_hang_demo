package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author xxx
 * @since 2024-03-13-15:44
 */
@Getter
@RequiredArgsConstructor
public enum DorisDataModelEnum implements BaseEnum {

    /**
     * 聚合模型
     */
    AGGREGATE(1, "AGGREGATE KEY", "AGG"),

    /**
     * Unique模型能够保证Key的唯一性
     */
    UNIQUE(2, "UNIQUE KEY", "UNI"),

    /**
     * 数据既没有主键，也没有聚合需求，采用Duplicate数据模型
     */
    DUPLICATE(3, "DUPLICATE KEY", "DUP"),
    ;


    private final int code;
    /**
     * doris数据模型的语法编码
     */
    private final String dorisCode;
    /**
     * 在columns表中，column_type聚合列的类型标识
     */
    private final String dorisColumnKeyCode;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

    public static DorisDataModelEnum parseByColumnKeyCode(String dorisColumnKeyCode) {
        return Arrays.stream(DorisDataModelEnum.values())
                .filter(model -> model.getDorisColumnKeyCode().equals(dorisColumnKeyCode))
                .findFirst()
                .orElse(DUPLICATE);
    }

}
