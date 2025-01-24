package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.ColumnBase;
import cn.cisdigital.datakits.framework.model.dto.database.IndexDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DatabaseTableDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableDto;
import cn.cisdigital.datakits.framework.model.enums.*;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.cisdigital.datakits.framework.util.sqlparser.javacc.ext.doris.ParseDorisIndex;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.beust.jcommander.internal.Lists;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.CharPool.SPACE;
import static cn.hutool.core.text.StrPool.COMMA;

/**
 * @author xxx
 */
@Slf4j
@Component
public class DorisTableInfoForCreateSqlParser implements DorisCreateSqlParser {

    private static final String SELECT_TABLE_SQL_TEMPLATE = "" +
            "SELECT " +
            "   table_schema, table_name, table_type, table_rows, data_length, create_time, update_time, " +
            "   table_comment " +
            "FROM " +
            "   information_schema.`tables` " +
            "WHERE" +
            "   table_schema = '%s' AND table_name IN ( %s ) ";
    private static final String SELECT_COLUMN_SQL_TEMPLATE = "" +
            "SELECT " +
            "   table_schema, table_name, column_name, is_nullable, data_type, numeric_precision, numeric_scale, " +
            "   column_key, column_comment, ordinal_position  " +
            "FROM " +
            "   information_schema.`columns` " +
            "WHERE " +
            "   table_schema = '%s' AND table_name IN ( %s )  " +
            "ORDER BY ordinal_position  ";
    /**
     * 表信息
     */
    private static final String COLUMNS_GROUP = "columns";
    private static final String ATTRIBUTES_GROUP = "attributes";
    private static final Pattern CREATE_TABLE_SQL_PATTERN = Pattern.compile(
            "(?i)\\s*CREATE\\s+TABLE\\s+`\\w*`\\s+\\(\n(?<" + COLUMNS_GROUP + ">[\\s\\S]*)\n\\)\\s" +
                    "(?<" + ATTRIBUTES_GROUP + ">ENGINE=\\w+\n[\\s\\S]*)");

    private static final String CHARACTER_TYPE = "character";
    private static final String CHARACTER_TYPE_QUALIFIED = "char(255)";

