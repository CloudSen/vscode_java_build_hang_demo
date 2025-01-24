package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.DELETE;
import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.UPDATE;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidDeleteTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean deleteTable = sqlStatement instanceof SQLDeleteStatement;
        Preconditions.checkArgument((deleteTable), "unsupported delete type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());

        final SQLDeleteStatement sqlDeleteStatement = (SQLDeleteStatement) sqlStatement;
        final SQLExprTableSource exprTableSource = sqlDeleteStatement.getExprTableSource();

        databaseOperation.setDbName(StringUtils.isEmpty(exprTableSource.getSchema()) ? param.getDataSourceDto().getSchemaName() :
                exprTableSource.getSchema());
        databaseOperation.setTableName(exprTableSource.getTableName());

        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return DELETE;
    }
}
