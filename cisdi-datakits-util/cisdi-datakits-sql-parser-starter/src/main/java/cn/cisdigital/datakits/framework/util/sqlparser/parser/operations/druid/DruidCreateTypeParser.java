package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.CREATE;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidCreateTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean createTable = sqlStatement instanceof SQLCreateTableStatement;
        boolean createView = sqlStatement instanceof SQLCreateViewStatement;
        Preconditions.checkArgument((createTable || createView), "unsupported create type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());
        if (createTable) {
            final SQLCreateTableStatement sqlCreateTableStatement = (SQLCreateTableStatement) sqlStatement;
            databaseOperation.setDbName(StringUtils.isEmpty(sqlCreateTableStatement.getSchema()) ? param.getDataSourceDto().getSchemaName() : sqlCreateTableStatement.getSchema());
            databaseOperation.setTableName(sqlCreateTableStatement.getTableName());
        } else if (createView) {
            final SQLCreateViewStatement sqlCreateViewStatement = (SQLCreateViewStatement) sqlStatement;
            databaseOperation.setDbName(StringUtils.isEmpty(sqlCreateViewStatement.getSchema()) ? param.getDataSourceDto().getSchemaName() : sqlCreateViewStatement.getSchema());
            databaseOperation.setTableName(sqlCreateViewStatement.getTableSource().getTableName());
        }
        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return CREATE;
    }
}
