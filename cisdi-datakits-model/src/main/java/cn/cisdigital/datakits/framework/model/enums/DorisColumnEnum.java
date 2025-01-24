package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * @author xxx
 * @since 2022-11-04-9:38
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum DorisColumnEnum implements ColumnType {
    /**
     * 数值型
     */
    DORIS_TINYINT("tinyint", DataTypeEnum.NUMBER_TYPE),
    DORIS_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    DORIS_INT("int", DataTypeEnum.NUMBER_TYPE),
    DORIS_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    DORIS_LARGEINT("largeint", DataTypeEnum.NUMBER_TYPE),
    DORIS_FLOAT("float", DataTypeEnum.NUMBER_TYPE),
    DORIS_DOUBLE("double", DataTypeEnum.NUMBER_TYPE),
    DORIS_DECIMAL("decimal", DataTypeEnum.NUMBER_TYPE),
    /**
     * 字符型
     */
    DORIS_CHAR("char", DataTypeEnum.STRING_TYPE),
    DORIS_VARCHAR("varchar", DataTypeEnum.STRING_TYPE),
    DORIS_STRING("string", DataTypeEnum.STRING_TYPE),
    DORIS_JSONB("jsonb", DataTypeEnum.STRING_TYPE),
    // 在1.2.x版本中，JSON类型的名字是JSONB，为了尽量跟MySQL兼容，从2.0.0版本开始改名为JSON，老的表仍然可以使用
    DORIS_JSON("json", DataTypeEnum.STRING_TYPE),
    /**
     * 时间型
     */
    DORIS_DATE("date", DataTypeEnum.OTHER_TIME_TYPE),
    DORIS_DATETIME("datetime", DataTypeEnum.DATETIME_TYPE),

    /**
     * 布尔
     */
    DORIS_BOOLEAN("boolean", DataTypeEnum.BOOL_TYPE),
    /**
     * 其他
     */
    DORIS_HLL("hll", DataTypeEnum.OTHER_TYPE),
    DORIS_BITMAP("bitmap", DataTypeEnum.OTHER_TYPE),
    DORIS_ARRAY("array", DataTypeEnum.OTHER_TYPE),
    DORIS_MAP("map", DataTypeEnum.OTHER_TYPE),
    DORIS_STRUCT("struct", DataTypeEnum.OTHER_TYPE),
    DORIS_AGG_STATE("agg_state", DataTypeEnum.OTHER_TYPE),
    ;
    private String type;
    private DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static DorisColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        // 特殊处理
        if(typeString.toLowerCase().startsWith("datetime")){
            return DorisColumnEnum.DORIS_DATETIME;
        }
        // 特殊处理
        if(typeString.toLowerCase().startsWith("decimal")){
            return DorisColumnEnum.DORIS_DECIMAL;
        }
        // 特殊处理
        if ("text".equalsIgnoreCase(typeString)) {
            return DorisColumnEnum.DORIS_STRING;
        }
        return Arrays.stream(DorisColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(null);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.DORIS;
    }

    @Override
    public DorisColumnEnum parseColumnType(String typeString) {
        return DorisColumnEnum.parse(typeString);
    }
}
