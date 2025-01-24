package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-11-04-9:20
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum MysqlColumnEnum implements ColumnType {

    /**
     * 数值型 包含bit位类型
     */
    MYSQL_BIT_1("bit(1)", DataTypeEnum.NUMBER_TYPE),
    MYSQL_BIT("bit",DataTypeEnum.BINARY_BIG_TYPE),

    MYSQL_TINYINT("tinyint", DataTypeEnum.NUMBER_TYPE),
    MYSQL_TINYINT_UNSIGNED("tinyint unsigned", DataTypeEnum.NUMBER_TYPE),
    MYSQL_INT("int", DataTypeEnum.NUMBER_TYPE),
    MYSQL_INT_UNSIGNED("int unsigned", DataTypeEnum.NUMBER_TYPE),
    MYSQL_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    MYSQL_SMALLINT_UNSIGNED("smallint unsigned", DataTypeEnum.NUMBER_TYPE),
    MYSQL_MEDIUMINT("mediumint", DataTypeEnum.NUMBER_TYPE),
    MYSQL_MEDIUMINT_UNSIGNED("mediumint unsigned", DataTypeEnum.NUMBER_TYPE),
    MYSQL_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    // 在mysql的schema表中，dataType字段并没有 bigint unsigned，但是doris有，此处是兼容doris
    MYSQL_BIGINT_UNSIGNED("bigint unsigned", DataTypeEnum.NUMBER_TYPE),
    //浮点
    MYSQL_FLOAT("float", DataTypeEnum.NUMBER_TYPE),
    MYSQL_FLOAT_UNSIGNED("float unsigned", DataTypeEnum.NUMBER_TYPE),
    MYSQL_DOUBLE("double", DataTypeEnum.NUMBER_TYPE),
    MYSQL_DOUBLE_UNSIGNED("double unsigned", DataTypeEnum.NUMBER_TYPE),
    //定点
    MYSQL_DECIMAL("decimal", DataTypeEnum.NUMBER_TYPE),
    MYSQL_DECIMAL_UNSIGNED("decimal unsigned", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    MYSQL_CHAR("char", DataTypeEnum.STRING_TYPE),
    MYSQL_VARCHAR("varchar", DataTypeEnum.STRING_TYPE),
    MYSQL_TINYTEXT("tinytext", DataTypeEnum.STRING_TYPE),
    MYSQL_TEXT("text", DataTypeEnum.STRING_TYPE),
    MYSQL_MEDIUMTEXT("mediumtext", DataTypeEnum.STRING_TYPE),
    MYSQL_LONGTEXT("longtext", DataTypeEnum.STRING_TYPE),
    //二进制字符串
    MYSQL_BINARY("binary", DataTypeEnum.BINARY_BIG_TYPE),
    MYSQL_VARBINARY("varbinary", DataTypeEnum.BINARY_BIG_TYPE),
    MYSQL_TINYBLOB("tinyblob", DataTypeEnum.BINARY_BIG_TYPE),
    MYSQL_BLOB("blob", DataTypeEnum.BINARY_BIG_TYPE),
    MYSQL_MEDIUMBLOB("mediumblob", DataTypeEnum.BINARY_BIG_TYPE),
    MYSQL_LONGBLOB("longblob", DataTypeEnum.BINARY_BIG_TYPE),

    /**
     * 时间型 目前只支持datetime timestamp 两个类型的筛选
     */
    MYSQL_YEAR("year", DataTypeEnum.OTHER_TIME_TYPE),
    MYSQL_TIME("time", DataTypeEnum.OTHER_TIME_TYPE),
    MYSQL_DATE("date", DataTypeEnum.OTHER_TIME_TYPE),
    //datetime timestamp
    MYSQL_DATETIME("datetime", DataTypeEnum.DATETIME_TYPE),
    MYSQL_TIMESTAMP("timestamp", DataTypeEnum.DATETIME_TYPE),

    /**
     * 空间型
     */
    //单值类型
    MYSQL_GEOMETRY("geometry", DataTypeEnum.OTHER_TYPE),
    MYSQL_POINT("point", DataTypeEnum.OTHER_TYPE),
    MYSQL_LINESTRING("linestring", DataTypeEnum.OTHER_TYPE),
    MYSQL_POLYGON("polygon", DataTypeEnum.OTHER_TYPE),
    //集合类型
    MYSQL_MULTIPOINT("multipoint", DataTypeEnum.OTHER_TYPE),
    MYSQL_MULTILINESTRING("multilinestring", DataTypeEnum.OTHER_TYPE),
    MYSQL_MULTIPOLYGON("multipolygon", DataTypeEnum.OTHER_TYPE),
    MYSQL_GEOMETRYCOLLECTION("geometrycollection", DataTypeEnum.OTHER_TYPE),

    /**
     * 特殊字符串类型
     */
    MYSQL_ENUM("enum", DataTypeEnum.STRING_TYPE),
    MYSQL_SET("set", DataTypeEnum.STRING_TYPE),
    MYSQL_JSON("json", DataTypeEnum.STRING_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    MYSQL_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;

    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static MysqlColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(MysqlColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(MYSQL_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.MYSQL;
    }

    @Override
    public MysqlColumnEnum parseColumnType(String typeString) {
        return MysqlColumnEnum.parse(typeString);
    }
}
