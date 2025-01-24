package cn.cisdigital.datakits.framework.model.enums;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 血缘模块系统枚举
 *
 * @author xxx
 * @since 2024-04-19-8:56
 */
@Getter
@AllArgsConstructor
public enum LineageModelEnum implements BaseEnum {

    /**
     * 代码式开发
     */
    COMPLEX_TASK_SYSTEM(1, "model.lineage_model.complex_task_system"),

    /**
     * 配置式开发
     */
    SIMPLE_TASK_SYSTEM(2, "model.lineage_model.simple_task_system"),
    ;

    private final int code;
    private final String key;
}
