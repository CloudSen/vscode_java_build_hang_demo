package cn.cisdigital.datakits.framework.model.interfaces;


/**
 * 代表一个无入参，无返回的逻辑执行
 *
 * @author xxx
 * @since 2022-09-26
 */
@FunctionalInterface
public interface Executable {

    /**
     * 逻辑执行入口
     */
    void execute();
}
