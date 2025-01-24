package cn.cisdigital.datakits.framework.util.qbeeopensdk.auto.convertor;

/**
 * 设置业务对象的某个属性
 *
 * @param <T> 数据对象
 * @param <M> 源数据对象
 * @author xxx
 */
@FunctionalInterface
public interface FillFunction<T, M> {

    /**
     * 设置自定义业务参数
     *
     * @param targetObj 数据对象
     * @param m 源数据对象
     */
    void setParam(T targetObj, M m);
}
