package unit.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5.HmacMd5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5.HmacMd5CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HmacMd5CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void fail_HmacMd5WithoutKey() {
        assertThrows(CryptoException.class, () -> {
            runner.run(context -> {
                CryptoService cryptoService = context.getBean(HmacMd5CryptoServiceImpl.class);
                CryptoConfig config = new HmacMd5CryptoConfig().setRandomSalt(false);
                cryptoService.encrypt(config, "test");
            });
        });
    }

    @Test
    public void testHmacMd5WithRandomSalt() {
        runner.run(context -> {
            CryptoService cryptoService = context.getBean(HmacMd5CryptoServiceImpl.class);
            CryptoConfig config = new HmacMd5CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = cryptoService.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = cryptoService.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
        });
    }

    @Test
    public void testHmacMd5WithFixSalt() {
        runner.run(context -> {
            CryptoService cryptoService = context.getBean(HmacMd5CryptoServiceImpl.class);
            CryptoConfig config = new HmacMd5CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = cryptoService.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("f6437e9c0344104be7ed4ecf156d3511");

            String result2 = cryptoService.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
        });
    }

}
