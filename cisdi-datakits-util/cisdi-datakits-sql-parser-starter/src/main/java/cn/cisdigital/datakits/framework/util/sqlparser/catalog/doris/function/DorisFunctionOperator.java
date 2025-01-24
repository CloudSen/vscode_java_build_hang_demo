package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.function;

import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.IFunctionConfig;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlBasicFunction;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeFamily;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlNameMatcher;
import org.apache.calcite.sql.validate.SqlNameMatchers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * doris函数注册器
 *
 * @author xxx
 */
@Slf4j
public class DorisFunctionOperator implements SqlOperatorTable {

    private static class SingletonHolder {
        private static final DorisFunctionOperator INSTANCE = new DorisFunctionOperator();
    }
    public static DorisFunctionOperator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //Calcite 默认的操作符表
    private static final SqlOperatorTable stdOperatorTable = SqlStdOperatorTable.instance();
    //用于存储自定义的函数名和实现的映射
    private final ListMultimap<String, SqlOperator> opMap = ArrayListMultimap.create();
    //所有的函数列表
    private final List<SqlOperator> operators = new ArrayList<>();

    private DorisFunctionOperator() {
        opMap.put(DATE_FORMAT.getName(), DATE_FORMAT);
        opMap.put(YEAR.getName(), YEAR);
        opMap.put(EXTRACT.getName(), EXTRACT);
    }

    @Override
    public void lookupOperatorOverloads(SqlIdentifier opName, @Nullable SqlFunctionCategory category, SqlSyntax syntax, List<SqlOperator> operatorList, SqlNameMatcher nameMatcher) {

        //先从doris的函数列表中查找
        if (syntax == SqlSyntax.FUNCTION && opName.isSimple()) {
            List<SqlOperator> ops = opMap.get(opName.getSimple().toUpperCase());
            if (CollectionUtils.isNotEmpty(ops)) {
                operatorList.addAll(ops);
                return;
            }
        }

        //再查找Calcite原生的函数列表
        stdOperatorTable.lookupOperatorOverloads(opName, category, syntax, operatorList, SqlNameMatchers.withCaseSensitive(false));
    }

    @Override
    public List<SqlOperator> getOperatorList() {
        return operators;
    }

    public Map<String, IFunctionConfig> registerFunction(Collection<? extends IFunctionConfig> functionConfigs) {
        if (CollectionUtils.isEmpty(functionConfigs)) {
            return Collections.emptyMap();
        }
        // 对该操作符的调用是否保证在给定相同操作数的情况下始终返回相同的结果;默认情况下假定为True。
        final boolean isDeterministic = true;
        // 引用此操作符的缓存查询计划是否不安全;默认为False。
        final boolean isDynamic = false;
        final Map<String, IFunctionConfig> executeResult = Maps.newHashMap();
        functionConfigs.forEach(config -> {
            try {
                if (StringUtils.isEmpty(config.getFunctionName())) {
                    log.warn("function name is null");
                    return;
                }
                //生成函数的入参
                String functionArgumentTypes = Objects.isNull(config.getFunctionArgumentTypes()) ? "[]" : config.getFunctionArgumentTypes();
                List<SqlTypeFamily> argumentTypes = JsonUtils.parseList(functionArgumentTypes, SqlTypeFamily.class);
                //用于函数入参匹配和检查
                SqlOperandTypeChecker checker = OperandTypes.family(argumentTypes);
                //函数返回值
                final SqlReturnTypeInference returnTypeInference;
                if (Objects.isNull(config.getFunctionReturnPrecision())) {
                    returnTypeInference = ReturnTypes.explicit(SqlTypeName.valueOf(config.getFunctionReturnType()));
                } else {
                    returnTypeInference = ReturnTypes.explicit(SqlTypeName.valueOf(config.getFunctionReturnType()), config.getFunctionReturnPrecision());
                }
                SqlFunction sqlFunction = new DorisSqlFunction(config.getFunctionName(),
                        isDeterministic,
                        isDynamic,
                        returnTypeInference,
                        SqlSyntax.FUNCTION,
                        checker);
                opMap.put(config.getFunctionName(), sqlFunction);
                operators.add(sqlFunction);
                executeResult.put(config.getFunctionName(), config);
            } catch (Exception e) {
                log.warn("function {} register error : ", config.getFunctionName(), e);
            }
        });
        return executeResult;
    }

    public static final SqlFunction DATE_FORMAT =
            SqlBasicFunction.create("DATE_FORMAT",
                    ReturnTypes.VARCHAR,
                    OperandTypes.family(SqlTypeFamily.STRING, SqlTypeFamily.STRING), SqlFunctionCategory.USER_DEFINED_FUNCTION);

    public static final SqlFunction YEAR =
            SqlBasicFunction.create("YEAR",
                    ReturnTypes.VARCHAR,
                    OperandTypes.family(SqlTypeFamily.STRING), SqlFunctionCategory.USER_DEFINED_FUNCTION);

    public static final SqlFunction EXTRACT = new DorisSqlExtractFunction("EXTRACT");
}
