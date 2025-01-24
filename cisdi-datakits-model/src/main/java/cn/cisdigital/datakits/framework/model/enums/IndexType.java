package cn.cisdigital.datakits.framework.model.enums;

import java.io.Serializable;

/**
 * @author xxx
 * @since 2024-09-04-9:15
 */
public interface IndexType extends Serializable {

    /**
     * 获取索引类型属于哪个数据库类型
     */
    DataBaseTypeEnum getDatabaseTypeEnum();

    /**
     * 返回索引类型英文名，用于SQL语句中指定索引类型
     * <p>必须保证与数据库中该索引类型的英文名一致</p>
     */
    String getIndexTypeName();

}
