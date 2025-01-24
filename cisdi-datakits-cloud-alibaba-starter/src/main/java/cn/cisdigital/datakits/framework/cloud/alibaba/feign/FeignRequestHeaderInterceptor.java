package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.common.constant.HeaderConstants;
import cn.cisdigital.datakits.framework.common.identity.AuthHolder;
import cn.hutool.core.text.CharSequenceUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Feign调用请求头参数设置
 *
 * @author xxx
 * @implNote 从AuthHolder中获取认证信息
 */
@Slf4j
@Component
public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    static {
        log.info("[ Feign请求拦截器] Header透传已加载.");
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (CharSequenceUtil.isBlank(AuthHolder.getRequestId())) {
            log.warn("[ Feign请求拦截器] 上下文中没有requestId, 可能是来自定时任务、消息消费等纯后端场景");
        } else {
            requestTemplate.header(HeaderConstants.HEADER_REQUEST_ID, AuthHolder.getRequestId());
        }

        if (CharSequenceUtil.isNotBlank(AuthHolder.getUserId())) {
            requestTemplate.header(HeaderConstants.HEADER_USER_ID, AuthHolder.getUserId());
        }
        if (CharSequenceUtil.isNotBlank(AuthHolder.getPartyId())) {
            requestTemplate.header(HeaderConstants.HEADER_PARTY_ID, AuthHolder.getPartyId());
        }
        if (CharSequenceUtil.isNotBlank(AuthHolder.getEmployeeId())) {
            requestTemplate.header(HeaderConstants.HEADER_EMPLOYEE_ID, AuthHolder.getEmployeeId());
        }
        String employeeName = AuthHolder.getEmployeeName();
        if (CharSequenceUtil.isNotBlank(employeeName)) {
            try {
                requestTemplate.header(HeaderConstants.HEADER_EMPLOYEE_NAME, URLEncoder.encode(employeeName,
                    StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
               log.error("cloud-alibaba.exception.employee_name_encode_error");
                requestTemplate.header(HeaderConstants.HEADER_EMPLOYEE_NAME, employeeName);
            }
        }
        if (CharSequenceUtil.isNotBlank(AuthHolder.getAccount())) {
            requestTemplate.header(HeaderConstants.HEADER_ACCOUNT, AuthHolder.getAccount());
        }
        if (CharSequenceUtil.isNotBlank(AuthHolder.getAppId())) {
            requestTemplate.header(HeaderConstants.HEADER_APP_ID, AuthHolder.getAppId());
        }
        requestTemplate.header(HeaderConstants.HEADER_APPLICATION_NAME, applicationName);
        requestTemplate.header(HeaderConstants.HEADER_SUPER_ADMIN, String.valueOf(AuthHolder.isAdmin()));
    }
}
