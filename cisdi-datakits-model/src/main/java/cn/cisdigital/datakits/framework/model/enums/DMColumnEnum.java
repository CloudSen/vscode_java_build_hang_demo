package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-06-21-15:15
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum DMColumnEnum implements ColumnType {
    /**
     * 数值型
     */
    DM_NUMERIC("NUMERIC", DataTypeEnum.NUMBER_TYPE),
    DM_DECIMAL("DECIMAL", DataTypeEnum.NUMBER_TYPE),
    DM_DEC("DEC", DataTypeEnum.NUMBER_TYPE),
    DM_NUMBER("NUMBER", DataTypeEnum.NUMBER_TYPE),
    DM_INTEGER("INTEGER", DataTypeEnum.NUMBER_TYPE),
    DM_INT("INT", DataTypeEnum.NUMBER_TYPE),
    DM_BIGINT("BIGINT", DataTypeEnum.NUMBER_TYPE),
    DM_TINYINT("TINYINT", DataTypeEnum.NUMBER_TYPE),
    DM_BYTE("BYTE", DataTypeEnum.NUMBER_TYPE),
    DM_SMALLINT("SMALLINT", DataTypeEnum.NUMBER_TYPE),
    DM_FLOAT("FLOAT", DataTypeEnum.NUMBER_TYPE),
    DM_DOUBLE("DOUBLE", DataTypeEnum.NUMBER_TYPE),
    DM_REAL("REAL", DataTypeEnum.NUMBER_TYPE),
    DM_DOUBLE_PRECISION("DOUBLE PRECISION", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    DM_CHAR("CHAR",DataTypeEnum.STRING_TYPE),
    DM_CHARACTER("CHARACTER",DataTypeEnum.STRING_TYPE),
    DM_VARCHAR("VARCHAR", DataTypeEnum.STRING_TYPE),
    DM_VARCHAR2("VARCHAR2", DataTypeEnum.STRING_TYPE),
    /**
     * bool
     */
    DM_BIT("BIT", DataTypeEnum.BOOL_TYPE),
    /**
     * 时间
     */
    DM_DATETIME("DATETIME",DataTypeEnum.DATETIME_TYPE),
    DM_TIMESTAMP("TIMESTAMP", DataTypeEnum.DATETIME_TYPE),
    /**
     * 特殊时间 以及非增量
     */
    DM_DATE("DATE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_TIME("TIME", DataTypeEnum.OTHER_TIME_TYPE),
    DM_TIME_WITH_TIME_ZONE("TIME WITH TIME ZONE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_DATETIME_WITH_TIME_ZONE("DATETIME WITH TIME ZONE", DataTypeEnum.OTHER_TIME_TYPE),


    DM_INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_YEAR("INTERVAL YEAR", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_DAY("INTERVAL DAY", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_DAY_TO_HOUR("INTERVAL DAY TO HOUR", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_DAY_TO_MINUTE("INTERVAL DAY TO MINUTE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_MINUTE("INTERVAL MINUTE", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_MINUTE_TO_SECOND("INTERVAL MINUTE TO SECOND", DataTypeEnum.OTHER_TIME_TYPE),
    DM_INTERVAL_SECOND("INTERVAL SECOND", DataTypeEnum.OTHER_TIME_TYPE),
    /**
     * 大文本以及二进制
     */
    DM_BINARY("BINARY", DataTypeEnum.BINARY_BIG_TYPE),
    DM_VARBINARY("VARBINARY", DataTypeEnum.BINARY_BIG_TYPE),
    DM_TEXT("TEXT", DataTypeEnum.BINARY_BIG_TYPE),
    DM_LONG("LONG", DataTypeEnum.BINARY_BIG_TYPE),
    DM_LONGVARCHAR("LONGVARCHAR", DataTypeEnum.BINARY_BIG_TYPE),
    DM_IMAGE("IMAGE", DataTypeEnum.BINARY_BIG_TYPE),
    DM_LONGVARBINARY("LONGVARBINARY", DataTypeEnum.BINARY_BIG_TYPE),
    DM_BLOB("BLOB", DataTypeEnum.BINARY_BIG_TYPE),
    DM_CLOB("CLOB", DataTypeEnum.BINARY_BIG_TYPE),
    DM_BFILE("BFILE", DataTypeEnum.BINARY_BIG_TYPE),
    DM_ROWID("ROWID", DataTypeEnum.BINARY_BIG_TYPE),

    /**
     * 用户自定义以及其他未知类型
     */
    DM_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),
    ;

    private String type;
    private DataTypeEnum dataType;


    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static DMColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(DMColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(DM_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.DM;
    }

    @Override
    public ColumnType parseColumnType(String typeString) {
        return DMColumnEnum.parse(typeString);
    }
}
