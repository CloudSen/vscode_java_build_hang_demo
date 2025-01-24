package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha1.Sha1CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha1.Sha1CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class Sha1CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testSha1WithoutSalt() {
        runner.run(context -> {
            Sha1CryptoServiceImpl sha1Service = context.getBean(Sha1CryptoServiceImpl.class);
            CryptoConfig config = new Sha1CryptoConfig().setRandomSalt(false);
            String result = sha1Service.encrypt(config, "test");
            assertThat(result).isEqualTo("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");

            String result2 = sha1Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testSha1WithRandomSalt() {
        runner.run(context -> {
            Sha1CryptoServiceImpl sha1Service = context.getBean(Sha1CryptoServiceImpl.class);
            CryptoConfig config = new Sha1CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = sha1Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = sha1Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testSha1WithFixSalt() {
        runner.run(context -> {
            Sha1CryptoServiceImpl sha1Service = context.getBean(Sha1CryptoServiceImpl.class);
            CryptoConfig config = new Sha1CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = sha1Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("7288edd0fc3ffcbe93a0cf06e3568e28521687bc");

            String result2 = sha1Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }
}
