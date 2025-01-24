package cn.cisdigital.datakits.framework.dynamic.datasource.database.engine;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;

import java.util.EnumMap;
import java.util.Map;

/**
 * database引擎相关服务工厂
 *
 * @author xxx
 */
public class DatabaseEngineStrategyFactory {

    private static final Map<DataBaseTypeEnum, DatabaseEngineStrategy> STRATEGY_MAP = new EnumMap<>(DataBaseTypeEnum.class);

    static {
        STRATEGY_MAP.put(DataBaseTypeEnum.MYSQL, new MysqlEngine());
        STRATEGY_MAP.put(DataBaseTypeEnum.DORIS, new DorisEngine());
    }

    public static DatabaseEngineStrategy getEngine(DataBaseTypeEnum type) {
        return STRATEGY_MAP.get(type);
    }
}
