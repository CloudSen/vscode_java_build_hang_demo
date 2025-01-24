package cn.cisdigital.datakits.framework.util.sqlparser.parser;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ContextDataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SqlParserConfig;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;

import javax.sql.DataSource;

/**
 * 各个数据库类型parser配置构建策略接口
 *
 * @author xxx
 * @since 2024/4/18 18:23
 */
public interface IParserConfigBuildStrategy {

    /**
     * 构建各数据库类型的catalog
     *
     * @return catalogDiscover
     */
    ICatalogDiscover buildCatalogDiscover(final ContextDataSourceDto dataSourceDto);

    /**
     * 构造calcite各数据库类型的词法，语法配置
     *
     * @return config
     */
    SqlParser.Config buildSqlParserConfig(final SqlParserConfig config);

    /**
     * 构造calcite各数据库的拓展函数库
     *
     * @return 函数库
     */
    SqlOperatorTable buildSqlOperatorTableFunctions();


    /**
     * 数据库数据类型描述工厂
     *
     * @return 数据类型描述工厂类
     */
    RelDataTypeFactory buildRelDataTypeFactory();

    /**
     * 数据库类型
     *
     * @return dbType
     */
    DataBaseTypeEnum dbType();


}
