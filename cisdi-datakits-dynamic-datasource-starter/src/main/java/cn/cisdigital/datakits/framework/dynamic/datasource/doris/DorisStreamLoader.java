package cn.cisdigital.datakits.framework.dynamic.datasource.doris;

import cn.cisdigital.datakits.framework.cloud.alibaba.interceptor.RedirectInterceptor;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.CustomHttpProperties;
import cn.cisdigital.datakits.framework.dynamic.datasource.common.DorisStatusConstants;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisColumnValueDto;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisStorageDataDto;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisStreamLoadConfigDto;
import cn.cisdigital.datakits.framework.dynamic.datasource.exception.DorisStreamLoadErrorCode;
import cn.cisdigital.datakits.framework.dynamic.datasource.exception.DorisStreamLoadException;
import cn.cisdigital.datakits.framework.dynamic.datasource.utils.AuthUtils;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.COMMA;


/**
 * @author xxx
 */
@Slf4j
@Data
public class DorisStreamLoader {

    /**
     * stream_load编译模板
     */
    private static final String LOAD_URL_TEMPLATE = "http://%s:%s/api/%s/%s/_stream_load";

    /**
     * 公共异常部分模板
     */
    private static final String ERROR_TEMPLATE = "doris_stream_load error: %s, url: %s, response: %s, dataCount: %s, dataSize: %s, 耗时: %s ms";
    /**
     * 字符串常量
     */
    private static final String LABEL = "label";
    private static final String COLUMN_SEPARATOR = "column_separator";
    private static final String FORMAT = "format";
    private static final String STRIP_OUTER_ARRAY = "strip_outer_array";
    private static final String NUM_AS_STRING = "num_as_string";
    private static final String JSON_STR = "json";
    private static final String CONTINUE_100 = "100-continue";
    private static final String MERGE_TYPE = "merge_type";
    private static final String DELETE = "DELETE";
    private static final String STATUS = "Status";
    public static final String ERROR_URL = "ErrorURL";
    public static final String COLUMNS = "columns";
    public static final String DORIS_ESCAPE = "`";
    public static final String MEDIA_TYPE = "application/json; charset=utf-8";

    private final DorisStreamLoadConfigDto streamLoadConfig;

    /**
     * http client
     */
    private final OkHttpClient httpClient;
    /**
     * http 配置
     */
    private CustomHttpProperties httpProperties;


    public DorisStreamLoader(DorisStreamLoadConfigDto streamLoadConfigDto, OkHttpClient httpClient, CustomHttpProperties httpProperties) {
        this.streamLoadConfig = streamLoadConfigDto;
        this.httpClient = httpClient;
        this.httpProperties = httpProperties;
    }

    /**
     * 保存数据
     */
    public String saveData(List<DorisStorageDataDto> storageDataList, Map<String, String> headers) {
        if (CollUtil.isNotEmpty(storageDataList)) {
            return handleDataByJson(JSON.toJSONString(getBody(storageDataList), SerializerFeature.WriteDateUseDateFormat),
                    getColumnHeader(storageDataList.get(0)), null, storageDataList.size(), headers);
        }
        return DorisStatusConstants.SUCCESS;
    }

    /**
     * 删除数据
     */
    public String deleteData(List<DorisStorageDataDto> deleteDataList, Map<String, String> headers) {
        if (CollUtil.isNotEmpty(deleteDataList)) {
           return handleDataByJson(JSON.toJSONString(getBody(deleteDataList), SerializerFeature.WriteDateUseDateFormat),
                    getColumnHeader(deleteDataList.get(0)), DELETE, deleteDataList.size(), headers);
        }
        return DorisStatusConstants.SUCCESS;
    }

