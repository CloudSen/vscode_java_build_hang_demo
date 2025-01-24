package cn.cisdigital.datakits.framework.util.sqlparser;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.CalciteSqlParser;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.ParserContext;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.ParserContextManager;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationResultParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.FieldParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.LineageParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.OperationParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SelectParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.util.Convertor;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * sql解析服务类
 *
 * @author xxx
 * @since 2024/4/18 9:52
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParseService implements ISqlParserService {

    private final ParserContextManager parserContextManager;
    private final IOperationResultParse operationResultParse;

    /**
     * 防止sql慢查询，导致数据库压力过大
     */
    private static final String PARSER_SQL_FORMAT = "SELECT * FROM ( %s ) SQL_TEMP WHERE 1 = 2";
    private static final String DEFAULT_DB_SQL_FORMAT = "USE `%s`";


    @Override
    public SelectParseResult parseSelect(final ParseSqlParam param, final boolean enableBackStrategy) throws RuntimeException {
        final ParserContext parserContext = checkAndGetContext(param);

        SqlNode sqlNode;
        // 判断sql类型,解析为语法树
        try {
            sqlNode = parseSqlNode(param.getSql(), parserContext);
        } catch (Exception e) {
            log.warn("parse sql node error sql[{}],datasource[{}]: ", param.getSql(), param.getDataSourceDto(), e);
            if (Objects.equals(enableBackStrategy, false)) {
                throw new RuntimeException(e.getMessage());
            }
            return parseOld(param.getSql(),  param.getDataSourceDto());
        }

        if (!Objects.equals(sqlNode.getKind(), SqlKind.SELECT) && !Objects.equals(sqlNode.getKind(), SqlKind.ORDER_BY)) {
            throw new RuntimeException("不支持非查询语句");
        }

        try {
            // 开始解析为关系树
            return CalciteSqlParser.parseSelect(sqlNode, parserContext);
        } catch (Exception e) {
            log.warn("parse rel node error sql[{}],datasource[{}]: ", param.getSql(), param.getDataSourceDto(), e);
            if (Objects.equals(enableBackStrategy, false)) {
                throw new RuntimeException(e.getMessage());
            }
            return parseOld(param.getSql(), param.getDataSourceDto());
        }
    }

    @Override
    public LineageParseResult parseLineage(final ParseSqlParam param) throws RuntimeException {
        final ParserContext parserContext = checkAndGetContext(param);
        final LineageParseResult result = new LineageParseResult();
        // 判断sql类型
        SqlNode sqlNode = parseSqlNode(param.getSql(), parserContext);
        if (!Objects.equals(sqlNode.getKind(), SqlKind.INSERT)) {
            return result;
        }
        try {
            // 开始解析
            CalciteSqlParser.parseInsert(result, sqlNode, parserContext);
        } catch (Exception e) {
            log.error("parse insert sql {} error : ", param.getSql(), e);
        }
        return result;
    }

    @Override
    public OperationParseResult parseOperation(final ParseSqlParam param) throws RuntimeException {
        return operationResultParse.databaseOperationObjectParse(param);
    }

    @Override
    public ParseOperationEnum checkSqlOperation(final ParseSqlParam param) {
        return operationResultParse.checkSqlOperation(param);
    }

    /**
     * 参数解析兜底策略
     *
     * @param sql           sql
     * @param dataSourceDto dataSourceDto
     * @return 解析结果
     */
    private SelectParseResult parseOld(String sql, DataSourceDto dataSourceDto) {
        SelectParseResult selectParseResult = new SelectParseResult();

        ResultSetMetaData metaData;
        // 执行sql，从metadata中获取字段类型，
        // 注：当语句中存在函数时，metadata无法获取到来源表，字段名，此时需要依赖sql解析来补充相关信息
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement()) {
            // 设置默认数据库
            if(StringUtils.isNotEmpty(dataSourceDto.getSchemaName())){
                statement.execute(String.format(DEFAULT_DB_SQL_FORMAT, dataSourceDto.getSchemaName()));
            }
            try(ResultSet rs = statement.executeQuery(String.format(PARSER_SQL_FORMAT, sql))){
                metaData = Optional.ofNullable(rs.getMetaData()).orElseThrow(() -> new RuntimeException("未获取到MetaData"));
                selectParseResult.setFieldParseResults(buildFieldRecordList(metaData));
            }
        } catch (SQLException sqlException) {
            log.error("execute select sql error", sqlException);
            throw new RuntimeException(sqlException.getMessage());
        }
        return selectParseResult;
    }

    /**
     * 取metadata结果设置到结果中
     *
     * @param metaData metaData
     * @return 解析结果
     */
    private List<FieldParseResult> buildFieldRecordList(ResultSetMetaData metaData) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        List<FieldParseResult> fieldRecordList = Lists.newArrayList();
        for (int i = 1; i <= columnCount; i++) {
            FieldParseResult fieldParseResult = new FieldParseResult();
            fieldParseResult.setQueryName(metaData.getColumnName(i));
            fieldParseResult.setOriginIds(Collections.emptyList());
            fieldRecordList.add(fieldParseResult);
        }
        return fieldRecordList;
    }

    /**
     * 检查参数，获取解析上下文
     *
     * @param param param
     * @return context
     */
    private ParserContext checkAndGetContext(final ParseSqlParam param) {
        Preconditions.checkNotNull(param.getDataSourceDto(), "datasource dto can not be null");
        Preconditions.checkNotNull(param.getSql(), "sql can not be null");
        final Optional<ParserContext> catalogParserContext = parserContextManager.getCatalogParserContext(Convertor.fromDataSourceDto(param.getDataSourceDto()));
        if (!catalogParserContext.isPresent()) {
            log.error("parser context not exists {}", param);
            throw new RuntimeException("parser context not exists");
        }
        return catalogParserContext.get();
    }

    /**
     * 解析sqlNode语法树
     *
     * @param sql           sql
     * @param parserContext 解析上下文
     * @return sqlNode
     */
    private SqlNode parseSqlNode(String sql, ParserContext parserContext) {
        try {
            return CalciteSqlParser.parseSqlNode(sql, parserContext);
        } catch (SqlParseException e) {
            log.error("parse sqlNode error {}", sql, e);
            throw new RuntimeException(e);
        }
    }
}
