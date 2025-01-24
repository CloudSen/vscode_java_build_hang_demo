package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;


import com.alibaba.druid.sql.parser.Token;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * sql 执行结果
 *
 * @author xxx
 */
@Data
public class QueryResult {

    /**
     * 执行结果，true：成功
     */
    private Boolean success = true;

    /**
     * 元数据
     */
    private List<ColumnInfo> metas = Lists.newArrayList();

    /**
     * 查询结果
     * 每一行的列以<列名, 值>的格式组成Map, 多行组成List
     */
    private List<List<Object>> rowList = Lists.newArrayList();

    /**
     * 执行信息
     */
    private List<ExecuteLog> executeInfo = Lists.newArrayList();

    /**
     * sql类型
     */
    private Token sqlType;

    /**
     * 非查询语句的执行结果
     */
    public static QueryResult newNonQuerySqlResult(Token sqlType,String enableSqlToken ) {
        QueryResult queryResult = new QueryResult();
        queryResult.setSuccess(false);
        queryResult.setSqlType(sqlType);
        queryResult.getExecuteInfo().add(ExecuteLog.newErrorLog(String.format("不允许执行非查询语句: %s, 已忽略.",enableSqlToken)));
        return queryResult;
    }

    /**
     * drop database的执行结果
     */
    public static QueryResult newDropDatabaseSqlResult(Token sqlType,String disableSqlToken) {
        QueryResult queryResult = new QueryResult();
        queryResult.setSuccess(false);
        queryResult.setSqlType(sqlType);
        queryResult.getExecuteInfo().add(ExecuteLog.newErrorLog(String.format("不允许执行%s语句, 已忽略.",disableSqlToken)));
        return queryResult;
    }
}
