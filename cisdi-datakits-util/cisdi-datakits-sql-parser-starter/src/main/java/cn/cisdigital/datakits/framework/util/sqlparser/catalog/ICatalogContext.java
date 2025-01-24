package cn.cisdigital.datakits.framework.util.sqlparser.catalog;

import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ContextDataSourceDto;

/**
 * 业务测catalog上下文
 */
public interface ICatalogContext {

    /**
     * 返回数据库信息
     *
     * @return 数据库信息
     */
    ContextDataSourceDto getDatasourceInfo();
}
