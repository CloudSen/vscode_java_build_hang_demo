package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 索引枚举
 *
 * @author xxx
 * @since 2024/9/2 16:07
 */
@Getter
@AllArgsConstructor
public enum DorisIndexTypeEnum implements IndexType {
    /**
     * Doris倒排索引
     */
    DORIS_INVERTED(1, "INVERTED"),
    /**
     * 未知索引类型
     */
    UNKNOWN(-1, "UNKNOWN");


    private final int code;
    private final String type;

    @JsonCreator
    public static DorisIndexTypeEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(DorisIndexTypeEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString))
                .findFirst().orElse(UNKNOWN);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.DORIS;
    }

    @Override
    public String getIndexTypeName() {
        return type;
    }
}
