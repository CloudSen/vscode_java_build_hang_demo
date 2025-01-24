package cn.cisdigital.datakits.framework.common.util;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.cisdigital.datakits.framework.common.util.FunctionUtil.buildListMergeFunc;
import static cn.cisdigital.datakits.framework.common.util.FunctionUtil.consumerToFunc;
import static cn.hutool.core.collection.CollStreamUtil.toList;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * 简易版Fork/Join分批执行工具
 *
 * @author xxx
 * @since 2024/10/18 下午2:55
 */
@Slf4j
public class BatchUtil {

    /**
     * 批量执行, 返集合数据, 按顺序切割参数
     *
     * @param paramList   参数集合
     * @param batch       分批大小
     * @param executeFunc 执行函数
     * @param executor    线程池
     * @return 合并所有结果的集合
     */
    public static <T, R> List<R> batchExecuteMergeList(List<T> paramList, int batch,
            Function<List<T>, List<R>> executeFunc, ThreadPoolTaskExecutor executor) {
        return batchExecute(paramList, batch, ListUtil::split, executeFunc, buildListMergeFunc(), executor);
    }

    /**
     * 批量执行, 返集合数据, 自定义参数切分方式
     *
     * @param paramList   参数集合
     * @param batch       分批大小
     * @param splitFunc   参数切分函数
     * @param executeFunc 执行函数
     * @param executor    线程池
     * @return 合并所有结果的集合
     */
    public static <T, R> List<R> batchExecuteMergeList(List<T> paramList, int batch,
            BiFunction<List<T>, Integer, List<List<T>>> splitFunc, Function<List<T>, List<R>> executeFunc,
            ThreadPoolTaskExecutor executor) {
        return batchExecute(paramList, batch, splitFunc, executeFunc, buildListMergeFunc(), executor);
    }

    /**
     * 批量执行, 无返回值, 按顺序切割参数
     *
     * @param paramList   参数集合
     * @param batch       分批大小
     * @param executeFunc 执行函数
     * @param executor    线程池
     */
    public static <T> void batchConsumer(List<T> paramList, int batch, Consumer<List<T>> executeFunc,
            ThreadPoolTaskExecutor executor) {
        batchExecute(paramList, batch, ListUtil::split, consumerToFunc(executeFunc), Function.identity(), executor);
    }

    /**
     * 批量执行, 无返回值, 自定义参数切分方式
     *
     * @param paramList   参数集合
     * @param batch       分批大小
     * @param splitFunc   参数切分函数
     * @param executeFunc 执行函数
     * @param executor    线程池
     */
    public static <T> void batchConsumer(List<T> paramList, int batch,
            BiFunction<List<T>, Integer, List<List<T>>> splitFunc, Consumer<List<T>> executeFunc,
            ThreadPoolTaskExecutor executor) {
        batchExecute(paramList, batch, splitFunc, consumerToFunc(executeFunc), Function.identity(), executor);
    }

    /**
     * 批量执行
     *
     * @param paramList       参数集合
     * @param batch           分批大小
     * @param splitFunc       参数切分函数
     * @param executeFunc     执行函数
     * @param resultMergeFunc 结果合并函数
     * @param executor        线程池
     */
    public static <T, D, R> R batchExecute(List<T> paramList, int batch,
            BiFunction<List<T>, Integer, List<List<T>>> splitFunc, Function<List<T>, D> executeFunc,
            Function<List<D>, R> resultMergeFunc, ThreadPoolTaskExecutor executor) {
        if (CollUtil.size(paramList) <= batch) {
            List<D> singletonList = new ArrayList<>();
            singletonList.add(executeFunc.apply(paramList));
            return resultMergeFunc.apply(singletonList);
        }
        List<List<T>> list = splitFunc.apply(paramList, batch);
        List<CompletableFuture<D>> futures = toList(list, param -> supplyAsync(() -> {
            long start = System.currentTimeMillis();
            D result = executeFunc.apply(param);
            long end = System.currentTimeMillis();
            log.info("执行方法耗时: {} ms", end - start);
            return result;
        }, executor));
        log.info("当前批量任务线程池情况, threadNamePrefix: {}, poolSize: {}. activeCount: {}, queueSize: {}",
                executor.getThreadNamePrefix(), executor.getPoolSize(), executor.getActiveCount(),
                executor.getQueueSize());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<D> resultList = CollStreamUtil.toList(futures, CompletableFuture::join);
        return resultMergeFunc.apply(resultList);
    }
}
