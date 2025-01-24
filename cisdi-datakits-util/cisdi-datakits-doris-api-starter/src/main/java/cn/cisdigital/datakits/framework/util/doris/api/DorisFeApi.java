package cn.cisdigital.datakits.framework.util.doris.api;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.util.FunctionUtil;
import cn.cisdigital.datakits.framework.model.dto.doris.DatabaseTableDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisBaseDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.util.doris.api.model.DorisHttpResponse;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.cisdigital.datakits.framework.common.util.Preconditions.checkArgument;
import static cn.hutool.core.text.StrPool.COLON;
import static java.util.regex.Pattern.DOTALL;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpMethod.GET;

/**
 * Doris的FE的API接口
 *
 * @author xxx
 */
@Slf4j
@Component
public class DorisFeApi {

    private static RestTemplate REST_TEMPLATE;

    /**
     * 建表语句提取正则
     */
    private static Pattern CREATE_TABLE_PATTERN = Pattern.compile("(CREATE TABLE `[^`]+` \\(.*?\\);)\n", DOTALL);
    /**
     * 响应内容为 DorisHttpResponse<List<String>> 的类型引用
     */
    public static final ParameterizedTypeReference<DorisHttpResponse<List<String>>> LIST_STRING_TYPE_REFERENCE =
            new ParameterizedTypeReference<DorisHttpResponse<List<String>>>() {
            };
    public static final String SELECT_FROM_SQL = "select * from ";
    public static final String ERROR_RESPONSE = "Error:";
    public static final String INFORMATION_SCHEMA_DATABASE = "information_schema";

