package cn.cisdigital.datakits.framework.util.sqlparser.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForColumnDto;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForTableDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ColumnLineageInfo;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.FieldParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.LineageParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SelectParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.TableLineageInfo;
import cn.cisdigital.datakits.framework.util.sqlparser.util.Convertor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;


@Slf4j
public class CalciteSqlParser {


    /**
     * 将Sql转化为语法树
     *
     * @param sql
     * @param parserContext
     * @return
     * @throws SqlParseException
     */
    public static SqlNode parseSqlNode(String sql, final ParserContext parserContext) throws SqlParseException {
        final FrameworkConfig frameworkConfig = parserContext.getFrameworkConfig();
        org.apache.calcite.sql.parser.SqlParser sqlParser = org.apache.calcite.sql.parser.SqlParser.create(sql, frameworkConfig.getParserConfig());
        // step1 parse
        return sqlParser.parseQuery();
    }

    /**
     * 解析INSERT
     *
     * @param result        解析结果
     * @param insertSqlNode insert node
     * @param parserContext context
     * @throws SqlParseException 解析异常时
     */
    public static void parseInsert(final LineageParseResult result, final SqlNode insertSqlNode, final ParserContext parserContext) throws SqlParseException {
        final FrameworkConfig frameworkConfig = parserContext.getFrameworkConfig();
        final CalciteCatalogReader calciteCatalogReader = parserContext.createCatalogReader();
        SqlValidator validator = SqlValidatorUtil.newValidator(
                frameworkConfig.getOperatorTable(),
                calciteCatalogReader, calciteCatalogReader.getTypeFactory(),
                frameworkConfig.getSqlValidatorConfig());
        SqlInsert validateInsertNode = (SqlInsert) validator.validate(insertSqlNode);
        final RelOptCluster cluster = parserContext.createCluster();
        // 创建 SqlToRelConverter
        final SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(new DogView(),
                validator, calciteCatalogReader, cluster, StandardConvertletTable.INSTANCE, frameworkConfig.getSqlToRelConverterConfig());
        // convert to RelNode
        RelRoot root = sqlToRelConverter.convertQuery(validateInsertNode, false, true);
        RelNode insertRelNode = root.rel;
        final RelOptTable table = insertRelNode.getTable();
        // 设置目的表
        final UniqueCodeForTableDto targetTable = Convertor.createUniqueCodeForTableDto(
                parserContext.getCatalogContext().getDatasourceInfo(), table.getQualifiedName().get(1), table.getQualifiedName().get(2));
        // 设置目的字段
        final List<UniqueCodeForColumnDto> targetColumns = Lists.newArrayList();
        // INSERT 指定了列
        if (CollectionUtils.isNotEmpty(validateInsertNode.getTargetColumnList())) {
            validateInsertNode.getTargetColumnList().forEach(sqlNode -> {
                SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                final UniqueCodeForColumnDto uniqueCodeForColumnDto =
                        Convertor.createUniqueCodeForColumnDto(parserContext.getCatalogContext().getDatasourceInfo(),
                                targetTable.getSchemaName(), targetTable.getTableName(), sqlIdentifier.names.get(0));
                targetColumns.add(uniqueCodeForColumnDto);
            });
        } else {
            table.getRowType().getFieldList().forEach(relDataTypeField -> {
                final UniqueCodeForColumnDto uniqueCodeForColumnDto =
                        Convertor.createUniqueCodeForColumnDto(parserContext.getCatalogContext().getDatasourceInfo(),
                                targetTable.getSchemaName(), targetTable.getTableName(), relDataTypeField.getName());
                targetColumns.add(uniqueCodeForColumnDto);
            });
        }

        // 查询sqlNode，获取来源
        final SqlNode selectNode = validateInsertNode.getSource();
        if (selectNode.getKind().equals(SqlKind.SELECT) || selectNode.getKind().equals(SqlKind.ORDER_BY)) {
            final SelectParseResult selectParseResult = parseSelect(selectNode, parserContext);
            buildLineageResult(result, targetTable, targetColumns, selectParseResult, parserContext);
        }
    }

