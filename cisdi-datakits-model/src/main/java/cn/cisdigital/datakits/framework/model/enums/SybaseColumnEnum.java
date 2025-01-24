package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * @author xxx
 * @since 2024-10-24
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SybaseColumnEnum implements ColumnType {

    /**
     * 数值型 包含bit位类型
     */
    SYBASE_NUMERIC("NUMERIC", DataTypeEnum.NUMBER_TYPE),
    SYBASE_FLOAT("FLOAT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_DOUBLE("DOUBLE", DataTypeEnum.NUMBER_TYPE),
    SYBASE_DECIMAL("DECIMAL", DataTypeEnum.NUMBER_TYPE),
    SYBASE_SMALLINT("SMALLINT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_TINYINT("TINYINT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_REAL("REAL", DataTypeEnum.NUMBER_TYPE),
    SYBASE_BIGINT("BIGINT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_UNSIGNED_BIGINT("UNSIGNED BIGINT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_INT("INT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_UNSIGNED_INT("UNSIGNED INT", DataTypeEnum.NUMBER_TYPE),
    SYBASE_INTEGER("INTEGER", DataTypeEnum.NUMBER_TYPE),
    SYBASE_UNSIGNED_INTEGER("UNSIGNED INTEGER", DataTypeEnum.NUMBER_TYPE),
    SYBASE_BIT("BIT", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    SYBASE_CHAR("CHAR", DataTypeEnum.STRING_TYPE),
    SYBASE_CHARACTER("CHARACTER", DataTypeEnum.STRING_TYPE),
    SYBASE_VARCHAR_VARYING("CHARACTER VARYING", DataTypeEnum.STRING_TYPE),
    SYBASE_VARCHAR("VARCHAR", DataTypeEnum.STRING_TYPE),
    SYBASE_UNIQUEIDENTIFIERSTR("UNIQUEIDENTIFIERSTR", DataTypeEnum.STRING_TYPE),
    SYBASE_LONG_VARCHAR("LONG VARCHAR", DataTypeEnum.STRING_TYPE),
    SYBASE_TEXT("TEXT", DataTypeEnum.STRING_TYPE),

    /**
     * 时间型 包括：
     */
    SYBASE_DATE("DATE", DataTypeEnum.OTHER_TIME_TYPE),
    SYBASE_TIMESTAMP("TIMESTAMP", DataTypeEnum.DATETIME_TYPE),
    SYBASE_SMALLDATETIME("SMALLDATETIME", DataTypeEnum.DATETIME_TYPE),
    SYBASE_TIME("TIME", DataTypeEnum.OTHER_TIME_TYPE),
    SYBASE_DATETIME("DATETIME", DataTypeEnum.DATETIME_TYPE),

    /**
     * 其他 包括：
     */
    SYBASE_BINARY("BINARY", DataTypeEnum.BINARY_BIG_TYPE),
    SYBASE_VARBINARY("VARBINARY", DataTypeEnum.BINARY_BIG_TYPE),
    SYBASE_UNIQUEIDENTIFIER("UNIQUEIDENTIFIER", DataTypeEnum.BINARY_BIG_TYPE),
    SYBASE_DOMAIN("DOMAIN", DataTypeEnum.OTHER_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    SYBASE_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),
    ;


    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static SybaseColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(SybaseColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst()
                .orElse(Arrays.stream(SybaseColumnEnum.values())
                        .filter(type -> typeString.startsWith(type.getType().toLowerCase()) || typeString.startsWith(type.getType()))
                        .findFirst().orElse(SYBASE_UNKNOWN_DEFINE));
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.SYBASE_IQ;
    }

    @Override
    public SybaseColumnEnum parseColumnType(String typeString) {
        return SybaseColumnEnum.parse(typeString);
    }
}
