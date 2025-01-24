package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512.Sha512CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512.Sha512CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class Sha512CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testSha512WithoutSalt() {
        runner.run(context -> {
            Sha512CryptoServiceImpl sha512Service = context.getBean(Sha512CryptoServiceImpl.class);
            CryptoConfig config = new Sha512CryptoConfig().setRandomSalt(false);
            String result = sha512Service.encrypt(config, "test");
            assertThat(result).isEqualTo("ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff");

            String result2 = sha512Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testSha512WithRandomSalt() {
        runner.run(context -> {
            Sha512CryptoServiceImpl sha512Service = context.getBean(Sha512CryptoServiceImpl.class);
            CryptoConfig config = new Sha512CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = sha512Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = sha512Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testSha512WithFixSalt() {
        runner.run(context -> {
            Sha512CryptoServiceImpl sha512Service = context.getBean(Sha512CryptoServiceImpl.class);
            CryptoConfig config = new Sha512CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = sha512Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("daef4953b9783365cad6615223720506cc46c5167cd16ab500fa597aa08ff964eb24fb19687f34d7665f778fcb6c5358fc0a5b81e1662cf90f73a2671c53f991");

            String result2 = sha512Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }
}
