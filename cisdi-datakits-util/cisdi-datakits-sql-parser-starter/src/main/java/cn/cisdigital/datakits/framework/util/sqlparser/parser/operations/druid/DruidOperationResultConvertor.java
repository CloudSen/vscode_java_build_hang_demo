package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationResultParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.OperationParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author xxx
 * @since 2024/4/28 8:43
 */
@Slf4j
@Service
public class DruidOperationResultConvertor implements IOperationResultParse {

    @Override
    public OperationParseResult databaseOperationObjectParse(final ParseSqlParam param) {
        final OperationParseResult result = new OperationParseResult();
        SQLStatementParser sqlStatementParser;
        ParseOperationEnum operationTypeEnum;
        SQLStatement sqlStatement;
        try {
            sqlStatementParser = createSQLStatementParserByDBType(param.getDataSourceDto().getDataBaseTypeEnum(), param.getSql());
            operationTypeEnum = convertOperation(sqlStatementParser);
            sqlStatement = sqlStatementParser.parseStatement();
        } catch (Exception e) {
            log.error("druid parse sql {} error ", param.getSql(), e);
            throw new RuntimeException("druid parse sql error");
        }
        result.setDatabaseOperations(createDatabaseOperation(operationTypeEnum, sqlStatement, param));
        return result;

    }

    @Override
    public ParseOperationEnum checkSqlOperation(final ParseSqlParam param) {
        try {
            final SQLStatementParser sqlStatementParser = createSQLStatementParserByDBType(param.getDataSourceDto().getDataBaseTypeEnum(), param.getSql());
            return convertOperation(sqlStatementParser);
        } catch (Exception e) {
            log.error("druid parse sql {} error ", param.getSql(), e);
            throw new RuntimeException("druid parse sql error");
        }
    }

    /**
     * 根据不同操作类型，构造解析结果
     *
     * @param operationTypeEnum 操作类型
     * @param sqlStatement      druid statement
     * @param param             param
     * @return operation
     */
    private Set<DatabaseOperation> createDatabaseOperation(ParseOperationEnum operationTypeEnum, SQLStatement sqlStatement, ParseSqlParam param) {

        switch (operationTypeEnum) {
            case CREATE:
                return Collections.singleton(new DruidCreateTypeParser(sqlStatement).databaseOperationParse(param));
            case ALTER:
                return Collections.singleton(new DruidAlterTypeParser(sqlStatement).databaseOperationParse(param));
            case DROP:
            case TRUNCATE:
                return Collections.singleton(new DruidDropTypeParser(sqlStatement).databaseOperationParse(param));
            case UPDATE:
                return Collections.singleton(new DruidUpdateTypeParser(sqlStatement).databaseOperationParse(param));
            case DELETE:
                return Collections.singleton(new DruidDeleteTypeParser(sqlStatement).databaseOperationParse(param));
            case USE:
                return Collections.singleton(new DruidUseTypeParser(sqlStatement).databaseOperationParse(param));
            case SELECT:
            case INSERT: {
                DatabaseOperation databaseOperation = new DatabaseOperation();
                databaseOperation.setParseOperationEnum(operationTypeEnum);
                databaseOperation.setExplainEnable(true);
                return Collections.singleton(databaseOperation);
            }
            default: {
                log.warn("unsupported sql: {}", param.getSql());
                throw new UnsupportedOperationException("unsupported sql:" + param.getSql());
            }
        }
    }

    /**
     * 根据数据库类型创建SQLStatementParser
     *
     * @param dataBaseTypeEnum dbType
     * @param sql              sql
     * @return SQLStatementParser
     */
    private SQLStatementParser createSQLStatementParserByDBType(DataBaseTypeEnum dataBaseTypeEnum, String sql) {
        if (Objects.requireNonNull(dataBaseTypeEnum) == DataBaseTypeEnum.DORIS) {
            return new MySqlStatementParser(sql);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * 转换druid token为SDK操作类型
     *
     * @param sqlStatementParser druid parser
     * @return operation type
     */
    private ParseOperationEnum convertOperation(SQLStatementParser sqlStatementParser) {
        switch (sqlStatementParser.getLexer().token()) {
            case SELECT:
                return ParseOperationEnum.SELECT;
            case USE:
                return ParseOperationEnum.USE;
            case SHOW:
                return ParseOperationEnum.SHOW;
            case UPDATE:
                return ParseOperationEnum.UPDATE;
            case DELETE:
                return ParseOperationEnum.DELETE;
            case INSERT:
                return ParseOperationEnum.INSERT;
            case TRUNCATE:
                return ParseOperationEnum.TRUNCATE;
            case CREATE:
                return ParseOperationEnum.CREATE;
            case DROP:
                return ParseOperationEnum.DROP;
            case ALTER:
                return ParseOperationEnum.ALTER;
            default:
                return ParseOperationEnum.OTHERS;
        }
    }
}
