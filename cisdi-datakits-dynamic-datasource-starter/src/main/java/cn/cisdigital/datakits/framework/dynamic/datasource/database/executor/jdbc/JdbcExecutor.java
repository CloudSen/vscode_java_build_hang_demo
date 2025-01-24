package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;

import cn.cisdigital.datakits.framework.dynamic.datasource.exception.DbExceptionMsgTranslator;
import cn.cisdigital.datakits.framework.dynamic.datasource.exception.SqlExecuteException;
import cn.cisdigital.datakits.framework.dynamic.datasource.properties.DynamicDsProperties;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.dynamic.datasource.utils.AuthUtils;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.fastjson.JSON;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.mysql.cj.result.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * jdbc执行器
 * <p>
 * 只支持执行select/show/set语句, 并且结果最大条数限制1000
 * 该类相比于直接使用JdbcTemplate做了如下封装:
 * 1. select和set语句的校验
 * 2. sql注释的移除
 * 3. 结果字段的schema信息: 类型/自增/是否为空等
 * 4. 结果中包含细粒度的异常信息和执行日志
 *
 * @author xxx
 */
@Slf4j
@Component
public class JdbcExecutor {

    /**
     * 默认limit数量
     */
    @Value("${sql.config.limit.default:1000}")
    private int defaultLimits = 1000;

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private DruidSimpleSqlParser sqlParser;


    @Autowired
    DynamicDsProperties dynamicDsProperties;

    public QueryResult doQueryWithTimeout(DataSourceDto dataSourceDto, String sql, Integer timeoutSeconds) {
        return doQuery(Collections.emptyList(), dataSourceDto, sql, defaultLimits, timeoutSeconds);
    }

    public QueryResult doQueryWithLimit(DataSourceDto dataSourceDto, String sql, Integer limit) {
        return doQuery(Collections.emptyList(), dataSourceDto, sql, limit, null);
    }

    public QueryResult doQueryWithDefaultControl(DataSourceDto dataSourceDto, String sql) {
        return doQuery(Collections.emptyList(), dataSourceDto, sql, defaultLimits, null);
    }

    public QueryResult doQueryWithDefaultControl(List<String> setStatements, DataSourceDto dataSourceDto, String sql) {
        return doQuery(setStatements, dataSourceDto, sql, defaultLimits, null);
    }

    /**
     * 只支持执行select/show/desc语句
     */
    public QueryResult doQuery(List<String> setStatements, DataSourceDto dataSourceDto, String sql, Integer limit, Integer timeoutSeconds) {
        return doExecute(setStatements, dataSourceDto, sql, limit, timeoutSeconds, true);
    }

    /**
     * 支持执行查询语句及表级别的ddl语句
     *
     */
    public QueryResult doExecute(List<String> setStatements, DataSourceDto dataSourceDto, String sql) {
        return doExecute(setStatements, dataSourceDto, sql, defaultLimits, null, false);
    }

    /**
     * 支持执行查询语句及表级别的ddl语句
     *
     */
    public QueryResult doExecute(List<String> setStatements, DataSourceDto dataSourceDto, String sql, Integer timeoutSeconds) {
        return doExecute(setStatements, dataSourceDto, sql, defaultLimits, timeoutSeconds, false);
    }

