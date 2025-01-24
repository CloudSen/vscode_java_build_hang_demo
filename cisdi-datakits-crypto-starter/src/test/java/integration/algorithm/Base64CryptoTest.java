package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoProvider;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64CryptoTest extends BaseIntegrationTest {

    @Autowired
    private CryptoProvider cryptoProvider;

    @Test
    public void testBase64EncodeToString() {
        CryptoService service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.BASE64);
        CryptoConfig config = new Base64CryptoConfig();
        String result = service.encrypt(config, "test");
        assertThat(result).isEqualTo("dGVzdA==");

        String result2 = service.encrypt(config, "test");
        assertThat(result).isEqualTo(result2);
    }

    @Test
    public void testBase64DecodeToString() {
        CryptoService service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.BASE64);
            CryptoConfig config = new Base64CryptoConfig();
        String result = service.decrypt(config, "dGVzdA==");
            assertThat(result).isEqualTo("test");

        String result2 = service.decrypt(config, "dGVzdA==");
            assertThat(result).isEqualTo(result2);
    }

    @Test
    public void testBase64EncodeToBytes() {
        CryptoService service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.BASE64);
            CryptoConfig config = new Base64CryptoConfig();
        byte[] result = service.encryptToBytes(config, "test");
            assertThat(result).isEqualTo("dGVzdA==".getBytes(StandardCharsets.UTF_8));

        byte[] result2 = service.encryptToBytes(config, "test");
            assertThat(result).isEqualTo(result2);
    }

    @Test
    public void testBase64DecodeToBytes() {
        CryptoService service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.BASE64);
            CryptoConfig config = new Base64CryptoConfig();
        byte[] result = service.decryptToBytes(config, "dGVzdA==");
            assertThat(result).isEqualTo("test".getBytes(StandardCharsets.UTF_8));

        byte[] result2 = service.decryptToBytes(config, "dGVzdA==");
            assertThat(result).isEqualTo(result2);
    }
}