    @Override
    public List<CreateSqlParserDto> parseBatch(DataSourceDto dataSource, List<CreateSqlParserDto> parserInfo) {
        // 从 information_schema 读取信息，key：schemaName.tableName
        List<DatabaseTableDto> schemaTableList = listSchemaTable(parserInfo);
        Map<String, TableBaseInfo> tableInformationMap = getTableInformation(dataSource, schemaTableList);
        Map<String, List<ColumnBaseInfo>> columnInformationMap = getColumnInformation(dataSource, schemaTableList);

        parserInfo.forEach(info -> {
            DorisTableDto tableInfo = info.getTableInfo();
            String createSql = info.getSurplusSql();
            Matcher matcher = CREATE_TABLE_SQL_PATTERN.matcher(createSql);
            if (!matcher.matches()) {
                log.error("建表语句不合法，database: {}, sql: {}", tableInfo.getDatabaseName(), createSql);
                return;
            }
            // 完善表信息
            String fullTableName = this.getFullTableName(tableInfo);
            TableBaseInfo tableInformation = tableInformationMap.get(fullTableName);
            this.setTableInfo(tableInfo, tableInformation);
            // 完善字段信息，key：columnName
            final DorisParseColumnGroupResultDto dorisParseColumnGroupResultDto = parseColumnFromSql(matcher);
            Map<String, ColumnParseInfo> parserColumnMap = dorisParseColumnGroupResultDto.getParserColumnMap();
            List<ColumnBaseInfo> columnInformation = columnInformationMap.get(fullTableName);
            this.setColumnInfo(tableInfo, parserColumnMap, columnInformation);
            // 完善索引信息
            setIndexInfoWithJavacc(tableInfo, dorisParseColumnGroupResultDto.getIndexStrList(),dataSource);
            // 其他属性
            String attributes = matcher.group(ATTRIBUTES_GROUP);
            info.setSurplusSql(attributes);
        });
        return parserInfo;
    }

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        // 建表语句单个解析性能太差
        throw new BusinessException("请勿调用表信息的单个解析，性能太差");
    }

    private List<DatabaseTableDto> listSchemaTable(List<CreateSqlParserDto> parserInfo) {
        return CollStreamUtil.toList(parserInfo, info -> {
            DorisTableDto tableInfo = info.getTableInfo();
            return new DatabaseTableDto(tableInfo.getDatabaseName(), tableInfo.getTableName());
        });
    }

    private void setTableInfo(DorisTableDto tableInfo, TableBaseInfo tableInformation) {
        if (tableInformation != null) {
            tableInfo.setTableType(MaterializeTypeEnum.parse(tableInformation.getTableType()));
            tableInfo.setTableRows(tableInformation.getTableRows());
            tableInfo.setDataLength(tableInformation.getDataLength());
            tableInfo.setCreateTime(tableInformation.getCreateTime());
            tableInfo.setUpdateTime(tableInformation.getUpdateTime());
            tableInfo.setTableComment(tableInformation.getTableComment());
        }
    }

    private void setColumnInfo(DorisTableDto tableInfo, Map<String, ColumnParseInfo> parserColumnMap,
            List<ColumnBaseInfo> columnInformation) {
        List<ColumnBase> columnList = Optional.ofNullable(columnInformation).orElse(new ArrayList<>()).stream()
                .sorted(Comparator.comparingLong(ColumnBaseInfo::getOrdinalPosition))
                .map(column -> {
                    ColumnBase targetColumn = new ColumnBase();
                    // 从information_schema中读取的信息
                    setColumnInfoByInformation(tableInfo, column, targetColumn);
                    // 从SQL中解析的信息
                    setColumnInfoByParser(parserColumnMap, column, targetColumn);
                    return targetColumn;
                }).collect(Collectors.toList());
        tableInfo.setColumnList(columnList);
    }

    private void setColumnInfoByInformation(DorisTableDto table, ColumnBaseInfo columnInfo, ColumnBase targetColumn) {
        String columnName = columnInfo.getColumnName();
        targetColumn.setColumnName(columnName);

        String columnKey = columnInfo.getColumnKey();
        if (StringUtils.hasText(columnKey)) {
            targetColumn.setPrimaryKey(true);
            DorisDataModelEnum tableModel = DorisDataModelEnum.parseByColumnKeyCode(columnKey);
            DorisTableConfigDto tableConfig = table.getTableConfig();
            tableConfig.setDataModel(tableModel);
        }

        targetColumn.setRequired(!BooleanUtil.toBoolean(columnInfo.getIsNullable()));
        targetColumn.setComment(columnInfo.getColumnComment());
    }

    private void setColumnInfoByParser(Map<String, ColumnParseInfo> parserColumnMap, ColumnBaseInfo columnInfo,
                                       ColumnBase targetColumn) {
        ColumnParseInfo parseInfo = parserColumnMap.get(targetColumn.getColumnName());
        if (parseInfo != null) {
            targetColumn.setAggregateType(parseInfo.getAggregate());
            String columnTypeDef = parseInfo.getColumnTypeDefinition();
            if (StringUtils.hasText(columnTypeDef)) {
                // 处理特殊情况
                if (CHARACTER_TYPE.equals(columnTypeDef)) {
                    targetColumn.setColumnType(DorisColumnEnum.DORIS_CHAR);
                    targetColumn.setPrecision(CHAR_MAX_LENGTH);
                    targetColumn.setColumnTypeQualified(CHARACTER_TYPE_QUALIFIED);
                } else {
                    this.setColumnTypeAndLength(columnInfo, columnTypeDef, targetColumn);
                }
            }
        }
    }

    private void setColumnTypeAndLength(ColumnBaseInfo columnInfo, String columnTypeDef, ColumnBase targetColumn) {
        String columnType = columnTypeDef;
        Long precision = null;
        Integer scale = null;
        // 是否包含符合类型，例如：ARRAY<T>
        int leftAngleBracketIndex = columnTypeDef.indexOf(LEFT_ANGLE_BRACKET);
        if (leftAngleBracketIndex != -1) {
            columnType = columnTypeDef.substring(0, leftAngleBracketIndex);
        } else {
            // 是否包含长度定义，例如：VARCHAR(T)
            int leftParenthesisIndex = columnTypeDef.indexOf(LEFT_PARENTHESIS);
            int rightParenthesisIndex = columnTypeDef.indexOf(RIGHT_PARENTHESIS);
            if (leftParenthesisIndex != -1 && rightParenthesisIndex != -1) {
                columnType = columnTypeDef.substring(0, leftParenthesisIndex);
                String lengthDef = columnTypeDef.substring(leftParenthesisIndex + 1, rightParenthesisIndex);
                if (ASTERISK.equals(lengthDef)) {
                    targetColumn.setPrecision(VARCHAR_MAX_LENGTH);
                } else {
                    List<String> lengthDefList = StrUtil.split(lengthDef, COMMA);
                    CollUtil.filter(lengthDefList, StrUtil::isNotBlank);
                    precision = Long.parseLong(lengthDefList.get(0).trim());
                    if (lengthDefList.size() > 1) {
                        scale = Integer.parseInt(lengthDefList.get(1).trim());
                    }
                    targetColumn.setPrecision(precision);
                    targetColumn.setScale(scale);
                }
            } else {
                // 针对建表时decimal类型未设置有效数和小数, 读取Doris赋予的默认有效数和小数
                if (DorisColumnEnum.DORIS_DECIMAL.getType().equalsIgnoreCase(columnTypeDef)) {
                    targetColumn.setPrecision(DORIS_DECIMAL_DEFAULT_PRECISION);
                    targetColumn.setScale(DORIS_DECIMAL_DEFAULT_SCALE);
                }
            }

        }
        DorisColumnEnum columnTypeEnum = Optional.ofNullable(DorisColumnEnum.parse(columnType))
                .orElse(DorisColumnEnum.parse(columnInfo.getDataType()));
        targetColumn.setColumnType(columnTypeEnum);
        targetColumn.setColumnTypeQualified(columnTypeDef);
    }

    private DorisParseColumnGroupResultDto parseColumnFromSql(Matcher matcher) {
        DorisParseColumnGroupResultDto result = new DorisParseColumnGroupResultDto();
        String columns = matcher.group(COLUMNS_GROUP);
        String[] columnArray = columns.split(COMMA + StrUtil.LF);
        List<String> indexStr = Lists.newArrayList();
        List<ColumnParseInfo> columnParseInfos = Lists.newArrayList();

        Arrays.stream(columnArray).forEach(column -> {
            if(Objects.equals(true, isIndexRow(column))) {
                indexStr.add(column);
                return;
            }
            String[] split = column.trim().replaceAll(COMMA + SPACE, COMMA).split(StrUtil.SPACE);
            ColumnParseInfo columnParseInfo = new ColumnParseInfo();
            columnParseInfo.setColumnName(DataBaseTypeEnum.DORIS.unwrappedEscapeCharacter(split[0]));
            columnParseInfo.setColumnTypeDefinition(split[1]);
            DorisAggregateEnum aggregate = DorisAggregateEnum.parse(split[2]);
            if (!DorisAggregateEnum.UNKNOWN.equals(aggregate)) {
                columnParseInfo.setAggregate(aggregate);
            }
            columnParseInfos.add(columnParseInfo);
        });
        result.setParserColumnMap(columnParseInfos.stream().collect(Collectors.toMap(ColumnParseInfo::getColumnName, Function.identity(), (v1, v2) -> v1)));
        result.setIndexStrList(indexStr);
        return result;
    }

    /**
     * 使用自定义javacc解析索引信息
     * <p>发生异常时，打印日志，查询元数据信息兜底</p>
     *
     * @param tableInfo  表信息
     * @param indexStr   索引字符串集合
     * @param dataSource 数据源信息
     */
    private void setIndexInfoWithJavacc(DorisTableDto tableInfo, List<String> indexStr, final DataSourceDto dataSource){
        if(CollectionUtils.isEmpty(indexStr)){
            return;
        }
        final String indexesStr = String.join(",", indexStr);
        try (StringReader input = new StringReader(indexesStr)) {
            ParseDorisIndex parseDorisIndex = new ParseDorisIndex(input);
            tableInfo.setIndexList(parseDorisIndex.parseIndexs());
        } catch (Exception e) {
            log.error("parse doris index [{}] error", indexesStr,e);
            setIndexInfoWithMetadata(dataSource,tableInfo);
        }
    }

    /**
     * 获取索引信息SQL模板
     */
    private static final String PARSER_INDEX_RESULT_FORMAT = "SHOW INDEX FROM %s.%s";

    /**
     * 从元数据获取索引信息
     * @param dataSource 数据源
     * @param tableInfo 表信息
     */
    private void setIndexInfoWithMetadata(final DataSourceDto dataSource, final DorisTableDto tableInfo)  {
        final String sql = String.format(PARSER_INDEX_RESULT_FORMAT,
                tableInfo.getDatabaseName(), tableInfo.getTableName());
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSource);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                IndexDto indexDto = new IndexDto();
                indexDto.setIndexName(rs.getString("Key_name"));
                indexDto.setIndexComment(rs.getString("Comment"));
                indexDto.setColumnNameLists(Collections.singletonList(rs.getString("Column_name")));
                indexDto.setIndexType(IndexTypeStrategyFactory.getIndexType(DataBaseTypeEnum.DORIS, rs.getString("Index_type")));
                indexDto.setProperties(null);
                tableInfo.getIndexList().add(indexDto);
            }
        } catch (SQLException e) {
            log.error("resource connect error", e);
        }
    }


    /**
     * 判断是否为索引行
     * @param row 正则匹配的一行字符串
     * @return true 索引行
     */
    private boolean isIndexRow(String row) {
        if (StringUtils.hasText(row)) {
            return row.trim().startsWith("INDEX");
        }
        return false;
    }


    private Map<String, TableBaseInfo> getTableInformation(DataSourceDto source, List<DatabaseTableDto> listDto) {
        List<TableBaseInfo> tableList = query(source, listDto, SELECT_TABLE_SQL_TEMPLATE, TableBaseInfo.class);
        return CollStreamUtil.toMap(tableList, this::getFullTableName, Function.identity());
    }

    private Map<String, List<ColumnBaseInfo>> getColumnInformation(DataSourceDto source,
                                                                   List<DatabaseTableDto> listDto) {
        List<ColumnBaseInfo> columnList = query(source, listDto, SELECT_COLUMN_SQL_TEMPLATE, ColumnBaseInfo.class);
        return CollStreamUtil.groupBy(columnList, this::getFullTableName, Collectors.toList());
    }

    private String getFullTableName(TableBaseInfo tableInfo) {
        return tableInfo.getTableSchema() + StrUtil.DOT + tableInfo.getTableName();
    }

    private String getFullTableName(ColumnBaseInfo columnInfo) {
        return columnInfo.getTableSchema() + StrUtil.DOT + columnInfo.getTableName();
    }

    private String getFullTableName(DorisTableDto dorisTableDto) {
        return dorisTableDto.getDatabaseName() + StrUtil.DOT + dorisTableDto.getTableName();
    }

    private <T> List<T> query(DataSourceDto source, List<DatabaseTableDto> listDto, String sqlTemplate,
                              Class<T> clazz) {
        Map<String, List<DatabaseTableDto>> schemaTableMap = CollStreamUtil.groupBy(listDto,
                DatabaseTableDto::getDatabaseName, Collectors.toList());
        return schemaTableMap.entrySet().stream().flatMap(entry -> {
            String schemaName = entry.getKey();
            String tableNames = entry.getValue().stream()
                    .map(dto -> CharUtil.SINGLE_QUOTE + dto.getTableName() + CharUtil.SINGLE_QUOTE)
                    .collect(Collectors.joining(COMMA));
            String sql = String.format(sqlTemplate, schemaName, tableNames);
            List<T> result = DynamicDataSourceHolder.queryForList(source, sql, clazz);
            return CollectionUtils.isEmpty(result) ? Stream.empty() : result.stream();
        }).collect(Collectors.toList());
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class TableBaseInfo implements Serializable {

        /**
         * schema名
         */
        String tableSchema;
        /**
         * 表名
         */
        String tableName;
        /**
         * 表类型：VIEW、BASE TABLE
         */
        String tableType;
        /**
         * 数据行数
         */
        long tableRows;
        /**
         * 数据长度，单位：B
         */
        long dataLength;
        /**
         * 创建时间
         */
        LocalDateTime createTime;
        /**
         * 修改时间
         */
        LocalDateTime updateTime;
        /**
         * 备注
         */
        String tableComment;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class ColumnBaseInfo implements Serializable {

        /**
         * schema名
         */
        String tableSchema;
        /**
         * 表名
         */
        String tableName;
        /**
         * 字段名
         */
        String columnName;
        /**
         * 是否为空
         */
        String isNullable;
        /**
         * 数据类型
         */
        String dataType;
        /**
         * 长度
         */
        Long numericPrecision;
        /**
         * 精度
         */
        Integer numericScale;
        /**
         * KEY类型：UNI、AGG、DUP
         */
        String columnKey;
        /**
         * 备注
         */
        String columnComment;
        /**
         * 字段顺序
         */
        long ordinalPosition;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class ColumnParseInfo implements Serializable {

        /**
         * 字段名
         */
        String columnName;
        /**
         * 字段聚合方式
         */
        DorisAggregateEnum aggregate;
        /**
         * 数据类型定义
         */
        String columnTypeDefinition;
    }

    /**
     * Doris逆向正则匹配{@link cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.DorisTableInfoForCreateSqlParser#COLUMNS_GROUP}
     * 解析结果
     * <p>
     * 包含字段结果和索引结果2部分
     * </p>
     *
     * @author xxx
     * @since 2024/9/4 11:44
     */
    @Data
    private static class DorisParseColumnGroupResultDto {
        /**
         * 字段信息
         */
        private Map<String, DorisTableInfoForCreateSqlParser.ColumnParseInfo> parserColumnMap;
        /**
         * 索引字符串,可能为空
         */
        private List<String> indexStrList;
    }
}
