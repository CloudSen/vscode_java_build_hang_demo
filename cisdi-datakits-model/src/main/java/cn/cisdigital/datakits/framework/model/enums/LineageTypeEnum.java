package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 血缘类型枚举
 *
 * @author xxx
 * @since 2024-04-19-8:56
 */
@Getter
@AllArgsConstructor
public enum LineageTypeEnum implements BaseEnum {

    /**
     * 代码式开发任务
     */
    COMPLEX_TASK(1,"model.lineage_type.complex_task"),

    /**
     * 配置式开发任务
     */
    SIMPLE_TASK(2,"model.lineage_type.simple_task"),

    /**
     * 数仓表
     */
    TABLE(3,"model.lineage_type.table"),

    /**
     * 数仓字段
     */
    COLUMN(4,"model.lineage_type.column"),

    ;

    private final int code;
    private final String key;
}
