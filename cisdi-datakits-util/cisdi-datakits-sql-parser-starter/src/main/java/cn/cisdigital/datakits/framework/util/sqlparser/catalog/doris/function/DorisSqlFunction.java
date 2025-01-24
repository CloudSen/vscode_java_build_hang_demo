package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.function;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;

public class DorisSqlFunction extends SqlFunction {

    private final boolean isDeterministic;
    private final boolean isDynamic;
    private final SqlSyntax syntax;

    public DorisSqlFunction(String name,
                            boolean isDeterministic,
                            boolean isDynamic,
                            SqlReturnTypeInference sqlReturnTypeInference,
                            SqlSyntax syntax,
                            SqlOperandTypeChecker operandTypeChecker) {
        super(name,
                new SqlIdentifier(name, SqlParserPos.ZERO),
                SqlKind.OTHER_FUNCTION,
                sqlReturnTypeInference,
                null,
                operandTypeChecker,
                SqlFunctionCategory.USER_DEFINED_FUNCTION);
        this.isDeterministic = isDeterministic;
        this.isDynamic = isDynamic;
        this.syntax = syntax;
    }

    @Override
    public SqlSyntax getSyntax() {
        return syntax;
    }

    @Override
    public boolean isDeterministic() {
        return isDeterministic;
    }

    @Override
    public boolean isDynamicFunction() {
        return isDynamic;
    }
}
