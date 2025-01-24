package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-02-14-14:13
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SqlServerColumnEnum implements ColumnType {

    /**
     * 数值型 包括别名
     */
    SQLSERVER_BIT("bit", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_TINYINT("tinyint", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_INT("int", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_DECIMAL("decimal", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_NUMERIC("numeric", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_FLOAT("float", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_REAL("real", DataTypeEnum.NUMBER_TYPE),

    SQLSERVER_SMALLMONEY("smallmoney", DataTypeEnum.NUMBER_TYPE),
    SQLSERVER_MONEY("money", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型 包括别名
     */
    SQLSERVER_CHAR("char", DataTypeEnum.STRING_TYPE),
    SQLSERVER_VARCHAR("varchar", DataTypeEnum.STRING_TYPE),
    SQLSERVER_NCHAR("nchar", DataTypeEnum.STRING_TYPE),
    SQLSERVER_NVARCHAR("nvarchar", DataTypeEnum.STRING_TYPE),
    SQLSERVER_TEXT("text", DataTypeEnum.STRING_TYPE),
    SQLSERVER_NTEXT("ntext", DataTypeEnum.STRING_TYPE),
    //类似uuid
    SQLSERVER_UNIQUEIDENTIFIER("uniqueidentifier", DataTypeEnum.STRING_TYPE),

    //binary
    SQLSERVER_BINARY("binary", DataTypeEnum.BINARY_BIG_TYPE),
    SQLSERVER_VARBINARY("varbinary", DataTypeEnum.BINARY_BIG_TYPE),
    SQLSERVER_IMAGE("image", DataTypeEnum.BINARY_BIG_TYPE),
    /**
     * 时间型
     */
    SQLSERVER_DATETIME2("datetime2", DataTypeEnum.DATETIME_TYPE),
    SQLSERVER_DATETIME("datetime", DataTypeEnum.DATETIME_TYPE),
    SQLSERVER_SMALLDATETIME("smalldatetime", DataTypeEnum.DATETIME_TYPE),

    SQLSERVER_DATE("date", DataTypeEnum.OTHER_TIME_TYPE),
    SQLSERVER_DATETIMEOFFSET("datetimeoffset", DataTypeEnum.OTHER_TIME_TYPE),
    SQLSERVER_TIME("time", DataTypeEnum.OTHER_TIME_TYPE),


    /**
     * 其他
     */
    //dbeaver是二进制。
    SQLSERVER_TIMESTAMP("timestamp", DataTypeEnum.OTHER_TYPE),

    SQLSERVER_CURSOR("cursor", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_ROWVERSION("rowversion", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_HIERARCHYID("hierarchyid", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_SQL_VARIANT("sql_variant", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_XML("xml", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_TABLE("table", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_GEOMETRY("geometry", DataTypeEnum.OTHER_TYPE),
    SQLSERVER_GEOGRAPHY("geography", DataTypeEnum.OTHER_TYPE),

    /**
     * 用户自定义以及其他未知类型
     */
    SQLSERVER_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;
    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static SqlServerColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(SqlServerColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(SQLSERVER_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.MS_SQL_SERVER;
    }

    @Override
    public SqlServerColumnEnum parseColumnType(String typeString) {
        return SqlServerColumnEnum.parse(typeString);
    }
}
