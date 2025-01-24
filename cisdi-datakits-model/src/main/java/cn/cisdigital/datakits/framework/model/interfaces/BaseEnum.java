package cn.cisdigital.datakits.framework.model.interfaces;

import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nullable;

/**
 * 基础枚举类，业务枚举必须要继承BaseEnum
 *
 * @author xxx
 */
public interface BaseEnum {

    /**
     * 业务code
     */
    @JsonValue
    int getCode();

    /**
     * 国际化文件中的key值
     */
    String getKey();

    /**
     * 国际化之后的业务描述
     */
    default String getDesc(@Nullable Object[] args) {
        return I18nUtils.getOriginMessageIfNotExits(getKey(), args);
    }

    /**
     * 国际化之后的业务描述
     */
    default String getDesc() {
        return I18nUtils.getOriginMessageIfNotExits(getKey(), null);
    }
}
