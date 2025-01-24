package cn.cisdigital.datakits.framework.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author xxx
 */
@Getter
@AllArgsConstructor
public enum SapColumnEnum implements ColumnType {

    /**
     * 数值型
     */
    SAP_NUMC("NUMC", DataTypeEnum.NUMBER_TYPE),
    SAP_DEC("DEC", DataTypeEnum.NUMBER_TYPE),
    SAP_INT1("INT1", DataTypeEnum.NUMBER_TYPE),
    SAP_INT2("INT2", DataTypeEnum.NUMBER_TYPE),
    SAP_INT4("INT4", DataTypeEnum.NUMBER_TYPE),
    SAP_FLTP("FLTP", DataTypeEnum.NUMBER_TYPE),
    SAP_CLNT("CLNT", DataTypeEnum.NUMBER_TYPE),
    /**
     * 字符型
     */
    SAP_CHAR("CHAR", DataTypeEnum.STRING_TYPE),
    SAP_STRING("STRING", DataTypeEnum.STRING_TYPE),
    SAP_VARC("VARC", DataTypeEnum.STRING_TYPE),
    /**
     * 日期型
     */
    SAP_DATS("DATS", DataTypeEnum.DATETIME_TYPE),
    SAP_TIMS("TIMS", DataTypeEnum.OTHER_TIME_TYPE),
    /**
     * 二进制
     */
    SAP_RAW("RAW", DataTypeEnum.BINARY_BIG_TYPE),
    SAP_LRAW("LRAW", DataTypeEnum.BINARY_BIG_TYPE),
    SAP_RAWSTRING("RAWSTRING", DataTypeEnum.BINARY_BIG_TYPE),
    /**
     * 其他类型
     */
    SAP_CUKY("CUKY", DataTypeEnum.OTHER_TYPE),
    SAP_UNIT("UNIT", DataTypeEnum.OTHER_TYPE),
    SAP_QUAN("QUAN", DataTypeEnum.OTHER_TYPE),
    SAP_CURR("CURR", DataTypeEnum.OTHER_TYPE),
    SAP_LANG("LANG", DataTypeEnum.OTHER_TYPE),

    /**
     * 用户自定义以及其他未知类型
     */
    SAP_UNKNOWN_DEFINE("UNKNOWN_DEFINE_TYPE", DataTypeEnum.OTHER_TYPE),

    ;
    private String type;
    private DataTypeEnum dataType;


    /**
     * 如果字符串符合规定筛选的字段，这返回此枚举
     *
     * @param typeString 枚举的字段 不区分大小写
     */
    @JsonCreator
    public static SapColumnEnum parse(String typeString) {
        if (typeString == null) {
            return null;
        }
        return Arrays.stream(SapColumnEnum.values()).filter(type -> type.getType().equalsIgnoreCase(typeString)).findFirst().orElse(SAP_UNKNOWN_DEFINE);
    }

    @Override
    public DataBaseTypeEnum getDatabaseTypeEnum() {
        return DataBaseTypeEnum.SAP;
    }

    @Override
    public SapColumnEnum parseColumnType(String typeString) {
        return SapColumnEnum.parse(typeString);
    }
}
