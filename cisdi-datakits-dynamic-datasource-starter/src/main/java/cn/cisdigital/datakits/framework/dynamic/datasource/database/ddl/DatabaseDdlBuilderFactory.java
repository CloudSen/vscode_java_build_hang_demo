package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;


import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * 数据源DDL构建器工厂
 *
 * @author xxx
 */
public class DatabaseDdlBuilderFactory {

    private static final Map<DataBaseTypeEnum, DatabaseDdlBuilderStrategy> STRATEGY_MAP = new EnumMap<>(DataBaseTypeEnum.class);

    static {
        STRATEGY_MAP.put(DataBaseTypeEnum.MYSQL, new MysqlDdlBuilder());
        STRATEGY_MAP.put(DataBaseTypeEnum.DORIS, new DorisDdlBuilder());
    }

    public static DatabaseDdlBuilderStrategy getBuilder(DataBaseTypeEnum type) {
        return STRATEGY_MAP.get(type);
    }
}
