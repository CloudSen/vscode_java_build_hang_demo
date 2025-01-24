package cn.cisdigital.datakits.framework.web.mvc.filter;

import cn.cisdigital.datakits.framework.common.constant.HeaderConstants;
import cn.cisdigital.datakits.framework.common.identity.UserIdentity;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 为所有请求添加request-id
 *
 * @implNote 确保次过滤器位与第2个
 * @author xxx
 * @since 2022-09-30
 */
@Order(2)
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "logMdcFilter")
@Configuration
public class LogMdcFilter implements Filter {

    static {
        log.info(WebConstants.LOADING_MDC_FILTER);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // 不需要操作
    }

    @Override
    public void destroy() {
        // 不需要操作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String requestId = getRequestId((HttpServletRequest) request);
        MDC.put(HeaderConstants.HEADER_REQUEST_ID, requestId);
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setHeader(HeaderConstants.HEADER_REQUEST_ID, requestId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(HeaderConstants.HEADER_REQUEST_ID);
            log.debug("清除request-id");
        }
    }

    private String getRequestId(HttpServletRequest request) {
        String existsReqId = request.getHeader(HeaderConstants.HEADER_REQUEST_ID);
        if (CharSequenceUtil.isBlank(existsReqId)) {
            log.debug("生成request-id");
            return UserIdentity.generateRequestId();
        }
        log.debug("复用request-id");
        return existsReqId;
    }
}
