package cn.cisdigital.datakits.framework.web.mvc.filter;

import cn.cisdigital.datakits.framework.common.identity.AuthHolder;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.cisdigital.datakits.framework.web.exception.AuthErrorCode;
import cn.cisdigital.datakits.framework.web.exception.AuthException;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 认证过滤器
 * <p>
 * 解析保存用户标识相关的请求头信息, 并在请求结束后进行销毁
 * </p>
 * <p>
 * 确保此过滤器是第3个注册的(需要用到MDC的request-id), 确保请求结束后是最后一个清除用户标识信息的过滤器
 * </p>
 *
 * @author xxx
 * @implNote 只对前端请求即/api相关的接口进行认证
 * @implNote 认证成功的条件是：请求头中的employeeId、appId不能同时为空
 * @since 2024-03-10
 */
@Order(3)
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "userIdentityHeaderFilter")
@Configuration
public class AuthFilter implements Filter {

    @Value("${server.servlet.context-path}")
    public String contextPath;

    static {
        log.info(WebConstants.LOADING_HEADER_FILTER);
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

        log.debug("获取认证信息");
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String requestURI = httpServletRequest.getRequestURI();
            // 所有请求都从header中读取身份信息
            AuthHolder.initUserIdentityFromRequest(httpServletRequest);
            // 只有前端请求才需要认证
            boolean isUnauthorizedRequest = requestURI.startsWith(contextPath + WebConstants.API_PREFIX)
                && AuthHolder.isInvalid();
            if (isUnauthorizedRequest) {
                throw new AuthException(AuthErrorCode.INVALID_AUTH);
            }
            chain.doFilter(request, response);
        } finally {
            AuthHolder.clear();
            log.debug("清除认证信息");
        }
    }
}
