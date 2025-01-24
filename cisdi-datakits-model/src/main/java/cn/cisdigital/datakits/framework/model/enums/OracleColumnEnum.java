package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-02-06-10:54
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum OracleColumnEnum implements ColumnType {

    /**
     * 数值型 包含bit位类型
     */
    ORACLE_NUMBER("NUMBER", DataTypeEnum.NUMBER_TYPE),
    ORACLE_FLOAT ("FLOAT", DataTypeEnum.NUMBER_TYPE),
    ORACLE_BINARY_DOUBLE("BINARY_DOUBLE", DataTypeEnum.NUMBER_TYPE),
    ORACLE_BINARY_FLOAT("BINARY_FLOAT", DataTypeEnum.NUMBER_TYPE),

    /**
     * 字符型
     */
    ORACLE_CHAR("CHAR", DataTypeEnum.STRING_TYPE),
    ORACLE_VARCHAR2("VARCHAR2", DataTypeEnum.STRING_TYPE),
    ORACLE_NCHAR("NCHAR", DataTypeEnum.STRING_TYPE),
    ORACLE_NVARCHAR2("NVARCHAR2", DataTypeEnum.STRING_TYPE),

    /**
     * 时间型 包括：
     * TIMESTAMP [ (fractional_seconds_precision) ][ WITH [ LOCAL ] TIME ZONE ]
     * INTERVAL YEAR [ (year_precision) ] TO MONTH
     * INTERVAL DAY [ (day_precision) ] TO SECOND [ (fractional_seconds_precision) ]
     * precision参数是动态的
     */
    ORACLE_DATE("DATE", DataTypeEnum.DATETIME_TYPE),
    ORACLE_TIMESTAMP("TIMESTAMP", DataTypeEnum.DATETIME_TYPE),
    ORACLE_TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE",DataTypeEnum.DATETIME_TYPE),

    ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE",DataTypeEnum.OTHER_TYPE),

    ORACLE_INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", DataTypeEnum.OTHER_TIME_TYPE),
    ORACLE_INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", DataTypeEnum.OTHER_TIME_TYPE),

    /**
     * 其他 包括：
     * large_object_datatypes / long_and_raw_datatypes / rowid_datatypes /
     */
    ORACLE_BLOB ("BLOB", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_CLOB ("CLOB", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_NCLOB ("NCLOB", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_BFILE ("BFILE", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_LONG ("LONG", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_LONG_RAW ("LONG RAW", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_RAW ("RAW", DataTypeEnum.BINARY_BIG_TYPE),
    ORACLE_ROWID ("ROWID", DataTypeEnum.OTHER_TYPE),
    ORACLE_UROWID ("UROWID", DataTypeEnum.OTHER_TYPE),
    /**
     * 用户自定义以及其他未知类型
     */
    ORACLE_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE",DataTypeEnum.OTHER_TYPE),
    ;



    String type;
    DataTypeEnum dataType;

    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static OracleColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        //因为数据库字段存的是带数字的动态类型，这里解析服务任务，对于某几个字段直接采用字符串匹配的方式解析
        //比如 INTERVAL DAY(2) TO SECOND(6) 只需要判断 INTERVAL DAY
        if(typeString.startsWith("INTERVAL DAY")){
            return ORACLE_INTERVAL_DAY_TO_SECOND;
        }
        if(typeString.startsWith("TIMESTAMP")){
            if(typeString.contains("WITH LOCAL TIME ZONE")){
                return ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE;
            }
            if(typeString.contains("WITH TIME ZONE")){
                return ORACLE_TIMESTAMP_WITH_TIME_ZONE;
            }
            return ORACLE_TIMESTAMP;
        }
        if(typeString.startsWith("INTERVAL YEAR")){
            return ORACLE_INTERVAL_YEAR_TO_MONTH;
        }
        return Arrays.stream(OracleColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(ORACLE_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.ORACLE;
    }

    @Override
    public OracleColumnEnum parseColumnType(String typeString) {
        return OracleColumnEnum.parse(typeString);
    }
}
