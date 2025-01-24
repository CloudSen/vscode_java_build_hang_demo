package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;


import cn.cisdigital.datakits.framework.dynamic.datasource.exception.SqlExecuteException;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 基于druid的简单sql解析器
 *
 * @author xxx
 */
@Slf4j
@Component
public class DruidSimpleSqlParser {

    /**
     * 解析sql语句的token
     *
     * @param sql sql语句
     * @return token
     */
    public Token parseFirstToken(String sql){
        SQLStatementParser parser = getParser(sql);
        return parser.getLexer().token();
    }

    /**
     * 解析sql语句的词法解析器
     *
     * @param sql sql语句
     * @return token
     */
    public Lexer getLexer(String sql){
        SQLStatementParser parser = getParser(sql);
        return parser.getLexer();
    }

    /**
     * 获取statement parser
     *
     * @param sql sql
     * @return parser
     */
    public SQLStatementParser getParser(String sql) {
        try {
            return new MySqlStatementParser(sql);
        } catch (Exception e) {
            log.error("解析异常:",e);
            throw new SqlExecuteException("解析异常: " + e.getMessage());
        }
    }

    /**
     * 按照mysql的语法解析sql, 并处理sql中未指定limit或指定的limit超过最大值的情况
     * 支持分页查询
     *
     * @param sql 待处理的sql, 可以是多条
     * @param maxRowCount limit的最大条数
     * @return 返回处理limit并格式化的sql, 如果是传入的是多条sql则也返回的也是多条已格式化并通过换行分隔的sql
     */
    public static String modifyOrAppendLimit(String sql, int maxRowCount) {
        return modifyOrAppendLimit(sql, maxRowCount, JdbcConstants.MYSQL);
    }

    /**
     * 按照指定的数据库类型语法解析sql, 并处理sql未指定limit或指定的limit超过最大值的情况
     * 支持分页查询
     *
     * @param sql 待处理的sql, 可以是多条
     * @param maxRowCount limit的最大条数
     * @param dbType 数据库类型, 目前只测试了doris(兼容mysql的语法)
     * @return 返回处理limit并格式化的sql, 如果是传入的是多条sql则也返回的也是多条已格式化并通过换行分隔的sql
     */
    public static String modifyOrAppendLimit(String sql, int maxRowCount, DbType dbType) {
        String dbTypeName = dbType.name();
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbTypeName);

        if (CollectionUtils.isEmpty(statementList)) {
            throw new IllegalStateException("[sql执行] [modifyOrAppendLimit] sql解析失败, 检查sql语法是否正确. sql=" + sql);
        }

        for (SQLStatement sqlStatement : statementList) {
            sqlStatement.accept(new MySqlASTVisitorAdapter(){

                @Override
                public boolean visit(SQLLimit x) {
                    SQLExpr rowCountExpr = x.getRowCount();
                    Number rowCount = ((SQLIntegerExpr) rowCountExpr).getNumber();
                    if (rowCount.intValue() > maxRowCount) {
                        // 原始limit大小超过默认值, 进行修改
                        x.setRowCount(maxRowCount);
                    }
                    return super.visit(x);
                }

                @Override
                public boolean visit(SQLUnionQuery x) {
                    for (int i = 0; i < x.getRelations().size(); i++) {
                        if (x.getRelations().get(i) instanceof SQLSelectQueryBlock) {
                            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x.getRelations().get(i);
                            SQLLimit limit = queryBlock.getLimit();
                            if (Objects.isNull(limit)) {
                                queryBlock.setLimit(new SQLLimit(maxRowCount));
                            } else {
                                SQLExpr rowCountExpr = limit.getRowCount();
                                Number rowCount = ((SQLIntegerExpr) rowCountExpr).getNumber();
                                if (rowCount.intValue() > maxRowCount) {
                                    // 原始limit大小超过默认值, 进行修改
                                    limit.setRowCount(maxRowCount);
                                }
                            }
                            // right union添加上括号
                            if (i != 0) {
                                queryBlock.setParenthesized(true);
                            }
                        }
                    }
                    return super.visit(x);
                }

                @Override
                public void endVisit(SQLSelectStatement x) {
                    // 语句未指定limit大小, 需要追加
                    SQLSelectQueryBlock queryBlock = x.getSelect().getQueryBlock();
                    if (Objects.nonNull(queryBlock)) {
                        SQLLimit limit = queryBlock.getLimit();
                        if (Objects.isNull(limit)) {
                            x.getSelect().setLimit(new SQLLimit(maxRowCount));
                        }
                    }
                    super.endVisit(x);
                }
            });
        }

        StringBuilder builder = new StringBuilder();

        statementList.stream().map(SQLStatement::toString).forEach(s -> builder.append(s).append(";\n"));
        return builder.toString().replace(";;", ";");
    }
}
