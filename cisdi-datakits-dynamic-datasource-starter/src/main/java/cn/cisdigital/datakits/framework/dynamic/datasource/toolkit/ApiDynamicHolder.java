package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;

import cn.cisdigital.datakits.framework.model.dto.database.TestResultDto;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xxx
 */
@Slf4j
public class ApiDynamicHolder {

    private final static String GET = "GET";

    public static TestResultDto testConnection(String testUrl, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(testUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            // 超时设置5秒
            connection.setConnectTimeout(timeout);
            connection.connect();
            return new TestResultDto(true, null);
        } catch (IOException e) {
            log.error("url 连接测试失败", e);
            return new TestResultDto(false, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
