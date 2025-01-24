package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3.Sm3CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3.Sm3CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class Sm3CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testSm3WithoutSalt() {
        runner.run(context -> {
            Sm3CryptoServiceImpl sm3Service = context.getBean(Sm3CryptoServiceImpl.class);
            CryptoConfig config = new Sm3CryptoConfig().setRandomSalt(false);
            String result = sm3Service.encrypt(config, "test");
            assertThat(result).isEqualTo("55e12e91650d2fec56ec74e1d3e4ddbfce2ef3a65890c2a19ecf88a307e76a23");

            String result2 = sm3Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testSm3WithRandomSalt() {
        runner.run(context -> {
            Sm3CryptoServiceImpl sm3Service = context.getBean(Sm3CryptoServiceImpl.class);
            CryptoConfig config = new Sm3CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = sm3Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = sm3Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testSm3WithFixSalt() {
        runner.run(context -> {
            Sm3CryptoServiceImpl sm3Service = context.getBean(Sm3CryptoServiceImpl.class);
            CryptoConfig config = new Sm3CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = sm3Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("09d3c4e64ed1fe457d91d234fd4def9b19bf4a0382c553c903f6fd538698b0d9");

            String result2 = sm3Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }
}
