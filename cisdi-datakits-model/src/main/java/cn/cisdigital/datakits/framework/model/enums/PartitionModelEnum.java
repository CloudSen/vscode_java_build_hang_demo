package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xxx
 * @since 2024-03-14-9:59
 */
@Getter
@AllArgsConstructor
public enum PartitionModelEnum implements BaseEnum {

    /**
     * 默认分区
     */
    DEFAULT_PARTITION(1),
    /**
     * 自定义 分区
     */
    CUSTOM_PARTITION (2),
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

}
