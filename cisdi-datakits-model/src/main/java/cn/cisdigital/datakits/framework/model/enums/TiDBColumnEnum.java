package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

/**
 * @author xxx
 * @since 2022-11-04-9:20
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum TiDBColumnEnum implements ColumnType {

    /**
     * 数值型 包含bit位类型
     */
    TIDB_BOOLEAN("boolean", DataTypeEnum.NUMBER_TYPE),
    TIDB_BIT("bit",DataTypeEnum.NUMBER_TYPE),

    TIDB_TINYINT("tinyint", DataTypeEnum.NUMBER_TYPE),
    TIDB_TINYINT_UNSIGNED("tinyint unsigned", DataTypeEnum.NUMBER_TYPE),
    TIDB_INT("int", DataTypeEnum.NUMBER_TYPE),
    TIDB_INT_UNSIGNED("int unsigned", DataTypeEnum.NUMBER_TYPE),
    TIDB_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    TIDB_SMALLINT_UNSIGNED("smallint unsigned", DataTypeEnum.NUMBER_TYPE),
    TIDB_MEDIUMINT("mediumint", DataTypeEnum.NUMBER_TYPE),
    TIDB_MEDIUMINT_UNSIGNED("mediumint unsigned", DataTypeEnum.NUMBER_TYPE),
    TIDB_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    TIDB_BIGINT_UNSIGNED("bigint unsigned", DataTypeEnum.NUMBER_TYPE),
    //浮点
    TIDB_FLOAT("float", DataTypeEnum.NUMBER_TYPE),
    TIDB_FLOAT_UNSIGNED("float unsigned", DataTypeEnum.NUMBER_TYPE),
    TIDB_DOUBLE("double", DataTypeEnum.NUMBER_TYPE),
    TIDB_DOUBLE_UNSIGNED("double unsigned", DataTypeEnum.NUMBER_TYPE),
    //定点
    TIDB_DECIMAL("decimal", DataTypeEnum.NUMBER_TYPE),
    TIDB_DECIMAL_UNSIGNED("decimal unsigned", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    TIDB_CHAR("char", DataTypeEnum.STRING_TYPE),
    TIDB_VARCHAR("varchar", DataTypeEnum.STRING_TYPE),
    TIDB_TINYTEXT("tinytext", DataTypeEnum.STRING_TYPE),
    TIDB_TEXT("text", DataTypeEnum.STRING_TYPE),
    TIDB_MEDIUMTEXT("mediumtext", DataTypeEnum.STRING_TYPE),
    TIDB_LONGTEXT("longtext", DataTypeEnum.STRING_TYPE),
    //二进制字符串
    TIDB_BINARY("binary", DataTypeEnum.BINARY_BIG_TYPE),
    TIDB_VARBINARY("varbinary", DataTypeEnum.BINARY_BIG_TYPE),
    TIDB_TINYBLOB("tinyblob", DataTypeEnum.BINARY_BIG_TYPE),
    TIDB_BLOB("blob", DataTypeEnum.BINARY_BIG_TYPE),
    TIDB_MEDIUMBLOB("mediumblob", DataTypeEnum.BINARY_BIG_TYPE),
    TIDB_LONGBLOB("longblob", DataTypeEnum.BINARY_BIG_TYPE),

    /**
     * 时间型 目前只支持datetime timestamp 两个类型的筛选
     */
    TIDB_YEAR("year", DataTypeEnum.OTHER_TIME_TYPE),
    TIDB_TIME("time", DataTypeEnum.OTHER_TIME_TYPE),
    TIDB_DATE("date", DataTypeEnum.OTHER_TIME_TYPE),
    //datetime timestamp
    TIDB_DATETIME("datetime", DataTypeEnum.DATETIME_TYPE),
    TIDB_TIMESTAMP("timestamp", DataTypeEnum.DATETIME_TYPE),

    /**
     * 特殊字符串类型
     */
    TIDB_ENUM("enum", DataTypeEnum.STRING_TYPE),
    TIDB_SET("set", DataTypeEnum.STRING_TYPE),
    TIDB_JSON("json", DataTypeEnum.STRING_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    TIDB_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;

    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static TiDBColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(TiDBColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(TIDB_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.TIDB;
    }

    @Override
    public TiDBColumnEnum parseColumnType(String typeString) {
        return TiDBColumnEnum.parse(typeString);
    }
}
