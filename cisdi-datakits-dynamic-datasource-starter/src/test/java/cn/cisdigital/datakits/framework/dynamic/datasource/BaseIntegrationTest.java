package cn.cisdigital.datakits.framework.dynamic.datasource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xxx
 * @since 2022-09-16
 */
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @BeforeAll
    static void init() {

    }

    @AfterAll
    static void clean() {

    }
}
