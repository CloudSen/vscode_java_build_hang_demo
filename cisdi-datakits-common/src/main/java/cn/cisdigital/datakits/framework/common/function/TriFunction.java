package cn.cisdigital.datakits.framework.common.function;

/**
 * 三个入参的函数
 *
 * @author xxx
 * @since 2024/7/15 3:58 PM
 */
@FunctionalInterface
public interface TriFunction<T, U, I, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param i the three function argument
     * @return the function result
     */
    R apply(T t, U u, I i);
}
