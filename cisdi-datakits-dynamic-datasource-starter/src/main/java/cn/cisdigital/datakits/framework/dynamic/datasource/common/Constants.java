package cn.cisdigital.datakits.framework.dynamic.datasource.common;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xxx
 * @since 2022-08-09-8:51
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    /**
     * 120分钟guava缓存访问过期时间
     */
    public static final long MAX_EXPIRE_TIME_TO_CACHE_ACCESS = 120L;
    /**
     * sql执行过期超时时间 秒
     */
    public static final int STATEMENT_TIMEOUT = 20;
    /**
     * 执行use database语句
     */
    public static final String MYSQL_USE = "use ";
    /**
     * 空白符号
     */
    public static final String LINE_BREAK = "\n";
    public static final String TABS = "\t";
    public static final String LINE_BREAK_AND_TABS = "\n\t";
    /**
     * 符号
     */
    public static final String BLANK = " ";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String BACK_QUOTE = "`";
    public static final String QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String EQUAL = "=";
    public static final String DASH = "-";
    /**
     * 对称加密的密钥字符串，勿轻易更改
     */
    public static final String KEY_DEFINE = "mVowsbXkBd";

    /**
     * SQL语法常见关键字
     */
    public static final String COLUMN = "COLUMN";
    public static final String FIRST = "FIRST";
    public static final String AFTER = "AFTER";
    public static final String NOT_NULL = "NOT NULL";
    public static final String NULL = "NULL";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String COMMENT = "COMMENT";
    public static final String PARTITION_BY = "PARTITION BY";
    public static final String COMMENT_EQUAL = "COMMENT=";
    public static final String DEFAULT = "DEFAULT";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s;";
    public static final String ALTER_TABLE = "ALTER TABLE %s";
    public static final String RENAME_TABLE = "RENAME TABLE %s TO %s;";
    public static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS";
    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final String TO = "TO";

    /**
     * doris 关键字 以及语法配置
     */
    public static final String ALTER_TABLE_RENAME = "ALTER TABLE %s RENAME %s;";
    public static final String KEY = "KEY";
    public static final String UNIQUE_KEY = "UNIQUE KEY";
    public static final String DISTRIBUTED_BY_HASH = "DISTRIBUTED BY HASH";
    public static final String BUCKETS = "BUCKETS";
    public static final String PROPERTIES = "PROPERTIES";
    /**
     * 正则筛选括号里
     */
    public static final String BRACKET_REGEX = "\\((.*?)\\)";
    /**
     * 删除括号及其里面的内容
     */
    public static final String CLEAR_BRACKET_REGEX = "\\(.*\\)";


    public static final String CHECK_DATABASE_TYPE_ERROR_MESSAGE = "该数据库连接类型为%s，请修改";

    /**
     * Doris数值范围
     */
    public static final int DORIS_TINYINT_MIN = -128;
    public static final int DORIS_TINYINT_MAX = 127;
    public static final int DORIS_SMALLINT_MIN = -32768;
    public static final int DORIS_SMALLINT_MAX = 32767;
    public static final int DORIS_INT_MIN = Integer.MIN_VALUE;
    public static final int DORIS_INT_MAX = Integer.MAX_VALUE;
    public static final long DORIS_BIGINT_MIN = Long.MIN_VALUE;
    public static final long DORIS_BIGINT_MAX = Long.MAX_VALUE;

    public static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    /**
     * Access的JDBC参数变量分隔符
     */
    public static final String ACCESS_PARAM_SEPARATOR = ";";
    public static final String ACCESS_KV_SEPARATOR = "=";
    public static final String ACCESS_PROTOCOL_PREFIX = "jdbc:ucanaccess://";
    public static final String ACCESS_PARAM_MIRROR_FOLDER_KEY = "mirrorFolder";

    public static final String JDBC_REMOTE_ERROR_URL_KEYWORD = "tracking_url=";


    public static final String DATASOURCE_ERROR_CODE_PREFIX ="000003";

    /**
     * ERROR_CODE:1044;SQLSTATE: 42000 Message: Access denied for user '%s'@'%s' to database '%s'
     * ERROR_CODE:1142;SQLSTATE: 42000 Message: %s command denied to user '%s'@'%s' for table '%s'
     * ERROR_CODE:1143;SQLSTATE: 42000 Message: %s command denied to user '%s'@'%s' for column '%s' in table '%s'
     * ERROR_CODE:1227;SQLSTATE: 42000 Message: Access denied; you need (at least one of) the %s privilege(s) for this operation
     * ERROR_CODE:1370;SQLSTATE: 42000 Message: %s command denied to user '%s'@'%s' for routine '%s'
     */
    public static final List<Integer> DATASOURCE_ACCESS_DENIED_ERROR_CODES = Lists.newArrayList(1044,1142,1143,1227,1370);

    public static final String DATASOURCE_ACCESS_DENIED_ERROR_SQLSTATE = "42000";

    public static final String ALTER_DORIS_TABLE_FORMAT = "ALTER TABLE `%s`.`%s`";
    public static final String ALTER_DORIS_TABLE_COMMENT_FORMAT = " MODIFY COMMENT '%s'";
    public static final String ALTER_DORIS_COLUMN_COMMENT_FORMAT = " MODIFY COLUMN `%s` COMMENT '%s'";

    public static final String CREATE_INDEX_TEMP = "ALTER TABLE %s ADD INDEX %s(%s) USING %s ";
    public static final String DROP_INDEX_TEMP = "ALTER TABLE %s DROP INDEX %s; ";
    public static final String ALTER_DROP_INDEX_TEMP_SEG ="DROP INDEX %s";
    public static final String CREATE_INDEX_WITH_TABLE_SQL_SEGMENT=" INDEX %s(%s) USING %s";

    public static final String CREATE_VIEW_TEMP = "CREATE VIEW IF NOT EXISTS %s";
    public static final String ALTER_VIEW_TEMP = "ALTER VIEW %s";
    public static final String DROP_VIEW_TEMP = "DROP VIEW IF EXISTS %s";

    public static final String AS = "AS";
}
