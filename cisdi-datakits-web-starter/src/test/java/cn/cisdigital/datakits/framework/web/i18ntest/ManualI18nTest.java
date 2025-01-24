package cn.cisdigital.datakits.framework.web.i18ntest;

import cn.cisdigital.datakits.framework.model.util.I18nUtils;
import cn.cisdigital.datakits.framework.web.BaseIntegrationTest;
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
 * @implNote 语言切换必须在新线程中才会生效
 * @since 2024-03-05
 */
@Slf4j
class ManualI18nTest extends BaseIntegrationTest {

    @Test
    void success_read_zh_CN() throws ExecutionException, InterruptedException {
        AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> {
                String message = I18nUtils.getMessage("common.exception.internal_error");
                log.info("message: {}", message);
                Assertions.assertEquals("系统未知错误", message);
            });
        submit.get();
        Assertions.assertTrue(success.get());
    }

    @Test
    void success_read_en_US() throws ExecutionException, InterruptedException {
         AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> {
                LocaleContextHolder.setLocale(new Locale("en_US"));
                String message = I18nUtils.getMessage("common.exception.internal_error");
                log.info("message: {}", message);
                Assertions.assertEquals("Unknown system error", message);
            });
        submit.get();
        Assertions.assertTrue(success.get());
    }

    @Test
    void success_read_ru_RU() throws ExecutionException, InterruptedException {
         AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> {
                LocaleContextHolder.setLocale(new Locale("ru_RU"));
                String message = I18nUtils.getMessage("common.exception.internal_error");
                log.info("message: {}", message);
                Assertions.assertEquals("Неизвестная системная ошибка", message);
            });
        submit.get();
        Assertions.assertTrue(success.get());
    }
}
