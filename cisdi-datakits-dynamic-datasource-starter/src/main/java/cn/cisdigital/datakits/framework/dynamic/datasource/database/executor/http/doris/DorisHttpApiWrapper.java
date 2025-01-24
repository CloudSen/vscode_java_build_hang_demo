package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.http.doris;

import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.DorisConnectionInfo;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.function.Supplier;

/**
 * doris rest api操作封装
 *
 * @author xxx
 */
@Slf4j
@Component
public class DorisHttpApiWrapper {

    @Autowired
    private DorisRestApiClient dorisRestApiClient;

    /**
     * 参考dwbuilder的即席查询调用doris api停止sql背后的任务
     * 1. 需要拿到doris相关的地址信息配置
     * 2. 调用接口根据traceId查询queryId "http://dev-master1:8050/rest/v2/manager/query/trace_id/9527888";
     * 3. 根据querId杀掉执行的正在执行的sql "http://dev-master1:8050/rest/v2/manager/query/kill/${query_id}";
     *
     * @param dorisConnectionInfo doris连接信息
     * @param taskTraceId        doris后台查询对应的追踪id
     * @apiNote 注意不要把异常抛出去了, 如果前端传入的当前env与实际跑的sql对应不上, 可能会拿到其他环境的数据源, 导致此api调用查不到对应的query_id, 则直接忽略即可
     */
    public void killDorisQuery(@NotNull DorisConnectionInfo dorisConnectionInfo, @NotNull String taskTraceId) {
        // 1. 调用接口根据traceId查询queryId
        String queryId = null;
        try {
            queryId = getNonNullData(() -> dorisRestApiClient.getQueryIdByTraceId(dorisConnectionInfo, taskTraceId));
        } catch (Exception e) {
            log.warn("[数据探查] [停止任务] doris sql停止失败, 原因: 根据traceId查询queryId失败, 可能该账号没有该api的权限, 忽略该错误. username={}, traceId={}",
                dorisConnectionInfo.getUsername(), taskTraceId, e);
        }

        if (queryId != null) {
            // 2. 根据queryId杀掉正在执行的sql
            try {
                String finalQueryId = queryId;
                getNonNullData(() -> dorisRestApiClient.killByQueryId(dorisConnectionInfo, finalQueryId));
            } catch (Exception e) {
                log.warn("[数据探查] [停止任务] doris sql停止失败, 原因: 根据queryId kill后台查询失败, 忽略该错误. queryId={}", queryId, e);
            }
        }
    }

    public static <T> T getNonNullData(Supplier<ResVo<T>> supplier) {
        ResVo<T> resVo;
        try {
            resVo = supplier.get();
            log.info("远程调用结果：{}", resVo);
        } catch (Exception e) {
            throw new RuntimeException("远程调用异常", e);
        }
        if (resVo == null) {
            throw new RuntimeException("远程调用返回结果为NULL");
        }
        if (StringUtils.equals(ResVo.SUCCESS_CODE, resVo.getCode())) {
            T data = resVo.getData();
            if (data == null) {
                throw new RuntimeException("远程调用返回的data为NULL");
            }
            return data;
        }
        throw new RuntimeException(resVo.getMessage());
    }
}