    /**
     * POST http://<host>:<port>/api/query_schema/<ns_name>/<db_name>
     */
    public static final String QUERY_SCHEMA_URL_TEMPLATE = "http://%s:%s/api/query_schema/%s/%s";
    /**
     * GET http://<host>:<port>/api/meta/namespaces/<ns_name>/databases
     */
    public static final String LIST_DATABASE_URL_TEMPLATE = "http://%s:%s/api/meta/namespaces/%s/databases";
    /**
     * GET http://<host>:<port>/api/meta/namespaces/<ns_name>/databases/<db_name>/tables
     */
    public static final String LIST_TABLE_URL_TEMPLATE = "http://%s:%s/api/meta/namespaces/%s/databases/%s/tables";

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        REST_TEMPLATE = restTemplate;
    }

    /**
     * 获取建表语句
     */
    public static Map<DatabaseTableDto, String> queryCreateTableSql(DorisBaseDto doris, List<DatabaseTableDto> list) {
        List<DatabaseTableDto> copyList = CollUtil.distinct(CollUtil.newArrayList(list));
        // 移除不存在的表
        List<DatabaseTableDto> notExistTableList = listNotExistTable(doris, copyList);
        copyList.removeAll(notExistTableList);
        if (CollectionUtils.isEmpty(copyList)) {
            return new HashMap<>();
        }
        return FunctionUtil.get(() -> doQueryCreateTableSql(doris, copyList), doris, copyList);
    }

    /**
     * 获取所有数据库名称列表，按字母顺序排列
     */
    public static List<String> listDatabase(DorisBaseDto doris) {
        String url = String.format(LIST_DATABASE_URL_TEMPLATE, doris.getHost(), doris.getFePort(), doris.getNsName());
        return FunctionUtil.get(() -> {
            List<String> result = sendRequest(doris, url, GET, LIST_STRING_TYPE_REFERENCE);
            return Optional.ofNullable(result).orElse(new ArrayList<>()).stream()
                    .map(r -> {
                        String[] split = r.split(StrUtil.COLON);
                        return split.length > 1 ? split[1] : r;
                    }).collect(Collectors.toList());
        }, url);
    }

    /**
     * 获取所有表列表，按字母顺序排列
     */
    public static List<String> listTable(DorisBaseDto doris, String databaseName) {
        List<String> databaseList = DorisFeApi.listDatabase(doris);
        Map<String, String> existDbMap = CollStreamUtil.toMap(databaseList, Function.identity(), Function.identity());
        if (!existDbMap.containsKey(databaseName)) {
            return new ArrayList<>();
        }
        String nsName = doris.getNsName();
        String url = String.format(LIST_TABLE_URL_TEMPLATE, doris.getHost(), doris.getFePort(), nsName, databaseName);
        List<String> tableList = FunctionUtil.get(() -> sendRequest(doris, url, GET, LIST_STRING_TYPE_REFERENCE), url);
        return Optional.ofNullable(tableList).orElse(new ArrayList<>());
    }

    /**
     * 获取不存在的表
     */
    public static List<DatabaseTableDto> listNotExistTable(DorisBaseDto doris, List<DatabaseTableDto> list) {
        List<DatabaseTableDto> allNotExistTableList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<String, List<DatabaseTableDto>> databaseGroupMap = CollStreamUtil.groupBy(list,
                DatabaseTableDto::getDatabaseName, Collectors.toList());
        // 获取库下所有表，在判断表是否存在
        databaseGroupMap.forEach((databaseName, tableList) -> {
            HashSet<String> allTableSet = new HashSet<>(DorisFeApi.listTable(doris, databaseName));
            List<DatabaseTableDto> notExistTableList = tableList.stream()
                    .filter(table -> !allTableSet.contains(table.getTableName()))
                    .collect(Collectors.toList());
            allNotExistTableList.addAll(notExistTableList);
        });
        return allNotExistTableList;
    }

    private static Map<DatabaseTableDto, String> doQueryCreateTableSql(DorisBaseDto doris,
            List<DatabaseTableDto> dbTableList) {
        Map<String, List<DatabaseTableDto>> duplicateMap = CollStreamUtil.groupBy(dbTableList,
                DatabaseTableDto::getTableName, Collectors.toList());
        // 当有重复的表名时，拆分重复表名分批查询
        Map<DatabaseTableDto, String> resultMap = new HashMap<>();
        while (!duplicateMap.isEmpty()) {
            List<DatabaseTableDto> batchTableList = new ArrayList<>();
            Iterator<Map.Entry<String, List<DatabaseTableDto>>> iterator = duplicateMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<DatabaseTableDto>> entry = iterator.next();
                List<DatabaseTableDto> tableList = entry.getValue();
                if (!CollectionUtils.isEmpty(tableList)) {
                    batchTableList.add(tableList.remove(0));
                }
                if (CollectionUtils.isEmpty(tableList)) {
                    iterator.remove();
                }
            }
            resultMap.putAll(requestAndParseCreateTableSql(doris, batchTableList));
        }
        return resultMap;
    }

    private static Map<DatabaseTableDto, String> requestAndParseCreateTableSql(DorisBaseDto doris,
            List<DatabaseTableDto> batchTableList) {
        String createSqlResult = sendQueryCreateTableSqlRequest(doris, batchTableList);
        if (!StringUtils.hasText(createSqlResult)) {
            return new HashMap<>();
        }
        return parseQueryCreateTableSqlResult(batchTableList, createSqlResult);
    }

    private static Map<DatabaseTableDto, String> parseQueryCreateTableSqlResult(List<DatabaseTableDto> list,
            String result) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(result + StrUtil.LF);
        List<String> createSqlList = new ArrayList<>();
        while (matcher.find()) {
            createSqlList.add(matcher.group(1));
        }

        String dorisEscapeCharacter = DataBaseTypeEnum.DORIS.getEscapeCharacter();
        // key: tableName, value: createTableSql
        Map<String, String> createTableSqlMap = createSqlList.stream().collect(toMap(createTableSql -> {
            int startIndex = createTableSql.indexOf(dorisEscapeCharacter);
            int endIndex = createTableSql.indexOf(dorisEscapeCharacter, startIndex + 1);
            return createTableSql.substring(startIndex + 1, endIndex);
        }, Function.identity(), (v1, v2) -> v1));
        return CollStreamUtil.toMap(list, Function.identity(), dto -> createTableSqlMap.get(dto.getTableName()));
    }

    private static String sendQueryCreateTableSqlRequest(DorisBaseDto doris, List<DatabaseTableDto> list) {
        String sql = getQuerySql(list);
        String url = String.format(QUERY_SCHEMA_URL_TEMPLATE, doris.getHost(), doris.getFePort(), doris.getNsName(),
                INFORMATION_SCHEMA_DATABASE);
        // 请求函数
        Callable<String> requestCallable = () -> {
            HttpEntity<?> httpEntity = instanceHttpEntity(doris, sql, MediaType.TEXT_PLAIN);
            return REST_TEMPLATE.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
        };
        // 验证函数
        Consumer<String> valid = r -> checkArgument(StringUtils.hasText(r) && !r.startsWith(ERROR_RESPONSE),
                "请求出现错误，请求结果：" + r);
        String result = FunctionUtil.getAndValid(requestCallable, valid, url, sql);
        log.info("表创建语句请求结果(前1000个字符)：{}", StrUtil.subWithLength(result, 0, 1000));
        return result;
    }

    private static String getQuerySql(List<DatabaseTableDto> databaseTableList) {
        return SELECT_FROM_SQL + databaseTableList.stream().map(dto -> {
            String databaseName = DataBaseTypeEnum.DORIS.wrappedEscapeCharacter(dto.getDatabaseName());
            String tableName = DataBaseTypeEnum.DORIS.wrappedEscapeCharacter(dto.getTableName());
            return databaseName + StrUtil.DOT + tableName;
        }).collect(Collectors.joining(StrUtil.COMMA));
    }

    private static <T> T sendRequest(DorisBaseDto doris, String url, HttpMethod method,
            ParameterizedTypeReference<DorisHttpResponse<T>> typeReference) {
        DorisHttpResponse<T> dorisHttpResponse = sendRequestGetDorisResponse(doris, url, method, typeReference);
        if (dorisHttpResponse == null) {
            return null;
        }
        if (dorisHttpResponse.isSuccess()) {
            return dorisHttpResponse.getData();
        }
        throw new BusinessException("请求内容报错, response: " + dorisHttpResponse);
    }

    private static <T> DorisHttpResponse<T> sendRequestGetDorisResponse(DorisBaseDto doris, String url,
            HttpMethod method, ParameterizedTypeReference<DorisHttpResponse<T>> typeReference) {
        HttpEntity<?> httpEntity = instanceHttpEntity(doris);
        ResponseEntity<DorisHttpResponse<T>> response = REST_TEMPLATE.exchange(url, method, httpEntity, typeReference);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new BusinessException("请求响应失败, response: " + response);
    }

    private static HttpEntity<?> instanceHttpEntity(DorisBaseDto doris) {
        return instanceHttpEntity(doris, null, null);
    }

    private static HttpEntity<?> instanceHttpEntity(DorisBaseDto doris, Object entity, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        if (mediaType == null) {
            headers.setContentType(mediaType);
        }
        headers.setBasicAuth(basicAuth(doris.getUsername(), doris.getPassword()));
        return new HttpEntity<>(entity, headers);
    }

    private static String basicAuth(String username, String password) {
        final String tobeEncode = username + COLON + password;
        byte[] encoded = Base64Utils.encode(tobeEncode.getBytes(StandardCharsets.UTF_8));
        return new String(encoded);
    }
}
