package cn.cisdigital.datakits.framework.common.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.cisdigital.datakits.framework.common.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.model.util.SpringContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

/**
 * @author xxx
 * @since 2022-09-16
 */
class SpringContextTest extends BaseIntegrationTest {

    @Test
    @DisplayName("通过Class类获取bean")
    void getBeanByClass() {
        assertDoesNotThrow(() -> {
            SpringContext bean = SpringContext.getBean(SpringContext.class);
            assertNotNull(bean);
        });
    }

    @Test
    @DisplayName("通过String类名获取bean")
    void getBeanByName() {
        assertDoesNotThrow(() -> {
            Object bean = SpringContext.getBean("springContext");
            assertNotNull(bean);
            assertEquals(SpringContext.class, bean.getClass());
        });
    }

    @Test
    @DisplayName("获取上下文")
    void getContext() {
        assertDoesNotThrow(() -> {
            ApplicationContext context = SpringContext.getContext();
            assertNotNull(context);
        });
    }

}