    /**
     * JSON import
     */
    private String handleDataByJson(String jsonData, List<String> headerColumn, String mergeType, Integer dataCount, Map<String, String> headers) {
        // 获取请求地址
        String requestUrl = String.format(LOAD_URL_TEMPLATE, streamLoadConfig.getHost(), streamLoadConfig.getPort(),
            streamLoadConfig.getDatabaseName(), streamLoadConfig.getTableName());
        // 设置自定义header
        Headers.Builder headerBuilder = new Headers.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(headerBuilder::set);
        }
        // 设置请求必须的header
        setRequestHeaders(headerBuilder, headerColumn);
        // 设置删除数据的header
        if (CharSequenceUtil.isNotBlank(mergeType)) {
            headerBuilder.set(MERGE_TYPE, DELETE);
        }
        // 设置请求体
        RequestBody body = RequestBody.create(jsonData, MediaType.parse(MEDIA_TYPE));
        // 构建请求builder
        final Headers requestHeaders = headerBuilder.build();
        // 构建request请求
        Request request = new Request.Builder()
            .url(requestUrl)
            .headers(requestHeaders)
            .put(body)
            .build();
        log.info("stream-load保存数据, header: {}", headerBuilder.removeAll(HttpHeaders.AUTHORIZATION).build());
        return executeRequest(request, jsonData, dataCount);
    }

    /**
     * 执行请求
     */
    private String executeRequest(Request request, String jsonData, Integer dataCount) {
        HttpUrl url = request.url();
        // 设置请求配置
        long startTime = System.currentTimeMillis();
        // 生成新的client
        OkHttpClient.Builder builder = httpClient.newBuilder()
            // 禁用默认重定向
            .followRedirects(false)
            .addInterceptor(new RedirectInterceptor());
        // 设置超时时间
        if (httpProperties.getConnectTimeout().getSeconds() > 0) {
            builder.connectTimeout(httpProperties.getConnectTimeout());
        }
        if (httpProperties.getReadTimeout().getSeconds() > 0) {
            builder.readTimeout(httpProperties.getReadTimeout());
        }
        try (Response response = builder.build().newCall(request).execute()) {
            // 请求失败
            if (!response.isSuccessful()) {
                String errorLog = String.format(ERROR_TEMPLATE, "插入数据响应失败", url, response, dataCount, getStringSize(jsonData), System.currentTimeMillis() - startTime);
                log.error(errorLog);
                throw new DorisStreamLoadException(DorisStreamLoadErrorCode.WRITE_DATA_FAIL, response, errorLog);
            }
            ResponseBody responseBody = response.body();
            if (Objects.isNull(responseBody)) {
                throw new DorisStreamLoadException(DorisStreamLoadErrorCode.WRITE_DATA_FAIL, response, "插入数据响应结果响应结果为空!");
            }
            String loadResult = responseBody.string();
            // 请求成功，查看响应结果
            if (StringUtils.isBlank(loadResult)) {
                throw new DorisStreamLoadException(DorisStreamLoadErrorCode.WRITE_DATA_FAIL, response, "插入数据响应结果响应结果为空!");
            }
            // 解析响应结果
            JSONObject resultJsonObject = JSON.parseObject(loadResult, JSONObject.class);
            String status = resultJsonObject.getString(STATUS);
            String errorUrl = resultJsonObject.getString(ERROR_URL);
            if (!isSuccess(status)) {
                String errorLog = String.format(ERROR_TEMPLATE, "插入数据响应结果错误", url, response, dataCount, getStringSize(jsonData), System.currentTimeMillis() - startTime);
                log.error(errorLog);
                // 请求地址获取详细日志详情
                String detailLog = getDetailErrorLog(errorUrl);
                throw new DorisStreamLoadException(DorisStreamLoadErrorCode.WRITE_DATA_FAIL, loadResult, errorLog + COMMA + detailLog);
            }
            return status;
        } catch (DorisStreamLoadException e) {
            throw e;
        } catch (Exception e) {
            String errorLog = String.format(ERROR_TEMPLATE, "插入数据出现异常", url, null, dataCount, getStringSize(jsonData), System.currentTimeMillis() - startTime);
            log.error(errorLog, e);
            throw new DorisStreamLoadException(DorisStreamLoadErrorCode.WRITE_DATA_FAIL, e, null, errorLog + COMMA + e.getMessage());
        }
    }

    private String getDetailErrorLog(String errorUrl) {
        if (StringUtils.isBlank(errorUrl)) {
            return "";
        }
        Request errorLogRequest = new Request.Builder().get().url(errorUrl).build();
        try (Response logResponse = httpClient.newCall(errorLogRequest).execute()) {
            if (logResponse.isSuccessful() && logResponse.body() != null) {
                return logResponse.body().string();
            }
        } catch (IOException e) {
            log.error("获取ErrorUrl详细日志失败", e);
        }
        return "";
    }

    private void setRequestHeaders(Headers.Builder headerBuilder, List<String> headerColumn) {
        // 移除限制header
        headerBuilder.removeAll(HttpHeaders.CONTENT_LENGTH);
        headerBuilder.removeAll(HttpHeaders.TRANSFER_ENCODING);

        // 设置新的header
        headerBuilder.set(HttpHeaders.EXPECT, CONTINUE_100);
        headerBuilder.set(HttpHeaders.AUTHORIZATION,
            AuthUtils.basicAuthHeader(streamLoadConfig.getUser(), streamLoadConfig.getPasswd()));
        headerBuilder.set(COLUMN_SEPARATOR, COMMA);
        headerBuilder.set(FORMAT, JSON_STR);
        headerBuilder.set(STRIP_OUTER_ARRAY, Boolean.TRUE.toString());
        headerBuilder.set(NUM_AS_STRING, Boolean.TRUE.toString());
        headerBuilder.set(COLUMNS, String.join(COMMA, headerColumn));
        if (StringUtils.isBlank(headerBuilder.get(LABEL))) {
            // 设置随机的label
            headerBuilder.set(LABEL, UUID.randomUUID().toString());
        }
    }

    private boolean isSuccess(String status) {
        if (DorisStatusConstants.PUBLISH_TIMEOUT.equalsIgnoreCase(status)) {
            log.warn("Doris写入数据状态异常，请及时检查Doris，status: {}", DorisStatusConstants.PUBLISH_TIMEOUT);
        }
        if (Objects.nonNull(status) && !status.isEmpty()) {
            return DorisStatusConstants.SUCCESS_STATUS_LIST.stream().anyMatch(s -> s.equalsIgnoreCase(status));
        }
        return false;
    }

    private List<Map<String, Object>> getBody(List<DorisStorageDataDto> storageDataList) {
        return storageDataList.stream().map(data -> {
            List<DorisColumnValueDto> targetValueList = data.getTargetColumnValueList();
            List<DorisColumnValueDto> prepareValueList = targetValueList.stream()
                .filter(dorisColumnValueDto -> !dorisColumnValueDto.isFunctionValue())
                .collect(Collectors.toList());
            return CollStreamUtil.toMap(prepareValueList, DorisColumnValueDto::getColumnName,
                DorisColumnValueDto::getValue);
        }).collect(Collectors.toList());
    }

    private List<String> getColumnHeader(DorisStorageDataDto dorisStorageDataDto) {
        // 普通采集列
        List<String> columnList = dorisStorageDataDto.getTargetColumnValueList().stream()
            .filter(s -> !s.isFunctionValue())
            .map(DorisColumnValueDto::getColumnName)
            .map(this::wrappedEscapeCharacter)
            .collect(Collectors.toList());
        // 函数列
        List<String> functionColumn = dorisStorageDataDto.getTargetColumnValueList().stream()
            .filter(DorisColumnValueDto::isFunctionValue)
            .map(k -> this.wrappedEscapeCharacter(k.getColumnName()) + "=" + k.getValue()).collect(Collectors.toList());
        columnList.addAll(functionColumn);
        return columnList;
    }

    /**
     * 包装转义符
     */
    public String wrappedEscapeCharacter(String columnName) {
        if (CharSequenceUtil.isBlank(columnName) || (columnName.startsWith(DORIS_ESCAPE) && columnName.endsWith(DORIS_ESCAPE))) {
            return columnName;
        }
        return DORIS_ESCAPE + columnName + DORIS_ESCAPE;
    }

    private Integer getStringSize(String jsonData) {
        if (Objects.isNull(jsonData)) {
            return 0;
        }
        int length = -1;
        try {
            length = jsonData.getBytes(StandardCharsets.UTF_8).length;
        } catch (Exception e) {
            log.error("获取数据量大小失败", e);
        }
        return length;
    }
}
