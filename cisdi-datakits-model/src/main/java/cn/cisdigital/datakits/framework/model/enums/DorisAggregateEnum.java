package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024-03-13-16:23
 */

@Getter
@RequiredArgsConstructor
public enum DorisAggregateEnum implements BaseEnum {

    /**
     * 未知聚合类型
     */
    UNKNOWN(-1),
    /**
     * 求和，多行的 Value 进行累加。
     */
    SUM(1),
    /**
     * 替代，下一批数据中的 Value 会替换之前导入过的行中的 Value
     */
    REPLACE(2),
    /**
     * 保留最大值
     */
    MAX(3),
    /**
     * 保留最小值
     */
    MIN(4),
    /**
     * 非空值替换。和 REPLACE 的区别在于对于null值，不做替换
     */
    REPLACE_IF_NOT_NULL(5),
    /**
     * HLL 类型的列的聚合方式，通过 HyperLogLog 算法聚合
     */
    HLL_UNION(6),
    /**
     * BIMTAP 类型的列的聚合方式，进行位图的并集聚合
     */
    BITMAP_UNION(7),
    /**
     * quantile_state 类型的列的聚合方式
     */
    QUANTILE_UNION(8);
    private final int code;

    private static final Map<String, DorisAggregateEnum> AGGREGATE_MAP = Arrays.stream(DorisAggregateEnum.values())
            .collect(Collectors.toMap(DorisAggregateEnum::name, Function.identity()));

    public static DorisAggregateEnum parse(String dorisAggregate) {
        DorisAggregateEnum aggregateEnum = AGGREGATE_MAP.get(dorisAggregate);
        return aggregateEnum == null ? UNKNOWN : aggregateEnum;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

}
