package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-02-13-9:48
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum PostgreSqlColumnEnum implements ColumnType {

    /**
     * 数值型 包括别名
     */
    PGSQL_SMALLINT("smallint", DataTypeEnum.NUMBER_TYPE),
    PGSQL_INTEGER ("integer", DataTypeEnum.NUMBER_TYPE),
    PGSQL_BIGINT("bigint", DataTypeEnum.NUMBER_TYPE),
    PGSQL_DECIMAL("decimal", DataTypeEnum.NUMBER_TYPE),
    PGSQL_REAL("real", DataTypeEnum.NUMBER_TYPE),
    PGSQL_NUMERIC("numeric", DataTypeEnum.NUMBER_TYPE),
    PGSQL_DOUBLE_PRECISION("double precision", DataTypeEnum.NUMBER_TYPE),
    PGSQL_SMALLSERIAL("smallserial", DataTypeEnum.NUMBER_TYPE),
    PGSQL_SERIAL("serial", DataTypeEnum.NUMBER_TYPE),
    PGSQL_BIGSERIAL("bigserial", DataTypeEnum.NUMBER_TYPE),
    PGSQL_INT8("int8", DataTypeEnum.NUMBER_TYPE),
    PGSQL_SERIAL8("serial8", DataTypeEnum.NUMBER_TYPE),
    PGSQL_FLOAT8("float8", DataTypeEnum.NUMBER_TYPE),
    PGSQL_INT("int", DataTypeEnum.NUMBER_TYPE),
    PGSQL_INT4("int4", DataTypeEnum.NUMBER_TYPE),
    PGSQL_FLOAT4("float4", DataTypeEnum.NUMBER_TYPE),
    PGSQL_INT2("int2", DataTypeEnum.NUMBER_TYPE),
    PGSQL_SERIAL2("serial2", DataTypeEnum.NUMBER_TYPE),
    PGSQL_SERIAL4("serial4", DataTypeEnum.NUMBER_TYPE),
    PGSQL_OID("oid", DataTypeEnum.NUMBER_TYPE),
    /**
     * 字符型 包括别名
     */
    PGSQL_BIT("bit", DataTypeEnum.STRING_TYPE),
    PGSQL_BIT_VARYING("bit varying", DataTypeEnum.STRING_TYPE),
    PGSQL_VARBIT("varbit", DataTypeEnum.STRING_TYPE),
    PGSQL_CHARACTER("character", DataTypeEnum.STRING_TYPE),
    PGSQL_CHAR("char", DataTypeEnum.STRING_TYPE),
    PGSQL_VARCHAR("varchar", DataTypeEnum.STRING_TYPE),
    PGSQL_TEXT("text", DataTypeEnum.STRING_TYPE),
    PGSQL_CHARACTER_VARYING("character varying", DataTypeEnum.STRING_TYPE),
    //通用唯一标识码
    PGSQL_UUID("uuid", DataTypeEnum.STRING_TYPE),
    /**
     * 时间型
     */
    PGSQL_TIMESTAMP("timestamp", DataTypeEnum.DATETIME_TYPE),
    PGSQL_TIMESTAMP_WITHOUT_TIME_ZONE("timestamp without time zone", DataTypeEnum.DATETIME_TYPE),
    PGSQL_TIMESTAMP_WITH_TIME_ZONE("timestamp with time zone", DataTypeEnum.DATETIME_TYPE),
    PGSQL_TIMESTAMPTZ("timestamptz", DataTypeEnum.DATETIME_TYPE),

    PGSQL_DATE("date", DataTypeEnum.OTHER_TIME_TYPE),
    PGSQL_TIME_WITHOUT_TIME_ZONE("time without time zone", DataTypeEnum.OTHER_TIME_TYPE),
    PGSQL_TIME_WITH_TIME_ZONE("time with time zone", DataTypeEnum.OTHER_TIME_TYPE),
    PGSQL_TIMETZ("timetz", DataTypeEnum.OTHER_TIME_TYPE),
    PGSQL_INTERVAL("interval", DataTypeEnum.OTHER_TIME_TYPE),

    /**
     * bool
     */
    PGSQL_BIT_1("bit(1)",DataTypeEnum.BOOL_TYPE),
    PGSQL_BOOLEAN("boolean", DataTypeEnum.BOOL_TYPE),
    PGSQL_BOOL("bool", DataTypeEnum.BOOL_TYPE),
    /**
     * 其他
     */
    //空间
    PGSQL_POINT("point", DataTypeEnum.OTHER_TYPE),
    PGSQL_LINE("line", DataTypeEnum.OTHER_TYPE),
    PGSQL_LSEG("lseg", DataTypeEnum.OTHER_TYPE),
    PGSQL_BOX("box", DataTypeEnum.OTHER_TYPE),
    PGSQL_PATH("path", DataTypeEnum.OTHER_TYPE),
    PGSQL_POLYGON("polygon", DataTypeEnum.OTHER_TYPE),
    PGSQL_CIRCLE("circle", DataTypeEnum.OTHER_TYPE),
    //IPv4或IPv6网络地址
    PGSQL_CIDR("cidr", DataTypeEnum.OTHER_TYPE),
    //IPv4或IPv6主机地址
    PGSQL_INET("inet", DataTypeEnum.OTHER_TYPE),
    PGSQL_JSON("json", DataTypeEnum.OTHER_TYPE),
    //二进制 JSON 数据，已分解
    PGSQL_JSONB("jsonb", DataTypeEnum.OTHER_TYPE),
    //MAC（Media Access Control）地址
    PGSQL_MACADDR("macaddr", DataTypeEnum.OTHER_TYPE),
    //MAC（Media Access Control）地址（EUI-64格式）
    PGSQL_MACADDR8("macaddr8", DataTypeEnum.OTHER_TYPE),
    //货币数量
    PGSQL_MONEY("money", DataTypeEnum.OTHER_TYPE),
    //PostgreSQL日志序列号
    PGSQL_PG_LSN("pg_lsn", DataTypeEnum.OTHER_TYPE),
    //用户级事务ID快照
    PGSQL_PG_SNAPSHOT("pg_snapshot", DataTypeEnum.OTHER_TYPE),
    //文本搜索查询
    PGSQL_TSVECTOR("tsvector", DataTypeEnum.OTHER_TYPE),
    //文本搜索文档
    PGSQL_TSQUERY("tsquery", DataTypeEnum.OTHER_TYPE),
    //用户级别事务ID快照(废弃; 参见 pg_snapshot)
    PGSQL_TXID_SNAPSHOT("txid_snapshot", DataTypeEnum.OTHER_TYPE),
    //XML数据
    PGSQL_XML("xml", DataTypeEnum.OTHER_TYPE),
    //枚举
    PGSQL_ENUM("enum ", DataTypeEnum.OTHER_TYPE),
    //二进制
    PGSQL_BYTEA("bytea",DataTypeEnum.BINARY_BIG_TYPE),
    //todo 伪类型 暂时没研究如何使用
    /**
     * 用户自定义以及其他未知类型
     */
    PGSQL_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;
    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static PostgreSqlColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(PostgreSqlColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(PGSQL_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.POSTGRE_SQL;
    }

    @Override
    public PostgreSqlColumnEnum parseColumnType(String typeString) {
        return PostgreSqlColumnEnum.parse(typeString);
    }
}
