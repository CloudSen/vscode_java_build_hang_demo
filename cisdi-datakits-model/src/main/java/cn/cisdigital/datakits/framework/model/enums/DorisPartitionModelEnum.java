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
public enum DorisPartitionModelEnum implements BaseEnum {

    /**
     * Range 分区
     */
    RANGE(1, "PARTITION BY RANGE"),
    /**
     * LIST 分区
     */
    LIST(2, "PARTITION BY LIST"),
    ;


    private final int code;
    /**
     * doris数据模型的语法编码
     */
    private final String dorisCode;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getKey() {
        return this.name();
    }

}
