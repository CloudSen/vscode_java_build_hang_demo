package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 血缘生成类型枚举
 *
 * @author xxx
 * @since 2024-04-19-8:56
 */
@Getter
@AllArgsConstructor
public enum LineageProducerEnum implements BaseEnum {

    /**
     * 手动
     */
    MANUAL(1,"model.lineage.producer.manual"),

    /**
     * 自动
     */
    AUTO(2,"model.lineage.producer.auto"),

    ;

    private final int code;
    private final String key;
}
