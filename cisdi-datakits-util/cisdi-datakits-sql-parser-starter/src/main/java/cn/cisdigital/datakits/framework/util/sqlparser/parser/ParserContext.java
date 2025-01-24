package cn.cisdigital.datakits.framework.util.sqlparser.parser;

import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogContext;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.IFunctionConfig;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.function.DorisFunctionOperator;
import cn.cisdigital.datakits.framework.util.sqlparser.schema.CatalogCalciteSchemaAdapter;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.Getter;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 解析器上下文
 * 创建相关类：{@link org.apache.calcite.tools.RelBuilder} or {@link FrameworkConfig}，用来构造{@link SqlValidator},{@link SqlParser}. {@link SqlToRelConverter}
 * <p>封装，构造解析器需要的参数如: root schema, cost factory, type system etc等</p>
 */
@Getter
public class ParserContext {

    /**
     * 数据类型描述工厂类
     */
    private final RelDataTypeFactory relDataTypeFactory;

    /**
     * 函数库
     */
    private final SqlOperatorTable sqlOperatorTableForFunction;

    /**
     * calcite catalog schema 封装了{@link org.apache.calcite.schema.Schema}的实现
     * <p>{@link cn.cisdigital.datakits.framework.util.sqlparser.schema.CatalogCalciteSchemaAdapter}
     */
    private final CalciteSchema rootSchema;

    private final ICatalogDiscover catalogDiscover;

    /**
     * 框架配置
     */
    private final FrameworkConfig frameworkConfig;

    /**
     * catalog context
     */
    private final ICatalogContext catalogContext;

    private final IParserConfigBuildStrategy parserConfigBuildStrategy;

    @Builder
    public ParserContext(
            ICatalogDiscover catalogDiscover,
            ICatalogContext catalogContext,
            SqlParser.Config config,
            IParserConfigBuildStrategy parserConfigBuildStrategy) {
        this.relDataTypeFactory = parserConfigBuildStrategy.buildRelDataTypeFactory();
        this.rootSchema = CalciteSchema.from(CalciteSchema.createRootSchema(false, false, "schema",
                new CatalogCalciteSchemaAdapter(catalogDiscover)).plus());
        this.catalogContext = catalogContext;
        this.sqlOperatorTableForFunction = parserConfigBuildStrategy.buildSqlOperatorTableFunctions();
        this.frameworkConfig = createFrameworkConfig(config);
        this.catalogDiscover = catalogDiscover;
        this.parserConfigBuildStrategy = parserConfigBuildStrategy;
    }

    /**
     * 创建框架配置
     *
     * @param config config
     * @return config
     */
    private FrameworkConfig createFrameworkConfig(SqlParser.Config config) {
        return Frameworks.newConfigBuilder()
                .parserConfig(config)
                .sqlToRelConverterConfig(SqlToRelConverter.config().withTrimUnusedFields(false))
                .sqlValidatorConfig(SqlValidator.Config.DEFAULT.
                        // 节点校验时，如果类型不匹配是不进行隐式类型强转，
                                withTypeCoercionEnabled(false))
                .defaultSchema(rootSchema.plus())
                .operatorTable(sqlOperatorTableForFunction)
                .convertletTable(StandardConvertletTable.INSTANCE)
                .build();
    }

    /**
     * 获取catalog reader
     *
     * @return catalog reader
     */
    public CalciteCatalogReader createCatalogReader() {
        final SchemaPlus finalRootSchema = getRootSchema(rootSchema.plus());
        return new CalciteCatalogReader(
                CalciteSchema.from(finalRootSchema),
                // 设置默认路径
                StringUtils.isEmpty(catalogContext.getDatasourceInfo().getSchemaName()) ? Collections.emptyList() :
                        CalciteSchema.from(finalRootSchema).path(catalogContext.getDatasourceInfo().getSchemaName()),
                relDataTypeFactory,
                new CalciteConnectionConfigImpl(new Properties()));
    }

    public RelOptCluster createCluster() {
        final RexBuilder rexBuilder = new RexBuilder(relDataTypeFactory);
        // init the planner
        HepProgramBuilder builder = new HepProgramBuilder();
        RelOptPlanner planner = new HepPlanner(builder.build());
        //note: init cluster: An environment for related relational expressions during the optimization of a query.
        return RelOptCluster.create(planner, rexBuilder);
    }


    private SchemaPlus getRootSchema(SchemaPlus schema) {
        if (schema.getParentSchema() == null) {
            return schema;
        } else {
            return getRootSchema(schema.getParentSchema());
        }
    }

    public void close() throws Exception {
        catalogDiscover.close();
    }
}
