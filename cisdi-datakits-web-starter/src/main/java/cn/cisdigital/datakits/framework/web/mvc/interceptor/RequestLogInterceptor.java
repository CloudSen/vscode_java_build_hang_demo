package cn.cisdigital.datakits.framework.web.mvc.interceptor;

import cn.cisdigital.datakits.framework.common.constant.HeaderConstants;
import cn.cisdigital.datakits.framework.common.identity.AuthHolder;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * 请求日志拦截器
 *
 * @author xxx
 */
@Slf4j
@Component
@SuppressWarnings({"NullableProblems", "RedundantThrows"})
public class RequestLogInterceptor implements CustomInterceptor {

    private static final ThreadLocal<LocalDateTime> REQUEST_TIME = new NamedThreadLocal<>("Request Duration ThreadLocal");
    private static final List<String> NEED_REQ_HEADER_NAMES = CollUtil.addAllIfNotContains(
        HeaderConstants.NECESSARY_HEADERS.stream().map(String::toUpperCase).collect(Collectors.toList()),
        Arrays.asList("CONTENT-TYPE", "USER-AGENT", "HOST", "ACCEPT-ENCODING"));
    private static final List<String> NEED_RES_HEADER_NAMES = Arrays.asList("AUTHORIZATION", "CONTENT-TYPE");

    @Value("${server.servlet.context-path}")
    public String contextPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith(contextPath + WebConstants.FILTER_EXCEPTION_PREFIX)) {
            return true;
        }
        REQUEST_TIME.set(LocalDateTime.now());
        logRequest(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/filter/error")) {
            return;
        }
        StringBuffer headerBuffer = new StringBuffer();
        Collection<String> headerNames = response.getHeaderNames();
        if (CollUtil.isNotEmpty(headerNames)) {
            headerNames
                .stream().filter(n -> NEED_RES_HEADER_NAMES.contains(n.toUpperCase()))
                .forEach(headerName -> {
                    headerBuffer.append("[ 拦截器 ] ")
                        .append(headerName).append(": ");
                    Object headerValue = response.getHeaders(headerName);
                    try {
                        if (HeaderConstants.HEADER_EMPLOYEE_NAME.toUpperCase().equals(headerName)) {
                            headerValue = URLDecoder.decode(response.getHeader(headerName), StandardCharsets.UTF_8.name());
                        }
                    } catch (UnsupportedEncodingException e) {
                        log.error("employeeName解码失败", e);
                    }
                    headerBuffer.append(headerValue).append("\n");
                });
        }
        log.info("[ 拦截器 ] 响应状态: [{}]", response.getStatus());
        log.info("[ 拦截器 ] 响应头: \n{}", headerBuffer);
        LocalDateTime before = REQUEST_TIME.get();
        log.info("[ 拦截器 ] 请求耗时:{} ms", LocalDateTimeUtil.between(before, LocalDateTime.now()).toMillis());
        REQUEST_TIME.remove();
    }

    private void logRequest(HttpServletRequest request) {
        try {
            String queryString = request.getQueryString();
            if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod()) && !CharSequenceUtil.isBlank(queryString)) {
                queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.name());
            }
            StringBuilder headers = new StringBuilder();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (NEED_REQ_HEADER_NAMES.contains(headerName.toUpperCase())) {
                    headers.append("[ 拦截器 ] ")
                        .append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
                }
            }
            log.info("[ 拦截器 ] 请求: [{}] {}", request.getMethod().toUpperCase(), request.getRequestURL());
            log.info("[ 拦截器 ] 请求URL参数: {}", queryString);
            log.info("[ 拦截器 ] 请求用户信息: employeeId={}, appId={}", AuthHolder.getEmployeeId(), AuthHolder.getAppId());
            log.info("[ 拦截器 ] 请求头: \n{}", headers);
        } catch (UnsupportedEncodingException e) {
            log.error(I18nUtils.getMessage("common.log.log_request_error"), e);
        }
    }
}
