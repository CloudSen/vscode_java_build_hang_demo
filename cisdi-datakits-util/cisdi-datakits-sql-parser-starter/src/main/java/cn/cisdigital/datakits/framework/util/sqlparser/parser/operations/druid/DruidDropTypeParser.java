package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.DROP;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidDropTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean dropTable = sqlStatement instanceof SQLDropTableStatement;
        boolean dropView = sqlStatement instanceof SQLDropViewStatement;
        boolean truncateTable = sqlStatement instanceof SQLTruncateStatement;
        Preconditions.checkArgument((dropTable || dropView || truncateTable), "unsupported drop type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());

        if (dropTable) {
            final SQLDropTableStatement dropTableStatement = (SQLDropTableStatement) sqlStatement;
            final List<SQLExprTableSource> tableSources = dropTableStatement.getTableSources();
            databaseOperation.setDbName(StringUtils.isEmpty(tableSources.get(0).getSchema()) ? param.getDataSourceDto().getSchemaName() : tableSources.get(0).getSchema());
            databaseOperation.setTableName(tableSources.get(0).getTableName());
        } else if (dropView) {
            final SQLDropViewStatement dropViewStatement = (SQLDropViewStatement) sqlStatement;
            final List<SQLExprTableSource> tableSources = dropViewStatement.getTableSources();
            databaseOperation.setDbName(StringUtils.isEmpty(tableSources.get(0).getSchema()) ? param.getDataSourceDto().getSchemaName() : tableSources.get(0).getSchema());
            databaseOperation.setTableName(tableSources.get(0).getTableName());
        } else if (truncateTable) {
            final SQLTruncateStatement truncateStatement = (SQLTruncateStatement) sqlStatement;
            final List<SQLExprTableSource> tableSources = truncateStatement.getTableSources();
            databaseOperation.setDbName(StringUtils.isEmpty(tableSources.get(0).getSchema()) ? param.getDataSourceDto().getSchemaName() : tableSources.get(0).getSchema());
            databaseOperation.setTableName(tableSources.get(0).getTableName());
        }
        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return DROP;
    }
}
