package cn.cisdigital.datakits.framework.util.sqlparser.catalog;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;

/**
 * @author xxx
 * @since 2024/4/19 15:55
 */
public interface IDatakitsFieldType {

    ColumnType getFieldColumnType();
    String getFieldComment();
}
