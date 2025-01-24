package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256.Sha256CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256.Sha256CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class Sha256CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testSha256WithoutSalt() {
        runner.run(context -> {
            Sha256CryptoServiceImpl sha256Service = context.getBean(Sha256CryptoServiceImpl.class);
            CryptoConfig config = new Sha256CryptoConfig().setRandomSalt(false);
            String result = sha256Service.encrypt(config, "test");
            assertThat(result).isEqualTo("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");

            String result2 = sha256Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testSha256WithRandomSalt() {
        runner.run(context -> {
            Sha256CryptoServiceImpl sha256Service = context.getBean(Sha256CryptoServiceImpl.class);
            CryptoConfig config = new Sha256CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = sha256Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = sha256Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testSha256WithFixSalt() {
        runner.run(context -> {
            Sha256CryptoServiceImpl sha256Service = context.getBean(Sha256CryptoServiceImpl.class);
            CryptoConfig config = new Sha256CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = sha256Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae");

            String result2 = sha256Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }
}
