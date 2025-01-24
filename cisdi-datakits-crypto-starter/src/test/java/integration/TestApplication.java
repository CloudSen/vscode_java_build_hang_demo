package integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * @author xxx
 * @since 2024-03-05
 */
@SpringBootApplication(
    scanBasePackages = {"cn.cisdigital.datakits.framework.crypto", "integration"},
    exclude = {ErrorMvcAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