    private static void buildLineageResult(final LineageParseResult lineageParseResult,
                                           final UniqueCodeForTableDto targetTable,
                                           final List<UniqueCodeForColumnDto> targetColumns,
                                           final SelectParseResult selectParseResult,
                                           final ParserContext parserContext) {
        final Set<String> addedTableSet = Sets.newHashSet();
        lineageParseResult.setColumnLineageInfos(Lists.newArrayList());
        lineageParseResult.setTableLineageInfos(Lists.newArrayList());

        for (int i = 0; i < targetColumns.size(); i++) {
            final FieldParseResult fieldParseResult = selectParseResult.getFieldParseResults().get(i);
            final UniqueCodeForColumnDto targetColumn = targetColumns.get(i);
            fieldParseResult.getOriginIds().forEach(fieldRecord -> {
                // 常量直接返回，如SELECT a,b,now()，
                if (Objects.isNull(fieldRecord.getFromDataBase()) || Objects.isNull(fieldRecord.getFromTableName())) {
                    return;
                }
                // 添加表
                if (!addedTableSet.contains(fieldRecord.getFromDataBase() + "-" + fieldRecord.getFromTableName())) {
                    addedTableSet.add(fieldRecord.getFromDataBase() + "-" + fieldRecord.getFromTableName());
                    TableLineageInfo tableLineageInfo = new TableLineageInfo();
                    tableLineageInfo.setTargetTable(targetTable);
                    UniqueCodeForTableDto sourceTableDto = Convertor.createUniqueCodeForTableDto(
                            parserContext.getCatalogContext().getDatasourceInfo(), fieldRecord.getFromDataBase(), fieldRecord.getFromTableName());
                    tableLineageInfo.setSourceTable(sourceTableDto);
                    lineageParseResult.getTableLineageInfos().add(tableLineageInfo);
                }
                // 添加字段
                ColumnLineageInfo columnLineageInfo = new ColumnLineageInfo();
                columnLineageInfo.setTargetColumn(targetColumn);
                UniqueCodeForColumnDto sourceColumnDto =
                        Convertor.createUniqueCodeForColumnDto(parserContext.getCatalogContext().getDatasourceInfo(),
                                fieldRecord.getFromDataBase(), fieldRecord.getFromTableName(), fieldRecord.getColumnEnName());
                sourceColumnDto.setDataSourceDto(Convertor.toDataSourceDto(parserContext.getCatalogContext().getDatasourceInfo()));
                columnLineageInfo.setSourceColumn(sourceColumnDto);
                lineageParseResult.getColumnLineageInfos().add(columnLineageInfo);
            });
        }
    }

    /**
     * 解析sql
     *
     * @param sqlNode sqlNode
     * @return 解析结果
     * @throws SqlParseException 解析异常时
     * @throws BusinessException 业务异常
     * @throws SqlParseException
     */
    public static SelectParseResult parseSelect(final SqlNode sqlNode, final ParserContext parserContext) throws BusinessException, SqlParseException {
        final SelectParseResult selectParseResult = new SelectParseResult();
        final FrameworkConfig frameworkConfig = parserContext.getFrameworkConfig();
        //note: step2 sql validate（会先通过Catalog读取获取相应的metadata和namespace）
        //note: 校验（包括对表名，字段名，函数名，字段类型的校验。）
        final CalciteCatalogReader calciteCatalogReader = parserContext.createCatalogReader();
        SqlValidator validator = SqlValidatorUtil.newValidator(
                frameworkConfig.getOperatorTable(),
                calciteCatalogReader, calciteCatalogReader.getTypeFactory(),
                frameworkConfig.getSqlValidatorConfig());
        SqlNode validateSqlNode = validator.validate(sqlNode);

        // step3 relnode/rexnode
        final RelOptCluster cluster = parserContext.createCluster();
        // 创建 SqlToRelConverter
        final SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(new DogView(),
                validator, calciteCatalogReader, cluster, StandardConvertletTable.INSTANCE, frameworkConfig.getSqlToRelConverterConfig());
        // convert to RelNode
        RelRoot root = sqlToRelConverter.convertQuery(validateSqlNode, false, true);
        RelNode relNode = root.rel;

        final RelMetadataQuery metadataQuery = sqlToRelConverter.getCluster().getMetadataQuery();
        // 构造返回值
        buildParseResult(selectParseResult, relNode, metadataQuery, relNode.getRowType().getFieldList());
        return selectParseResult;
    }

    private static void buildParseResult(SelectParseResult selectParseResult, RelNode relNode, RelMetadataQuery metadataQuery, List<RelDataTypeField> fieldList) {
        for (int i = 0; i < fieldList.size(); i++) {
            Set<RelColumnOrigin> columnOrigins = metadataQuery.getColumnOrigins(relNode, i);
            RelDataTypeField relDataTypeField = fieldList.get(i);
            FieldParseResult fieldParseResult = Convertor.fromRelColumnOrigins(relDataTypeField.getKey(), columnOrigins);
            selectParseResult.getFieldParseResults().add(fieldParseResult);
        }
    }

    private static class DogView implements RelOptTable.ViewExpander {
        public DogView() {
        }

        @Override
        public RelRoot expandView(RelDataType rowType, String queryString, List<String> schemaPath,
                                  List<String> viewPath) {
            return null;
        }
    }
}