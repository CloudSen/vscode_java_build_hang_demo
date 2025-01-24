package cn.cisdigital.datakits.framework.web.i18ntest;

import cn.cisdigital.datakits.framework.web.BaseIntegrationTest;
import java.util.Locale;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author xxx
 * @since 2024-03-05
 */
@Slf4j
class LogI18nTest extends BaseIntegrationTest {

    @Test
    @DisplayName("打印默认语言日志")
    void success_log_zh_CN() {
        log.info("common.exception.internal_error");
    }

    @Test
    @DisplayName("打印默认语言带参数的日志")
    void success_log_zh_CN_with_param() {
        log.info("common.log.message_with_param", "p1", "p2");
    }

    @Test
    @DisplayName("打印没有国际化的日志")
    void success_log_without_i18n() {
        log.info("没有国际化的日志");
    }

    @Test
    @DisplayName("打印默认是英文语言的日志")
    void success_log_en_US() {
        Executors.newFixedThreadPool(1)
            .execute(() -> {
                LocaleContextHolder.setLocale(new Locale("en_US"));
                log.info("common.exception.internal_error");
            });

    }

    @Test
    @DisplayName("打印默认是俄语的日志")
    void success_log_ru_RU() {
        Executors.newFixedThreadPool(1)
            .execute(() -> {
                LocaleContextHolder.setLocale(new Locale("ru_RU"));
                log.info("common.exception.internal_error");
            });

    }


}
