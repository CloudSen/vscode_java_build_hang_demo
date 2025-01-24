package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-09-25-10:37
 */

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum KingBaseColumnEnum implements ColumnType{

    /**
     * 字符
     */
    //元数据读取的是bpchar
    KB_CHARACTER("character",DataTypeEnum.STRING_TYPE),
    KB_VARCHAR("varchar",DataTypeEnum.STRING_TYPE),
    KB_TEXT("text",DataTypeEnum.STRING_TYPE),

    /**
     * 数字
     */
    //整型
    KB_TINYINT("tinyint", DataTypeEnum.NUMBER_TYPE),
    KB_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    KB_INTEGER("integer", DataTypeEnum.NUMBER_TYPE),
    KB_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    //精度整型 NUMERIC 不指定 p,s 则默认设置最大p,s 元数据为null 别名 DECIMAL NUMBER
    KB_NUMERIC("numeric", DataTypeEnum.NUMBER_TYPE),
    //浮点
    KB_REAL("real", DataTypeEnum.NUMBER_TYPE),
    KB_DOUBLE_PRECISION("double precision", DataTypeEnum.NUMBER_TYPE),
    //货币
    KB_MONEY("money", DataTypeEnum.NUMBER_TYPE),

    /**
     * 时间
     */
    // date/datetime 元数据需要查询domain_name 确定
    KB_DATE("date", DataTypeEnum.DATETIME_TYPE),
    KB_DATETIME("datetime", DataTypeEnum.DATETIME_TYPE),
    KB_TIMESTAMP("timestamp", DataTypeEnum.DATETIME_TYPE),

    KB_TIME("time",DataTypeEnum.OTHER_TIME_TYPE),
    KB_TIME_WITH_TIME_ZONE("time with time zone",DataTypeEnum.OTHER_TIME_TYPE),
    KB_TIMESTAMP_WITH_TIME_ZONE("timestamp with time zone",DataTypeEnum.OTHER_TIME_TYPE),
    KB_TIMESTAMP_WITH_LOCAL_TIME_ZONE("timestamp with local time zone",DataTypeEnum.OTHER_TIME_TYPE),
    //时间间隔类型 需要udt_name 确定
    KB_INTERVAL("interval",DataTypeEnum.OTHER_TIME_TYPE),

    /**
     * bool
     */
    KB_BOOLEAN("boolean",DataTypeEnum.BOOL_TYPE),

    /**
     * 其他大文本
     */
    KB_BIT("bit",DataTypeEnum.BINARY_BIG_TYPE),
    KB_BIT_VARYING("bit varying",DataTypeEnum.BINARY_BIG_TYPE),
    KB_BINARY("binary",DataTypeEnum.BINARY_BIG_TYPE),
    KB_VARBINARY("varbinary",DataTypeEnum.BINARY_BIG_TYPE),
    //BLOB CLOB NCLOB 元数据需要查询domain_name 确定
    KB_BLOB("blob",DataTypeEnum.BINARY_BIG_TYPE),
    KB_CLOB("clob",DataTypeEnum.BINARY_BIG_TYPE),
    KB_NCLOB("nclob",DataTypeEnum.BINARY_BIG_TYPE),
    KB_BYTEA("bytea",DataTypeEnum.BINARY_BIG_TYPE),
    /**
     * 用户自定义以及其他未知类型
     * 几何类型
     */
    KB_POINT("point", DataTypeEnum.OTHER_TYPE),
    KB_LINE("line", DataTypeEnum.OTHER_TYPE),
    KB_LSEG("lseg", DataTypeEnum.OTHER_TYPE),
    KB_BOX("box", DataTypeEnum.OTHER_TYPE),
    KB_PATH("path", DataTypeEnum.OTHER_TYPE),
    KB_POLYGON("polygon", DataTypeEnum.OTHER_TYPE),
    KB_CIRCLE("circle", DataTypeEnum.OTHER_TYPE),
    KB_CIDR("cidr", DataTypeEnum.OTHER_TYPE),
    KB_INET("inet", DataTypeEnum.OTHER_TYPE),
    KB_MACADDR("macaddr", DataTypeEnum.OTHER_TYPE),
    KB_MACADDR8("macaddr8", DataTypeEnum.OTHER_TYPE),
    KB_TSVECTOR("tsvector", DataTypeEnum.OTHER_TYPE),
    KB_TSQUERY("tsquery", DataTypeEnum.OTHER_TYPE),
    KB_UUID("uuid", DataTypeEnum.OTHER_TYPE),
    KB_XML("xml", DataTypeEnum.OTHER_TYPE),
    KB_JSON("JSON", DataTypeEnum.OTHER_TYPE),
    KB_JSONB("JSONB", DataTypeEnum.OTHER_TYPE),
    KB_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),
    ;

    private String type;
    private DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static KingBaseColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        if("bpchar".equalsIgnoreCase(typeString)){
            return KB_CHARACTER;
        }
        if("timestamp without time zone".equalsIgnoreCase(typeString)){
            return KB_TIMESTAMP;
        }
        if("time without time zone".equalsIgnoreCase(typeString)){
            return KB_TIME;
        }
        if(typeString.contains("interval")){
            return KB_INTERVAL;
        }
        return Arrays.stream(KingBaseColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(KB_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.KING_BASE_V8;
    }

    @Override
    public ColumnType parseColumnType(String typeString) {
        return KingBaseColumnEnum.parse(typeString);
    }
}
