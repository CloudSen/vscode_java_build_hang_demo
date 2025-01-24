package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.common.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author xxx
 * @implNote 语言切换必须在新线程中
 * @since 2024-03-06
 */
@Slf4j
class I18nUtilsTest extends BaseIntegrationTest {

    @Test
    void read_zh_CN() {
        String message = I18nUtils.getMessage("common.exception.internal_error");
        log.info(message);
        Assertions.assertEquals("系统未知错误", message);
    }

    @Test
    void read_en_US() throws ExecutionException, InterruptedException {
        AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> {
                try {
                    LocaleContextHolder.setLocale(new Locale("en_US"));
                    String message = I18nUtils.getMessage("common.exception.internal_error");
                    log.info(message);
                    Assertions.assertEquals("Unknown system error", message);
                } catch (Exception e) {
                    success.set(false);
                }
            });
        submit.get();
        Assertions.assertTrue(success.get());
    }

    @Test
    void read_ru_RU() throws ExecutionException, InterruptedException {
        AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> Assertions.assertDoesNotThrow(() -> {
                LocaleContextHolder.setLocale(new Locale("ru_RU"));
                String message = I18nUtils.getMessage("common.exception.internal_error");
                log.info(message);
                Assertions.assertEquals("Неизвестная системная ошибка", message);
            }));
        submit.get();
        Assertions.assertTrue(success.get());
    }
}
