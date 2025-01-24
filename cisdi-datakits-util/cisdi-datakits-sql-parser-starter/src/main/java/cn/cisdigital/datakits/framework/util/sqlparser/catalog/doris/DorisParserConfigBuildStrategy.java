package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.function.DorisFunctionOperator;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.IParserConfigBuildStrategy;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ContextDataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SqlParserConfig;
import cn.cisdigital.datakits.framework.util.sqlparser.util.Convertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.type.DorisSqlTypeFactoryImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author xxx
 * @since 2024/4/18 18:30
 */
@Service
@Slf4j
public class DorisParserConfigBuildStrategy implements IParserConfigBuildStrategy {
    @Override
    public ICatalogDiscover buildCatalogDiscover(final ContextDataSourceDto dataSourceDto) {
        return new DorisCatalog(dataSourceDto.getSchemaName(), Convertor.toDataSourceDto(dataSourceDto));
    }

    @Override
    public SqlParser.Config buildSqlParserConfig(final SqlParserConfig config) {
        return SqlParser.config()
                // 默认设置
                .withParserFactory((SqlParserImpl.FACTORY))
                // 枚举有效的SQL兼容模式
                .withConformance(SqlConformanceEnum.MYSQL_5)
                // 设置如何引用标识符`,这里设置为``
                .withQuoting(Quoting.BACK_TICK)
                .withLex(Lex.MYSQL)
                .withCaseSensitive(config.isCaseSensitive())
                .withIdentifierMaxLength(128);
    }

    @Override
    public SqlOperatorTable buildSqlOperatorTableFunctions() {
        return DorisFunctionOperator.getInstance();
    }

    @Override
    public RelDataTypeFactory buildRelDataTypeFactory() {
        return new DorisSqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
    }

    @Override
    public DataBaseTypeEnum dbType() {
        return DataBaseTypeEnum.DORIS;
    }


}
