package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-02-08-16:52
 */

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum DB2ColumnEnum implements ColumnType {

    /**
     * 数值型
     */
    DB2_SMALLINT("SMALLINT", DataTypeEnum.NUMBER_TYPE),
    DB2_INTEGER("INTEGER", DataTypeEnum.NUMBER_TYPE),
    DB2_BIGINT("BIGINT", DataTypeEnum.NUMBER_TYPE),
    DB2_DECFLOAT("DECFLOAT", DataTypeEnum.NUMBER_TYPE),
    DB2_DECIMAL("DECIMAL", DataTypeEnum.NUMBER_TYPE),
    DB2_REAL("REAL", DataTypeEnum.NUMBER_TYPE),
    DB2_DOUBLE("DOUBLE", DataTypeEnum.NUMBER_TYPE),
    /**
     * bool
     */
    DB2_BOOLEAN("BOOLEAN", DataTypeEnum.BOOL_TYPE),

    /**
     * 字符型 不包含图像字符串
     */
    DB2_CHARACTER("CHARACTER",DataTypeEnum.STRING_TYPE),
    DB2_CHAR("CHAR", DataTypeEnum.STRING_TYPE),
    DB2_VARCHAR("VARCHAR", DataTypeEnum.STRING_TYPE),
    DB2_GRAPHIC("GRAPHIC", DataTypeEnum.STRING_TYPE),
    DB2_VARGRAPHIC("VARGRAPHIC", DataTypeEnum.STRING_TYPE),
    //大文本以及二进制
    DB2_CLOB("CLOB", DataTypeEnum.BINARY_BIG_TYPE),
    DB2_BINARY("BINARY", DataTypeEnum.BINARY_BIG_TYPE),
    DB2_VARBINARY("VARBINARY", DataTypeEnum.BINARY_BIG_TYPE),
    DB2_BLOB("BLOB", DataTypeEnum.BINARY_BIG_TYPE),
    DB2_DBCLOB("DBCLOB", DataTypeEnum.BINARY_BIG_TYPE),
    /**
     * 时间型
     */
    DB2_DATE("DATE", DataTypeEnum.OTHER_TIME_TYPE),
    DB2_TIME("TIME", DataTypeEnum.OTHER_TIME_TYPE),
    //时间戳
    DB2_TIMESTAMP("TIMESTAMP", DataTypeEnum.DATETIME_TYPE),

    /**
     * 其他
     */
    DB2_XML("XML", DataTypeEnum.OTHER_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    DB2_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;
    private String type;
    private DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static DB2ColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(DB2ColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(DB2_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.DB2;
    }

    @Override
    public DB2ColumnEnum parseColumnType(String typeString) {
        return DB2ColumnEnum.parse(typeString);
    }
}
