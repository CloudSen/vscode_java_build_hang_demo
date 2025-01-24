package cn.cisdigital.datakits.framework.model.enums;

import java.io.Serializable;

/**
 * @author xxx
 * @since 2022-11-04-9:15
 */
public interface ColumnType extends Serializable {

    /**
     * 获取类型字符串
     */
    String getType();

    /**
     * 获取类型分类
     */
    DataTypeEnum getDataType();

    /**
     * 获取此枚举是属于哪个数据库类型
     */
    DataBaseTypeEnum getDatabaseTypeEnum();

    /**
     * 解析为枚举
     */
    ColumnType parseColumnType(String typeString);

}
