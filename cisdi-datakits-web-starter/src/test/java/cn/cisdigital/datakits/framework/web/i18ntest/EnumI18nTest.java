package cn.cisdigital.datakits.framework.web.i18ntest;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import cn.cisdigital.datakits.framework.web.BaseIntegrationTest;
import java.util.Locale;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author xxx
 * @since 2024-03-05
 */
class EnumI18nTest extends BaseIntegrationTest {

    @Test
    void success_get_zh_CN_desc() {
        Executors.newFixedThreadPool(1)
            .execute(() -> {
                Assertions.assertEquals("枚举A", TestEnum.A.getDesc());
                Assertions.assertEquals("枚举B", TestEnum.B.getDesc());
                Assertions.assertEquals("枚举C 参数 p1 参数 p2", TestEnum.C.getDesc(new Object[]{"p1", "p2"}));
            });
    }

    @Test
    void success_get_en_US_desc() {
        Executors.newFixedThreadPool(1)
            .execute(() -> {
                LocaleContextHolder.setLocale(new Locale("en_US"));
                Assertions.assertEquals("Enum Of A", TestEnum.A.getDesc());
                Assertions.assertEquals("Enum Of B", TestEnum.B.getDesc());
                Assertions.assertEquals("Enum Of C param p1 param p2", TestEnum.C.getDesc(new Object[]{"p1", "p2"}));
            });
    }

    @Test
    void success_get_ru_RU_desc() {
        Executors.newFixedThreadPool(1)
            .execute(() -> {
                LocaleContextHolder.setLocale(new Locale("ru_RU"));
                Assertions.assertEquals("перечисление A", TestEnum.A.getDesc());
                Assertions.assertEquals("перечисление B", TestEnum.B.getDesc());
                Assertions.assertEquals("перечисление C параметр p1 параметр p2",
                    TestEnum.C.getDesc(new Object[]{"p1", "p2"}));
            });
    }

    @Getter
    @RequiredArgsConstructor
    private static enum TestEnum implements BaseEnum {

        A(1, "TEST.TestEnum.A"),
        B(2, "TEST.TestEnum.B"),
        C(3, "TEST.TestEnum.C"),
        ;

        private final int code;
        private final String key;
    }
}
