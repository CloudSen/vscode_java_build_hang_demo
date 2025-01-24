package cn.cisdigital.datakits.framework.web.mvc.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * filter层异常处理
 * <p>
 * 将filter层抛出的异常转移到spring统一异常处理中
 * </p>
 *
 * @author xxx
 * @implNote 确保此过滤器是第1个注册的
 * @since 2024-03-07
 */
@Order(1)
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "exceptionFilter")
@Configuration
public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            //            // 传递异常信息
            servletRequest.setAttribute("filterError", e.getMessage());
            // 指定处理该请求的处理器
            servletRequest.getRequestDispatcher("/filter/error").forward(servletRequest, servletResponse);
        }
    }
}
