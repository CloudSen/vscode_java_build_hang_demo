package cn.cisdigital.datakits.framework.model.enums;

import java.util.*;

/**
 * 列类型枚举值工厂
 *
 * @author xxx
 */
public class ColumnTypeStrategyFactory {

    private static final Map<String, ColumnType> STRATEGY_MAP = new HashMap<>();

    static {
        List<Class<?>> classList = getClassList();
        for (Class<?> aClass : classList) {
            Object[] enumConstants = aClass.getEnumConstants();
            for (Object enumConstant : enumConstants) {
                Enum enums = (Enum) enumConstant;
                String name = enums.name();
                STRATEGY_MAP.put(name, (ColumnType) enums);
            }
        }
    }

    private static List<Class<?>> getClassList() {
        List<Class<?>> classList = new ArrayList<>();
        classList.add(MysqlColumnEnum.class);
        classList.add(DorisColumnEnum.class);
        classList.add(DB2ColumnEnum.class);
        classList.add(OracleColumnEnum.class);
        classList.add(SqlServerColumnEnum.class);
        classList.add(PostgreSqlColumnEnum.class);
        classList.add(SqliteColumnEnum.class);
        classList.add(MsAccessColumnEnum.class);
        classList.add(DMColumnEnum.class);
        classList.add(KingBaseColumnEnum.class);
        classList.add(TiDBColumnEnum.class);
        classList.add(SybaseColumnEnum.class);
        classList.add(SapColumnEnum.class);
        return classList;
    }

    public static Collection<ColumnType> listColumnType() {
        return STRATEGY_MAP.values();
    }

    public static ColumnType getColumnType(String columnType) {
        return STRATEGY_MAP.get(columnType);
    }
}