    /**
     * 支持执行查询语句及表级别的ddl语句
     *
     * @param rejectExecuteNonQuerySql 是否拒绝执行非查询sql
     */
    public QueryResult doExecute(List<String> setStatements, DataSourceDto dataSourceDto, String sql, Integer limit, Integer timeoutSeconds, boolean rejectExecuteNonQuerySql) {
        // 移除sql注释功能
        sql = removeSqlComments(removeMultiLineComments(sql));
        log.info("[sql执行] 预期执行的sql={}", sql);

        // 判断是否为非查询语句
        Lexer sqlLexer = sqlParser.getLexer(sql);
        Token sqlTypeToken = sqlLexer.token();
        //List<Token> querySqlToken = Arrays.asList(Token.SELECT, Token.SHOW, Token.DESC,Token.WITH,Token.EXPLAIN);
        // 非查询语句且不允许执行
        boolean nonQuerySql = !dynamicDsProperties.getEnableSqlToken().contains(sqlTypeToken);
        if (nonQuerySql && rejectExecuteNonQuerySql) {
            String enableSqlToken = StringUtils.join(dynamicDsProperties.getEnableSqlToken(), "/");
            log.warn("[sql执行] 拒绝执行非{}语句, 返回默认的空结果. sql={}", enableSqlToken, sql);
            return QueryResult.newNonQuerySqlResult(sqlTypeToken,enableSqlToken);
        } else if (dynamicDsProperties.getDisableSqlToken().contains(sqlTypeToken)) {
            // 判断是否为不允许执行的drop database语句
            while (sqlTypeToken != Token.EOF) {
                if (sqlTypeToken == Token.DATABASE) {
                    String disableSqlToken = StringUtils.join(dynamicDsProperties.getDisableSqlToken(), "/");
                    log.warn("[sql执行] 拒绝执行{}语句, 返回默认的空结果. sql={}", disableSqlToken, sql);
                    return QueryResult.newDropDatabaseSqlResult(sqlTypeToken,disableSqlToken);
                }
                sqlLexer.nextTokenValue();
                sqlTypeToken = sqlLexer.token();
            }
        }

        QueryResult queryResult = new QueryResult();
        long startTime = System.currentTimeMillis();
        queryResult.setSqlType(sqlTypeToken);

        List<ExecuteLog> executeInfo = queryResult.getExecuteInfo();
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement()) {

            if (timeoutSeconds != null) {
                statement.setQueryTimeout(timeoutSeconds);
            }else{
                timeoutSeconds = statement.getQueryTimeout();
            }

            // 添加set语句，批量执行
            executeSetStatement(queryResult, setStatements, statement);
            if (Boolean.FALSE.equals(queryResult.getSuccess())) {
                return queryResult;
            }

            if (nonQuerySql) {
                doNonQuerySql(sql, statement, startTime, queryResult);
            } else {
                doQuerySql(sql, statement, limit, startTime, queryResult);
            }

            double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000D;
            log.info("[sql执行] 最终执行的sql={}, 耗时={}秒", sql, elapsedSeconds);
        } catch (SQLTimeoutException e) {
            log.error("[sql执行] sql执行超时, 当前超时时间(0即没有限制)=[{}], sql=[{}]", e.getClass().getSimpleName(), timeoutSeconds, e);
            executeInfo.add(ExecuteLog.newErrorLog(e.getMessage()));
            queryResult.setSuccess(false);
        } catch (SQLException e) {
            log.error("[sql执行] sql执行报错, 异常类型=[{}], sql=[{}]", e.getClass().getSimpleName(), sql, e);
            String translatedMsg = DbExceptionMsgTranslator.getMsg(dataSourceDto.getDataBaseTypeEnum(), String.valueOf(e.getErrorCode()));
            executeInfo.add(ExecuteLog.newErrorLog(translatedMsg + " " + e.getMessage()));
            DbExceptionMsgTranslator.getRemoteMsgIfExists(e.getMessage(), restTemplate, AuthUtils.basicAuthHeader(dataSourceDto.getUsername(), dataSourceDto.getPassword()))
                .ifPresent(msg -> executeInfo.add(ExecuteLog.newErrorLog(msg)));
            queryResult.setSuccess(false);
        } catch (Exception e) {
            log.error("[sql执行] 未知错误, 异常类型=[{}], sql=[{}]", e.getClass().getSimpleName(), sql, e);
            executeInfo.add(ExecuteLog.newErrorLog(e.getMessage()));
            queryResult.setSuccess(false);
        }

