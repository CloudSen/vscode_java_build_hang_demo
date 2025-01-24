package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Locale;

/**
 * @author xxx
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum SqliteColumnEnum implements ColumnType {

    /**
     * 数值型
     */
    SQLITE_INTEGER("INTEGER", DataTypeEnum.NUMBER_TYPE),
    SQLITE_NUMERIC("NUMERIC", DataTypeEnum.NUMBER_TYPE),
    SQLITE_REAL("REAL", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    SQLITE_BLOB("BLOB", DataTypeEnum.BINARY_BIG_TYPE),
    SQLITE_TEXT("TEXT", DataTypeEnum.STRING_TYPE),

    /**
     * 用户自定义以及其他未知类型
     */
    SQLITE_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),
    ;

    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     * 为了最大化SQLite和其它数据库引擎之间的数据类型兼容性，SQLite提出了"类型亲缘性(Type Affinity)"的概念。
     * 我们可以这样理解"类型亲缘性 "，在表字段被声明之后，SQLite都会根据该字段声明时的类型为其选择一种亲缘类型，当数据插入时，
     * 该字段的数据将会优先采用亲缘类型作为该值的存储方式，除非亲缘类型不匹配或无法转换当前数据到该亲缘类型，这样SQLite才会考虑其它更适合该值的类型存储该值。
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static SqliteColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        String lowerCaseType = typeString.toLowerCase(Locale.ROOT);
        //INT INTEGER TINYINT SMALLINT MEDIUMINT BIGINT UNSIGNED BIGINT INT2 INT8
        if(lowerCaseType.contains("int")){
            return SqliteColumnEnum.SQLITE_INTEGER;
        }
        if(lowerCaseType.contains("char") || lowerCaseType.contains("text") || lowerCaseType.contains("clob")){
            return SqliteColumnEnum.SQLITE_TEXT;
        }
        if( lowerCaseType.contains("double") || lowerCaseType.contains("real") || lowerCaseType.contains("float")){
            return SqliteColumnEnum.SQLITE_REAL;
        }
        if(lowerCaseType.contains("numeric") || lowerCaseType.contains("decimal")
                || lowerCaseType.contains("bool") || lowerCaseType.contains("date") || lowerCaseType.contains("time")){
            return SqliteColumnEnum.SQLITE_NUMERIC;
        }
        if(lowerCaseType.contains("blob")){
            return SqliteColumnEnum.SQLITE_BLOB;
        }
        return SqliteColumnEnum.SQLITE_UNKNOWN_DEFINE;
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.SQLITE;
    }

    @Override
    public SqliteColumnEnum parseColumnType(String typeString) {
        return SqliteColumnEnum.parse(typeString);
    }
}
