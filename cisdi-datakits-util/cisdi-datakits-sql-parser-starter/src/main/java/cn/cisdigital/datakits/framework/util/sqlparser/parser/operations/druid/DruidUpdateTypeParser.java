package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.CREATE;
import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.UPDATE;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidUpdateTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean updateTable = sqlStatement instanceof SQLUpdateStatement;
        Preconditions.checkArgument((updateTable), "unsupported update type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());

        final SQLUpdateStatement sqlUpdateStatement = (SQLUpdateStatement) sqlStatement;
        final SQLExprTableSource tableSource = (SQLExprTableSource) sqlUpdateStatement.getTableSource();

        databaseOperation.setDbName(StringUtils.isEmpty(tableSource.getSchema()) ? param.getDataSourceDto().getSchemaName() :
                tableSource.getSchema());
        databaseOperation.setTableName(tableSource.getTableName());

        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return UPDATE;
    }
}
