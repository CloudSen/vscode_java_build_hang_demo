package cn.cisdigital.datakits.framework.dynamic.datasource.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.cisdigital.datakits.framework.dynamic.datasource.BaseIntegrationTest;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.client.RestTemplate;

/**
 * @author xxx
 * @since 2024-05-08
 */
class DbExceptionMsgTranslatorTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Test
    void success_get_doris_zh_CN_msg() {
        String msg = DbExceptionMsgTranslator.getMsg(DataBaseTypeEnum.DORIS, "1005");
        assertEquals("创建表失败", msg);
    }

    @Test
    void success_get_doris_en_US_msg() throws ExecutionException, InterruptedException {
        AtomicBoolean success = new AtomicBoolean(true);
        Future<?> submit = Executors.newFixedThreadPool(1)
            .submit(() -> {
                try {
                    LocaleContextHolder.setLocale(new Locale("en_US"));
                    String msg = DbExceptionMsgTranslator.getMsg(DataBaseTypeEnum.DORIS, "1005");
                    assertEquals("Failed to create table", msg);
                } catch (Exception e) {
                    success.set(false);
                }
            });
        submit.get();
        Assertions.assertTrue(success.get());
    }

    @Test
    void success_parse_remote_url() {
        String errorDetail = "SQL 错误 [5025] [HY000]: Insert has filtered data in strict mode, tracking_url=http://10.106.251.120:8040/api/_load_error_log?file=__shard_15/error_log_insert_stmt_4840392074164db2-9c70f1296b87a88a_4840392074164db2_9c70f1296b87a88a";
        Optional<String> remoteUrl = DbExceptionMsgTranslator.getRemoteUrl(errorDetail);
        Assertions.assertTrue(remoteUrl.isPresent());
        Assertions.assertEquals(
            "http://10.106.251.120:8040/api/_load_error_log?file=__shard_15/error_log_insert_stmt_4840392074164db2-9c70f1296b87a88a_4840392074164db2_9c70f1296b87a88a",
            remoteUrl.get());
    }

    @Test
    void success_get_doris_remote_msg() {
        String errorDetail = "SQL 错误 [5025] [HY000]: Insert has filtered data in strict mode, tracking_url=http://10.106.251.120:8040/api/_load_error_log?file=__shard_15/error_log_insert_stmt_4840392074164db2-9c70f1296b87a88a_4840392074164db2_9c70f1296b87a88a";
        Optional<String> remoteMsg = DbExceptionMsgTranslator.getRemoteMsgIfExists(errorDetail, restTemplate);
        Assertions.assertTrue(remoteMsg.isPresent());
        System.out.println("Doris远程错误信息：\n" + remoteMsg);
    }
}
