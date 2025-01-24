package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.ALTER;
import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.CREATE;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidAlterTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean alterTable = sqlStatement instanceof SQLAlterTableStatement;
        boolean alterView = sqlStatement instanceof SQLAlterViewStatement;
        Preconditions.checkArgument((alterTable || alterView), "unsupported alter type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());
        if (alterTable) {
            final SQLAlterTableStatement sqlAlterTableStatement = (SQLAlterTableStatement) sqlStatement;
            databaseOperation.setDbName(StringUtils.isEmpty(sqlAlterTableStatement.getSchema()) ? param.getDataSourceDto().getSchemaName() :
                    sqlAlterTableStatement.getSchema());
            databaseOperation.setTableName(sqlAlterTableStatement.getTableName());
        } else if (alterView) {
            final SQLAlterViewStatement sqlAlterViewStatement = (SQLAlterViewStatement) sqlStatement;
            databaseOperation.setDbName(StringUtils.isEmpty(sqlAlterViewStatement.getSchema()) ? param.getDataSourceDto().getSchemaName()
                    : sqlAlterViewStatement.getSchema());
            databaseOperation.setTableName(sqlAlterViewStatement.getTableSource().getTableName());
        }
        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return ALTER;
    }
}
