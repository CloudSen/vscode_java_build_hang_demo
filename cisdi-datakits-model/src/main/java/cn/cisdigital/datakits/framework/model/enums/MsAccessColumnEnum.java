package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * @author xxx
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum MsAccessColumnEnum implements ColumnType {

    /**
     * 数值型
     */
    MS_ACCESS_INTEGER("INTEGER", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_DECIMAL("DECIMAL", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_SMALLINT("SMALLINT", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_NUMERIC("NUMERIC", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_INT("INT", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_BIGINT("BIGINT", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_DOUBLE("DOUBLE", DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_DOUBLE_PRECISION("DOUBLE PRECISION",DataTypeEnum.NUMBER_TYPE),
    MS_ACCESS_FLOAT("FLOAT", DataTypeEnum.NUMBER_TYPE),
    /**
     * 字符型
     */
    MS_ACCESS_CHARACTER_VARYING("CHARACTER VARYING", DataTypeEnum.STRING_TYPE),
    MS_ACCESS_VARCHAR("VARCHAR", DataTypeEnum.STRING_TYPE),
    MS_ACCESS_CHAR("CHAR", DataTypeEnum.STRING_TYPE),
    MS_ACCESS_LONGVARCHAR("LONGVARCHAR", DataTypeEnum.STRING_TYPE),
    /**
     * 时间型
     */
    MS_ACCESS_TIMESTAMP("TIMESTAMP", DataTypeEnum.DATETIME_TYPE),
    /**
     * 布尔型
     */
    MS_ACCESS_BOOLEAN("BOOLEAN", DataTypeEnum.BOOL_TYPE),
    /**
     * 其他类型
     */
    MS_ACCESS_BLOB("BLOB", DataTypeEnum.OTHER_TYPE),
    MS_ACCESS_OBJECT("OBJECT", DataTypeEnum.OTHER_TYPE),
    MS_ACCESS_OTHER("OTHER", DataTypeEnum.OTHER_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    MS_ACCESS_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),
    ;

    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static MsAccessColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        if(typeString.startsWith("CHAR")){
            return MS_ACCESS_CHAR;
        }
        if(typeString.startsWith("DECIMAL")){
            return MS_ACCESS_DECIMAL;
        }
        //兼容DOUBLE PRECISION
        if(typeString.startsWith("DOUBLE")){
            return MS_ACCESS_DOUBLE;
        }
        return Arrays.stream(MsAccessColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(MS_ACCESS_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.MS_ACCESS;
    }

    @Override
    public MsAccessColumnEnum parseColumnType(String typeString) {
        return MsAccessColumnEnum.parse(typeString);
    }

}
