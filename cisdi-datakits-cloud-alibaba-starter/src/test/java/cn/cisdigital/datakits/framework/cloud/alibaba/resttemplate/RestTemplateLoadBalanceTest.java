package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate;

import cn.cisdigital.datakits.framework.cloud.alibaba.BaseIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author xxx
 * @since 2024-05-24
 */
@Slf4j
class RestTemplateLoadBalanceTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Test
    void success_call_ip_address() {
        Assertions.assertDoesNotThrow(() -> {
            restTemplate.getForObject(
                "http://10.106.251.214:8040/cpu",
                String.class);
        });
    }

    @Test
    void success_call_service_name() throws InterruptedException {
        Assertions.assertDoesNotThrow(() -> {
            Thread.sleep(5000);
            restTemplate.getForObject("http://test-cloud-application/innerApi/mock", String.class);
        });
    }

    @Test
    void success_call_domain() throws InterruptedException {
        Assertions.assertDoesNotThrow(() -> {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(
                "https://www.baidu.com",
                HttpMethod.GET,
                entity,
                String.class
            );
            log.info("res: {}", res.getBody());
        });
    }
}