        return queryResult;
    }

    private void executeSetStatement(QueryResult queryResult, List<String> setStatements, Statement statement) throws SQLException {
        if (CollectionUtils.isNotEmpty(setStatements)) {
            try {
                checkSetStatements(setStatements);

                for (String setSql : setStatements) {
                    statement.addBatch(setSql);
                }

                long setStatementStartTime = System.currentTimeMillis();
                statement.executeBatch();
                log.info("[sql执行] 最终执行的set statement sql列表={}, 耗时={}ms",
                    JSON.toJSONString(setStatements), System.currentTimeMillis() - setStatementStartTime);
            } catch (Exception e) {
                log.error("[sql执行] set statement sql执行报错, 跳过后面的sql执行. 异常类型=[{}], set statement sql=[{}]",
                    e.getClass().getSimpleName(), setStatements, e);
                queryResult.getExecuteInfo().add(ExecuteLog.newErrorLog(e.getMessage()));
                queryResult.setSuccess(false);
            }
        }
    }

    private void doQuerySql(String sql, Statement statement, Integer limit, long startTime, QueryResult queryResult) throws SQLException {
        // 设置返回条数限制
        limit = Optional.ofNullable(limit).orElse(defaultLimits);
        if (limit > defaultLimits) {
            limit = defaultLimits;
        }

        sql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, limit, DbType.mysql);

        // 执行查询语句
        try (ResultSet resultSet = statement.executeQuery(sql)) {
            double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000D;

            ResultSetMetaData metaData = null;
            try {
                metaData = (ResultSetMetaData)resultSet.getMetaData();
            }catch (NullPointerException e){
                log.warn(String.format("JDBC获取结果数据空指针异常,执行sql: %s,异常信息: %s",sql,e.getMessage()));
            }

            //现在语句有没有返回任何结果的操作，比如单独执行use 语句
            if(metaData != null){
                queryResult.getMetas().addAll(toColumnInfo(metaData.getFields()));
                int columnCount = queryResult.getMetas().size();
                while (resultSet.next()) {
                    List<Object> rowValues = new ArrayList<>();
                    for (int index = 0; index < columnCount; index++) {
                        Object data = resultSet.getObject(index + 1);
                        // sql date转 util.date 不然序列化出去要异常
                        if (data instanceof Date) {
                            data = new java.util.Date(((Date) data).getTime());
                        }
                        rowValues.add(data);
                    }
                    queryResult.getRowList().add(rowValues);

                }
            }
            queryResult.getExecuteInfo().add(ExecuteLog.newInfoLog("查询到 " + queryResult.getRowList().size() + " 行, 耗时 " + elapsedSeconds + " 秒"));
        }
    }

    private static void doNonQuerySql(String sql, Statement statement, long startTime, QueryResult queryResult) throws SQLException {
        // 执行非查询语句
        boolean isResultSetObject = statement.execute(sql);
        double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000D;

        if (isResultSetObject) {
            ResultSet resultSet = statement.getResultSet();
            throw new IllegalStateException("非查询语句不应该会有ResultSet对象, 需要确认为什么会出现这种情况. resultSet=" + JSON.toJSONString(resultSet));
        } else {
            int updateCount = statement.getUpdateCount();
            if (updateCount == -1) {
                queryResult.getExecuteInfo().add(ExecuteLog.newInfoLog("耗时 " + elapsedSeconds + " 秒"));
            } else {
                queryResult.getExecuteInfo().add(ExecuteLog.newInfoLog("影响 " + updateCount + " 行, 耗时 " + elapsedSeconds + " 秒"));
            }
        }
    }

    private Collection<? extends ColumnInfo> toColumnInfo(Field[] fields) {
        if (fields == null || fields.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(fields).map(ColumnInfo::of).collect(Collectors.toList());
    }

    private static final Pattern MULTIL_LINE_COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /**
     * 通过正则移除多行模板注释
     *
     * @param sql 原始SQL
     */
    public String removeMultiLineComments(String sql) {
        // 使用Pattern和Matcher进行全局匹配和替换
        Matcher matcher = MULTIL_LINE_COMMENT_PATTERN.matcher(sql);
        return matcher.replaceAll("");
    }

    /**
     * 根据正则移除 每行SQL的注释
     *
     * @param sql 原始SQL
     */
    public String removeSqlComments(String sql) {
        return Arrays.stream(StringUtils.split(sql, "\n"))
            .map(line -> line.replaceAll("--.*", ""))
            .collect(Collectors.joining("\n"));
    }

    /**
     * 检查setStatements中是否存在非set语句和set中包含global的语句
     *
     * @param sqlList sqlList
     */
    private void checkSetStatements(List<String> sqlList) {
        sqlList.forEach(sql -> {
            Token token = sqlParser.parseFirstToken(sql);
            if (!Objects.equals(token, Token.SET) || sql.contains("global")) {
                throw new SqlExecuteException("拒绝运行 非set语句 或 包含global关键字的set语句: " + sql);
            }
        });
    }
}
