package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.cloud.alibaba.TestApplication;
import cn.cisdigital.datakits.framework.model.vo.ResVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author xxx
 * @since 2024-03-19
 */
@SpringBootTest(classes = TestApplication.class)
class FeignClientWrapperTest {

    @Autowired
    private TestFeignClient testFeignClient;

    @Test
    void success_get_data() {
        Optional<Integer> data = FeignClientWrapper.getData(testFeignClient::testSuccessCall);
        assertThat(data).isPresent();
        assertThat(data.get()).isEqualTo(100);
    }

    @Test
    void error_get_data() {
        assertThatThrownBy(() -> FeignClientWrapper.getData(testFeignClient::testErrorCall));
    }

    @Test
    void error_get_null_data() {
        assertThatThrownBy(() -> FeignClientWrapper.getData(testFeignClient::testNullCall));
    }


    @FeignClient(name = "test-application", contextId = "cloud-alibaba-starter-feign")
    private interface TestFeignClient {

        default ResVo<Integer> testSuccessCall() {
            return ResVo.ok(100);
        }

        default ResVo<Integer> testErrorCall() {
            return ResVo.error();
        }

        default ResVo<Integer> testNullCall() {
            return null;
        }
    }
}
