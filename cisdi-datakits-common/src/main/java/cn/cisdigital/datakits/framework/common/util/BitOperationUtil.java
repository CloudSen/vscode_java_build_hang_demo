package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;

/**
 * 位运算工具类
 *
 * @author xxx
 * @since 2024/5/20 12:04
 */
public class BitOperationUtil {

    private BitOperationUtil() {

    }

    /**
     * 检查枚举是否存在某个属性
     */
    public static Boolean hasStatus(Integer status, BaseEnum baseEnum) {
        return (baseEnum.getCode() & status) == baseEnum.getCode();
    }

    /**
     * 在原来的基础上添加某种属性
     */
    public static int addStatus(Integer status, BaseEnum baseEnum) {
        if (hasStatus(status, baseEnum)) {
            return status;
        }
        return (status | baseEnum.getCode());
    }

    /**
     * 移除某种属性
     */
    public static int removeStatus(Integer status, BaseEnum baseEnum) {
        if (!hasStatus(status, baseEnum)) {
            return status;
        }
        return status ^ baseEnum.getCode();
    }
}
