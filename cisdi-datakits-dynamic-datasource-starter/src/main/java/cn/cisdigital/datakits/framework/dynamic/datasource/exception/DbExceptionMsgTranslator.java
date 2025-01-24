package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import cn.cisdigital.datakits.framework.common.exception.DorisErrorCode;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * 数据库异常消息翻译
 *
 * @author xxx
 * @implNote 这里暂时不用考虑数据源版本，也不需要抽象，能满足需求就行
 * @since 2024-05-08
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbExceptionMsgTranslator {

    private static final Pattern REMOTE_ERROR_PATTERN = Pattern.compile("tracking_url=([^\\s]+)");

    /**
     * 翻译数据库异常消息
     *
     * @param dbType 数据库类型
     * @param errorCode jdbc错误码
     * @return 翻译后的错误原因，如果没有翻译，返回空字符串
     */
    public static String getMsg(DataBaseTypeEnum dbType, String errorCode) {
        if (dbType == null) {
            return CharSequenceUtil.EMPTY;
        }
        switch (dbType) { // NOSONAR
            case DORIS:
                return DorisErrorCode.getMsgByCode(errorCode);
            default:
                return CharSequenceUtil.EMPTY;
        }
    }

    public static Optional<String> getRemoteUrl(String exceptionDetail) {
        if (CharSequenceUtil.isBlank(exceptionDetail)) {
            return Optional.empty();
        }
        Matcher matcher = REMOTE_ERROR_PATTERN.matcher(exceptionDetail);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.ofNullable(matcher.group(1));
    }

    /**
     * 获取远程错误日志
     *
     * @param exceptionDetail SQL异常详情
     * @param restTemplate restTemplate
     * @param basicAuth basic认证，用于设置header，格式为Base64编码
     */
    public static Optional<String> getRemoteMsgIfExists(
        String exceptionDetail,
        RestTemplate restTemplate,
        String basicAuth) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", basicAuth);
        HttpEntity<String> request = new HttpEntity<>(headers);
        return getRemoteUrl(exceptionDetail)
            .filter(CharSequenceUtil::isNotBlank)
            .map(remoteUrl -> {
                log.info("监测到Doris远程错误URL={}，开始查询...", remoteUrl);
                return restTemplate.exchange(remoteUrl, HttpMethod.GET, request, String.class).getBody();
            });

    }

    /**
     * 获取远程错误日志
     *
     * @param exceptionDetail SQL异常详情
     * @param restTemplate restTemplate
     */
    public static Optional<String> getRemoteMsgIfExists(String exceptionDetail, RestTemplate restTemplate) {
        return getRemoteUrl(exceptionDetail)
            .filter(CharSequenceUtil::isNotBlank)
            .map(remoteUrl -> {
                log.info("监测到Doris远程错误URL={}，开始查询...", remoteUrl);
                return restTemplate.getForObject(remoteUrl, String.class);
            });
    }
}
