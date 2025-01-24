package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;

import cn.cisdigital.datakits.framework.model.enums.DorisIndexTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.IndexType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xxx
 * @date 2024/8/28 11:10
 **/
public class DatabaseIndexBuilderFactory {

    private static final Map<IndexType, DatabaseIndexBuilderStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        STRATEGY_MAP.put(DorisIndexTypeEnum.DORIS_INVERTED, new InvertedIndexBuilder());
    }

    public static DatabaseIndexBuilderStrategy getBuilder(IndexType type) {
        DatabaseIndexBuilderStrategy strategy = STRATEGY_MAP.get(type);
        if (Objects.isNull(strategy)) {
            throw new UnsupportedOperationException();
        }
        return strategy;
    }
}
