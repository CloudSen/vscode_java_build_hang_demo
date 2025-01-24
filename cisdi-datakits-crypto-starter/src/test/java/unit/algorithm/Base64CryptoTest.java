package unit.algorithm;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64CryptoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    @Test
    public void testBase64EncodeToString() {
        runner.run(context -> {
            Base64CryptoServiceImpl base64Service = context.getBean(Base64CryptoServiceImpl.class);
            CryptoConfig config = new Base64CryptoConfig();
            String result = base64Service.encrypt(config, "test");
            assertThat(result).isEqualTo("dGVzdA==");

            String result2 = base64Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testBase64DecodeToString() {

        runner.run(context -> {
            Base64CryptoServiceImpl base64Service = context.getBean(Base64CryptoServiceImpl.class);
            CryptoConfig config = new Base64CryptoConfig();
            String result = base64Service.decrypt(config, "dGVzdA==");
            assertThat(result).isEqualTo("test");

            String result2 = base64Service.decrypt(config, "dGVzdA==");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testBase64EncodeToBytes() {
        runner.run(context -> {
            Base64CryptoServiceImpl base64Service = context.getBean(Base64CryptoServiceImpl.class);
            CryptoConfig config = new Base64CryptoConfig();
            byte[] result = base64Service.encryptToBytes(config, "test");
            assertThat(result).isEqualTo("dGVzdA==".getBytes(StandardCharsets.UTF_8));

            byte[] result2 = base64Service.encryptToBytes(config, "test");
            assertThat(result).isEqualTo(result2);
        });
    }

    @Test
    public void testBase64DecodeToBytes() {
        runner.run(context -> {
            Base64CryptoServiceImpl base64Service = context.getBean(Base64CryptoServiceImpl.class);
            CryptoConfig config = new Base64CryptoConfig();
            byte[] result = base64Service.decryptToBytes(config, "dGVzdA==");
            assertThat(result).isEqualTo("test".getBytes(StandardCharsets.UTF_8));

            byte[] result2 = base64Service.decryptToBytes(config, "dGVzdA==");
            assertThat(result).isEqualTo(result2);
        });
    }
}
