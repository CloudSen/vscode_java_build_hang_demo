package cn.cisdigital.datakits.framework.web.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.cisdigital.datakits.framework.web.WebConfigProperties;
import cn.cisdigital.datakits.framework.web.mvc.filter.XssAndSqlFilter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author xxx
 * @since 2022-09-19
 */
class XssAndSqlFilterTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(XssAndSqlFilter.class))
        .withUserConfiguration(WebConfigProperties.class)
        .withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true");

    @Test
    @DisplayName("加载配置")
    void load() {
        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true")
            .run(context -> assertDoesNotThrow(() -> context.getBean(XssAndSqlFilter.class)));
    }

    @Test
    @DisplayName("不加载配置")
    void notLoad() {
        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=false")
            .run(context -> assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(XssAndSqlFilter.class)));
    }

    @Test
    @DisplayName("GET拒绝sql注入请求")
    void canNotAccessGet() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/sql-injection-get");
        request.setParameter("name", " or 1=1;");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true")
            .run(context -> {
                Assertions.assertDoesNotThrow(() -> {
                    XssAndSqlFilter filter = context.getBean(XssAndSqlFilter.class);
                    assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
                    assertEquals("application/json;charset=UTF-8", response.getContentType());
                    assertNotNull(response.getContentAsByteArray());
                    String res = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
                    assertTrue(res.contains("非法参数"));
                });
            });
    }

    @Test
    @DisplayName("POST拒绝sql注入请求")
    void canNotAccessPost() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/sql-injection-post");
        request.setContent("{'name':' or 1=1;'}".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true")
            .run(context -> {
                Assertions.assertDoesNotThrow(() -> {
                    XssAndSqlFilter filter = context.getBean(XssAndSqlFilter.class);
                    assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
                    assertEquals("application/json;charset=UTF-8", response.getContentType());
                    assertNotNull(response.getContentAsByteArray());
                    String res = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
                    assertTrue(res.contains("非法参数"));
                });
            });
    }

    @Test
    @DisplayName("POST拒绝sql注入请求")
    void canNotAccessPost2() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/sql-injection-post");
        String json = "{ \n"
            + "    \"id\": \"4\", \n"
            + "    \"template\": \"import java.io.IOException;\\r\\n\\r\\n/**\\r\\n* 任务名称：${taskName}\\r\\n* 功能描述：${taskDesc}\\r\\n* 运行频率：每 N 分钟/小时/天 一次\\r\\n* 输入表：ods_table1,ods_table2\\r\\n* 输出表：dwd_table1\\r\\n* 加载策略：3 （注：1-全表覆盖 ，2-增量更新，3-增量追加）\\r\\n* 关键字说明：XXXX\\r\\n* 创建时间：${createTime}\\r\\n* 创建人：${author}\\r\\n* 修改历史：\\r\\n*  版本          更改日期          更改人             修改说明\\r\\n*\\r\\n*/\\r\\npublic class HelloWorld{\\r\\n     public static void main(String[] args) throws IOException{\\r\\n     StringBuilder builder = new StringBuilder(\\\"Hello:\\\");\\r\\n     for (String arg:args) {\\r\\n        builder.append(arg).append(\\\" \\\");\\r\\n     }\\r\\n     System.out.println(builder);\\r\\n  }\\r\\n}\" \n"
            + "}";
        request.setContent(json.getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true")
            .run(context -> {
                Assertions.assertDoesNotThrow(() -> {
                    XssAndSqlFilter filter = context.getBean(XssAndSqlFilter.class);
                    assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
                    assertNull(response.getContentType());
                    assertNotNull(response.getContentAsByteArray());
                    System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
                });
            });
    }

    @Test
    @DisplayName("合法请求")
    void canAccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/no-sql-injection");
        request.setParameter("name", "clouds3n");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        this.runner.withPropertyValues("datakits.default-config.web.mvc.xss-sql-filter-enabled=true")
            .run(context -> {
                Assertions.assertDoesNotThrow(() -> {
                    XssAndSqlFilter filter = context.getBean(XssAndSqlFilter.class);
                    assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
                    assertNull(response.getContentType());
                    assertNotNull(response.getContentAsByteArray());
                    System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
                });
            });
    }
}
