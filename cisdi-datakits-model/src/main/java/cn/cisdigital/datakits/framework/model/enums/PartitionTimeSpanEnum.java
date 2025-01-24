package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author xxx
 * @since 2024-03-14-10:05
 */
@Getter
@AllArgsConstructor
public enum PartitionTimeSpanEnum implements BaseEnum {

    /**
     * 按小时粒度分区
     */
    HOUR(1),
    /**
     * 按天粒度分区
     */
    DAY(2),
    /**
     * 按周粒度分区
     */
    WEEK(3),
    /**
     * 按月粒度分区
     */
    MONTH(4),
    /**
     * 按年粒度分区
     */
    YEAR(5),
    ;

    private final int code;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

    public static PartitionTimeSpanEnum valueOfByName(String name) {
        return Arrays.stream(PartitionTimeSpanEnum.values())
                .filter(value -> value.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}
