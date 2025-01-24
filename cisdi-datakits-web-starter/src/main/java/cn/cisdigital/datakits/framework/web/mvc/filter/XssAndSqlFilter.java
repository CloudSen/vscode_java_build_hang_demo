package cn.cisdigital.datakits.framework.web.mvc.filter;

import cn.cisdigital.datakits.framework.model.vo.ResVo;
import cn.cisdigital.datakits.framework.web.WebConfigProperties;
import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * 防止Xss攻击和Sql注入过滤器
 *
 * @author xxx
 */
@Order(3)
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = WebConstants.ENABLE_XSS_SQL_FILTER, havingValue = "true", matchIfMissing = false)
public class XssAndSqlFilter implements Filter {

    private final WebConfigProperties webConfigProperties;

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println( URLEncoder.encode("手动发顺丰", StandardCharsets.UTF_8.name()));;
    }

    static {
        log.info(WebConstants.LOADING_XSS_SQL_FILTER);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // 不需要处理
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
        ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        XssAndSqlHttpServletRequestWrapper xssRequest = new XssAndSqlHttpServletRequestWrapper(
            request);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        boolean needIgnore = webConfigProperties.getMvc() != null
            && CollUtil.isNotEmpty(webConfigProperties.getMvc().getIgnoredUrlForXssSqlFilter())
            && webConfigProperties.getMvc().getIgnoredUrlForXssSqlFilter().contains(requestURI);
        if (needIgnore) {
            log.debug("{}已忽略XSS&SQL-Injection校验", requestURI);
            filterChain.doFilter(xssRequest, servletResponse);
            return;
        }
        String param;
        log.debug("开始XSS&SQL-Injection校验...");
        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            param = this.getBodyString(xssRequest.getReader());
            if (CharSequenceUtil.isNotBlank(param) && xssRequest.checkXssAndSql(
                param)) {
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.setContentType("application/json;charset=UTF-8");
                PrintWriter out = servletResponse.getWriter();
                out.write(new ObjectMapper().writer()
                    .writeValueAsString(ResVo.error("非法参数")));
                return;
            }
        }
        if (xssRequest.checkParameter()) {
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out = servletResponse.getWriter();
            out.write(new ObjectMapper().writer()
                .writeValueAsString(ResVo.error("非法参数")));
            return;
        }
        filterChain.doFilter(xssRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // 不需要处理
    }

    /**
     * 获取request请求body中参数
     *
     * @param br BufferedReader
     * @return body参数
     */
    private String getBodyString(BufferedReader br) throws IOException {
        String inputLine;
        StringBuilder str = new StringBuilder();
        try {
            while ((inputLine = br.readLine()) != null) {
                str.append(inputLine);
            }
        } finally {
            br.close();
        }
        return str.toString();
    }

}
