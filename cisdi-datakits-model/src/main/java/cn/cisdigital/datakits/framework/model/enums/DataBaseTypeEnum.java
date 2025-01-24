package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-08-09-9:45
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum DataBaseTypeEnum implements BaseEnum {

    /**
     * MYSQL
     */
    MYSQL(1, "`", SourceType.DATABASE, "com.mysql.cj.jdbc.Driver", false, false),
    /**
     * ROCKET_MQ
     */
    ROCKET_MQ(2, "", SourceType.MQ, null, false, false),
    /**
     * DORIS
     */
    DORIS(3, "`", SourceType.DATABASE, "com.mysql.cj.jdbc.Driver", false, false),
    /**
     * DB2
     */
    DB2(4, "\"", SourceType.DATABASE, "com.ibm.db2.jcc.DB2Driver", true, false),
    /**
     * Oracle
     */
    ORACLE(5, "\"", SourceType.DATABASE, "oracle.jdbc.driver.OracleDriver", true, false),
    /**
     * MS SQL Server
     */
    MS_SQL_SERVER(6, "[]", SourceType.DATABASE, "com.microsoft.sqlserver.jdbc.SQLServerDriver", true, false),
    /**
     * PostgreSQL
     */
    POSTGRE_SQL(7, "\"", SourceType.DATABASE, "org.postgresql.Driver", true, false),
    /**
     * SQLite
     */
    SQLITE(8, "`", SourceType.DATABASE, "org.sqlite.JDBC", false, true),
    /**
     * MS_ACCESS
     */
    MS_ACCESS(9, "`", SourceType.DATABASE, "net.ucanaccess.jdbc.UcanaccessDriver", false, true),
    /**
     * 达梦8
     */
    DM(10,"\"",SourceType.DATABASE,"dm.jdbc.driver.DmDriver",true, false),
    /**
     * 人大金仓
     */
    KING_BASE_V8(11,"\"",SourceType.DATABASE,"com.kingbase8.Driver",true, false),
    /**
     * Kafka
     */
    KAFKA(12, "", SourceType.MQ, null, false, false),
    /**
     * TiDB
     */
    TIDB(13, "`", SourceType.DATABASE, "com.mysql.cj.jdbc.Driver", false, false),
    /**
     * sybase iq
     */
    SYBASE_IQ(14, "\"", SourceType.DATABASE, "com.sybase.jdbc4.jdbc.SybDriver", false, false),
    /**
     * api
     */
    API(15, "", SourceType.API, null, false, false),
    /**
     * sap
     */
    SAP(16, "", SourceType.SAP, null, false, false),

    /**
     * OTHER_TYPE
     */
    OTHER_TYPE(-1, "", null, null, false, false),
    ;

    /**
     * 枚举值
     */
    final int value;
    /**
     * 转义符，例如MySQL中的"`"，使用示例：insert into `table`
     */
    final String escapeCharacter;
    /**
     * 是否为数据库类型
     */
    final SourceType sourceType;
    /**
     * 驱动名称
     */
    final String driverClassName;
    /**
     * 是否嵌入schema
     */
    final boolean schemaEmbed;
    /**
     * 是否为嵌入式文件型数据库，例如SQLite
     */
    final boolean fileEmbedDatabase;

    @Override
    public int getCode() {
        return value;
    }

    @Override
    public String getKey() {
        return this.name();
    }

    public static DataBaseTypeEnum parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(DataBaseTypeEnum.values()).filter(type -> type.getValue() == value).findFirst().orElse(null);
    }

    /**
     * 包装转义符
     */
    public String wrappedEscapeCharacter(String source) {
        char[] chars = this.getEscapeCharacter().toCharArray();
        String leftEscapeCharacter = String.valueOf(chars[0]);
        String rightEscapeCharacter = leftEscapeCharacter;
        if (chars.length > 1) {
            rightEscapeCharacter = String.valueOf(chars[1]);
        }
        if (StrUtil.isBlank(source) || (source.startsWith(leftEscapeCharacter) && source.endsWith(rightEscapeCharacter))) {
            return source;
        }
        return leftEscapeCharacter + source + rightEscapeCharacter;
    }

    /**
     * 删除转义符
     */
    public String unwrappedEscapeCharacter(String source) {
        char[] chars = this.getEscapeCharacter().toCharArray();
        String leftEscapeCharacter = String.valueOf(chars[0]);
        String rightEscapeCharacter = leftEscapeCharacter;
        if (chars.length > 1) {
            rightEscapeCharacter = String.valueOf(chars[1]);
        }
        if (StrUtil.isBlank(source) || (!source.startsWith(leftEscapeCharacter) && !source.endsWith(rightEscapeCharacter))) {
            return source;
        }
        return source.replace(leftEscapeCharacter, "").replace(rightEscapeCharacter, "");
    }
}
