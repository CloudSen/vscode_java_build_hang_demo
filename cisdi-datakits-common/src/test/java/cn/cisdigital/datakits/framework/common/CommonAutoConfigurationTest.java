package cn.cisdigital.datakits.framework.common;

import cn.cisdigital.datakits.framework.model.util.SpringContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * @author xxx
 * @since 2024-03-05
 */
class CommonAutoConfigurationTest {

    @Test
    void success_load() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonAutoConfiguration.class))
            .run(context -> {
                Assertions.assertThat(context).hasSingleBean(CommonAutoConfiguration.class);
                Assertions.assertThat(context).hasSingleBean(SpringContext.class);
            });
    }
}
