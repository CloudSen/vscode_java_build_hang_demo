package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 函数工具类
 *
 * @author xxx
 */
@Slf4j
public class FunctionUtil {

    /**
     * 获取并验证结果，并打印异常信息
     *
     * @param valid 验证函数，抛出异常则代表验证未通过，可使用工具 {@link Preconditions}
     */
    public static <R> R getAndValid(Callable<R> getFunction, Consumer<R> valid, Object... logInfoWhenError) throws BusinessException {
        R result = null;
        try {
            result = getFunction.call();
            valid.accept(result);
            return result;
        } catch (Exception e) {
            log.error("获取结果出现异常，result: " + result + ", info: " + Arrays.asList(logInfoWhenError), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(e);
            }
        }
    }

    /**
     * 获取并验证结果，并异常回调，以及打印异常信息
     *
     * @param valid             验证函数，抛出异常则代表验证未通过，可使用工具 {@link Preconditions}
     * @param exceptionCallback 异常回调
     */
    public static <R> R getAndValid(Callable<R> getFunction, Consumer<R> valid, Consumer<Exception> exceptionCallback
            , Object... logInfoWhenError) throws BusinessException {
        R result = null;
        try {
            result = getFunction.call();
            valid.accept(result);
            return result;
        } catch (Exception e) {
            log.error("获取结果出现异常，result: " + result + ", info: " + Arrays.asList(logInfoWhenError), e);
            exceptionCallback.accept(e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(e);
            }
        }
    }

    /**
     * 获取结果，并打印异常信息
     */
    public static <R> R get(Callable<R> getFunction, Object... logInfoWhenError) throws BusinessException {
        return FunctionUtil.getAndValid(getFunction, emptyConsumer(), logInfoWhenError);
    }

    /**
     * 获取结果，并异常回调，以及打印异常信息
     *
     * @param exceptionCallback 异常回调
     */
    public static <R> R get(Callable<R> getFunction, Consumer<Exception> exceptionCallback,
            Object... logInfoWhenError) throws BusinessException {
        return FunctionUtil.getAndValid(getFunction, emptyConsumer(), exceptionCallback, logInfoWhenError);
    }

    /**
     * 运行并打印异常信息
     *
     * @param runnable         运行函数
     * @param logInfoWhenError 执行异常时打印的日志
     */
    public static void run(Runnable runnable, Object... logInfoWhenError) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("运行出现异常, info: " + Arrays.asList(logInfoWhenError), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(e);
            }
        }
    }

    /**
     * 空消费者
     */
    public static <R> Consumer<R> emptyConsumer() {
        return r -> {
        };
    }

    /**
     * 构建合并列表函数
     */
    public static <R> Function<List<List<R>>, List<R>> buildListMergeFunc() {
        return (List<List<R>> dataList) -> dataList.stream().flatMap(List::stream).collect(Collectors.toList());
    }


    /**
     * 将consumer转换为function
     */
    public static <T> Function<List<T>, Void> consumerToFunc(Consumer<List<T>> executeFunc) {
        return param -> {
            executeFunc.accept(param);
            return null;
        };
    }
}
