package cn.cisdigital.datakits.framework.model.interfaces;

import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import java.io.Serializable;
import javax.annotation.Nullable;

/**
 * 错误代码
 *
 * @author xxx
 */
public interface ErrorCode extends Serializable {

    /**
     * 当前系统自己的错误代码
     */
    String getCode();

    /**
     * 国际化文件中的key值
     */
    String getKey();

    /**
     * 国际化之后的错误代码含义
     */
    default String getMsg(@Nullable Object[] args) {
        return I18nUtils.getOriginMessageIfNotExits(getKey(), args);
    }

    /**
     * 国际化之后的错误代码含义
     */
    default String getMsg() {
        return I18nUtils.getOriginMessageIfNotExits(getKey(), null);
    }
}
