package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.http.doris;

import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.DorisConnectionInfo;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * doris api
 * 由于数据源的配置不同, doris api url/认证头等信息无法在定义的时候就确认
 * doris的认证头是basic认证
 *
 * @author xxx
 */
@Slf4j
@Component
public class DorisRestApiClient {

    /**
     * 根据traceId查询queryId
     *
     * @param traceId 链路id
     * @return 成功则code为0, data里面是queryId, 失败则code非0, data里面是失败信息
     */
    ResVo<String> getQueryIdByTraceId(@NotNull DorisConnectionInfo dorisKillQueryDto, @NotNull String traceId){
        String url = "http://" + dorisKillQueryDto.getHost() + ":" + dorisKillQueryDto.getDorisHttpPort() + "/rest/v2/manager/query/trace_id/" + traceId;
        HttpRequest httpRequest = HttpUtil.createGet(url).basicAuth(dorisKillQueryDto.getUsername(), dorisKillQueryDto.getPassword());

        log.info("[doris api] [getQueryIdByTraceId] url: {}, headers: {}", httpRequest.getUrl(), httpRequest.headers());

        try (HttpResponse response = httpRequest.execute()) {
            JSONObject dorisResponseJson = JSON.parseObject(response.body());

            ResVo resVo = dorisResponseJson.toJavaObject(ResVo.class);
            resVo.setMessage(dorisResponseJson.getString("msg"));

            return resVo;
        }
    }

    /**
     * 根据queryId杀掉正在执行的sql
     *
     * @param queryId 查询id
     * @return 此接口无论成功还是失败code都会返回0
     */
    ResVo<String> killByQueryId(@NotNull DorisConnectionInfo dorisKillQueryDto, @NotNull String queryId){
        String url = "http://" + dorisKillQueryDto.getHost() + ":" + dorisKillQueryDto.getDorisHttpPort() + "/rest/v2/manager/query/kill/" + queryId;
        HttpRequest httpRequest = HttpUtil.createGet(url).basicAuth(dorisKillQueryDto.getUsername(), dorisKillQueryDto.getPassword());

        log.info("[doris api] [killByQueryId] url: {}, headers: {}", httpRequest.getUrl(), httpRequest.headers());

        try (HttpResponse response = httpRequest.execute()) {
            JSONObject dorisResponseJson = JSON.parseObject(response.body());

            ResVo resVo = dorisResponseJson.toJavaObject(ResVo.class);
            resVo.setMessage(dorisResponseJson.getString("msg"));

            return resVo;
        }
    }

}
