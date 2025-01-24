package unit.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5.Md5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5.Md5CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class Md5CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testMd5WithoutSalt() {
        runner.run(context -> {
            Md5CryptoServiceImpl md5Service = context.getBean(Md5CryptoServiceImpl.class);
            CryptoConfig config = new Md5CryptoConfig().setRandomSalt(false);
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isEqualTo("098f6bcd4621d373cade4e832627b4f6");

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testMd5WithRandomSalt() {
        runner.run(context -> {
            Md5CryptoServiceImpl md5Service = context.getBean(Md5CryptoServiceImpl.class);
            CryptoConfig config = new Md5CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testMd5WithFixSalt() {
        runner.run(context -> {
            Md5CryptoServiceImpl md5Service = context.getBean(Md5CryptoServiceImpl.class);
            CryptoConfig config = new Md5CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("cc03e747a6afbbcbf8be7668acfebee5");

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }

}
