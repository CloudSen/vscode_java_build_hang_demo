package cn.cisdigital.datakits.framework.web.mvc.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 表示这是自定义拦截器
 *
 * @author xxx
 * @since 2022-09-29
 */
public interface CustomInterceptor extends HandlerInterceptor {

    /**
     * 排除的路由匹配模式
     *
     * @return 模式数组
     */
    default String[] excludePathPatterns() {
        return new String[]{
            "/*/*.html",
            "/*/*.css",
            "/*/*.js",
            "/*/*.png",
            "/*/*.xml",
            "/*/*.json",
            "/*/*.yaml",
            "/*/*.yml",
            "/swagger*/**"
        };
    }

    /**
     * 路由匹配模式
     *
     * @return 模式数组
     */
    default String[] pathPatterns() {
        return new String[]{"/**"};
    }
}
