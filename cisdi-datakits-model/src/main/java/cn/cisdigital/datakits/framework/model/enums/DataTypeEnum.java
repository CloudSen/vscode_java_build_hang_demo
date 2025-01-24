package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xxx
 * @since 2022-06-30-15:44
 */
@Getter
@AllArgsConstructor
public enum DataTypeEnum implements BaseEnum {

    STRING_TYPE(1,"字符型"),
    NUMBER_TYPE(2,"数字型"),
    DATETIME_TYPE(3,"日期时间、时间戳型，对应java以及各个数据库的datetime,timestamp类型"),
    BOOL_TYPE(4,"布尔型"),
    BINARY_BIG_TYPE(5,"二进制数据类型,包含二进制文本以及部分数据库的大文本"),
    OTHER_TIME_TYPE(6,"其他时间型，包含date,year,time等类型"),

    OTHER_TYPE(-1,"其他类型，不同数据库的特性类型"),
    ;
    private int type;
    private String desc;


    @Deprecated
    public static DataTypeEnum getDataTypeEnum (Integer type){
        for (DataTypeEnum dataTypeEnum : DataTypeEnum.values()) {
            if (type == dataTypeEnum.getType()) {
                return dataTypeEnum;
            }
        }
        return null;
    }

    @JsonCreator
    public static DataTypeEnum parse(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(DataTypeEnum.values()).filter(v -> v.getCode() == value).findFirst().orElse(null);
    }

    @Override
    public int getCode() {
        return type;
    }

    @Override
    public String getKey() {
        return desc;
    }
}
