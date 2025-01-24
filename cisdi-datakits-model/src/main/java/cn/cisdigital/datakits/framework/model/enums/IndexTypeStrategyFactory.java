package cn.cisdigital.datakits.framework.model.enums;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引类型枚举值工厂
 *
 * @author xxx
 * @since 2024/09/02
 */
@Slf4j
public class IndexTypeStrategyFactory {

    private IndexTypeStrategyFactory() {
    }

    private static final Map<String, IndexType> STRATEGY_MAP = new HashMap<>();

    static {
        List<Class<? extends IndexType>> classList = getIndexTypeEnumClassList();
        for (Class<? extends IndexType> aClass : classList) {
            Object[] enumConstants = aClass.getEnumConstants();
            for (Object enumConstant : enumConstants) {
                IndexType enums = (IndexType) enumConstant;
                String key = getIndexUniqueKey(enums.getDatabaseTypeEnum(), enums.getIndexTypeName());
                if (STRATEGY_MAP.containsKey(key)) {
                    log.error("存在相同名称的索引枚举定义: {}", key);
                    throw new IllegalArgumentException();
                }
                STRATEGY_MAP.put(key, enums);
            }
        }
    }

    private static List<Class<? extends IndexType>> getIndexTypeEnumClassList() {
        List<Class<? extends IndexType>> classList = Lists.newArrayList();
        classList.add(DorisIndexTypeEnum.class);
        return classList;
    }

    /**
     * 根据索引类型唯一标识获取IndexType枚举
     *
     * @param indexUniqueKey 索引类型唯一标识，用于查找对应的IndexType枚举，通过{@link  #getIndexUniqueKey(DataBaseTypeEnum, String)}生成
     * @return 返回对应的IndexType枚举对象，如果找不到则返回null
     */
    public static IndexType getIndexType(String indexUniqueKey) {
        return STRATEGY_MAP.get(indexUniqueKey);
    }

    /**
     * 根据数据库类型和索引类型字符串获取对应的索引类型
     * <p>
     * 该方法通过使用预定义的策略映射来获取索引类型，首先根据数据库类型枚举和索引类型字符串
     * 构建一个唯一的键，然后在这个键的基础上从策略映射中获取对应的索引类型如果策略映射中
     * 没有对应的索引类型，则返回null
     *
     * @param dataTypeEnum 数据库类型枚举，用于确定所使用的数据库类型
     * @param indexTypEnName 索引类型字符串，表示特定的索引类型
     * @return 返回与数据库类型和索引类型对应的索引类型对象，如果找不到则返回null
     */
    public static IndexType getIndexType(DataBaseTypeEnum dataTypeEnum, String indexTypEnName){
        return STRATEGY_MAP.get(getIndexUniqueKey(dataTypeEnum, upperIndexTypeName(indexTypEnName)));
    }

    /**
     * 获取索引枚举唯一标识
     * <p>逆向解析时，只能拿到索引类型的英文名和数据库类型，所以这里使用这2个信息构建唯一key映射到枚举</p>
     *
     * @param dataType  数据库类型
     * @param indexTypEnName 索引类型英文名
     * @return 唯一字符串
     */
    public static String getIndexUniqueKey(DataBaseTypeEnum dataType, String indexTypEnName) {
        return dataType + "_" + upperIndexTypeName(indexTypEnName);
    }

    /**
     * 统一设置索引类型名为大写
     * @param indexTypEnName 索引类型英文名
     * @return 大写的索引类型名
     */
    private static String upperIndexTypeName(String indexTypEnName) {
        return indexTypEnName.toUpperCase();
    }

}
