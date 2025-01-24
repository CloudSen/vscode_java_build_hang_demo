package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.druid;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.operations.IOperationTypeParse;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.DELETE;
import static cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum.USE;

/**
 * @author xxx
 * @since 2024/4/28 10:13
 */
@AllArgsConstructor
public class DruidUseTypeParser implements IOperationTypeParse {
    private final SQLStatement sqlStatement;

    @Override
    public DatabaseOperation databaseOperationParse(final ParseSqlParam param) {
        boolean use = sqlStatement instanceof SQLUseStatement;
        Preconditions.checkArgument((use), "unsupported use type :" + param.getSql());
        final DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.setParseOperationEnum(getParseOperationEnum());

        final SQLUseStatement sqlUseStatement = (SQLUseStatement) sqlStatement;
        databaseOperation.setDbName(StringUtils.isEmpty(sqlUseStatement.getDatabase().getSimpleName()) ? param.getDataSourceDto().getSchemaName() :
                sqlUseStatement.getDatabase().getSimpleName());
        return databaseOperation;
    }

    @Override
    public ParseOperationEnum getParseOperationEnum() {
        return USE;
    }
}
