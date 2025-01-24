package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoProvider;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5.HmacMd5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HmacMd5CryptoTest extends BaseIntegrationTest {

    @Autowired
    private CryptoProvider cryptoProvider;

    @Test
    public void fail_HmacMd5WithoutKey() {
        assertThrows(CryptoException.class, () -> {
            CryptoService cryptoService = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.HmacMD5);
            CryptoConfig config = new HmacMd5CryptoConfig().setRandomSalt(false);
            cryptoService.encrypt(config, "test");
        });
    }

    @Test
    public void testHmacMd5WithRandomSalt() {
        CryptoService cryptoService = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.HmacMD5);
        CryptoConfig config = new HmacMd5CryptoConfig().setRandomSalt(true).setSaltSize(8);
        String result = cryptoService.encrypt(config, "test");
        assertThat(result).isNotNull();

        String result2 = cryptoService.encrypt(config, "test");
        assertThat(result2).isNotNull();
        assertThat(result).isNotEqualTo(result2);
    }

    @Test
    public void testHmacMd5WithFixSalt() {
        CryptoService cryptoService = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.HmacMD5);
        CryptoConfig config = new HmacMd5CryptoConfig()
                .setRandomSalt(false)
                .setSalt("123");
        String result = cryptoService.encrypt(config, "test");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("f6437e9c0344104be7ed4ecf156d3511");

        String result2 = cryptoService.encrypt(config, "test");
        assertThat(result2).isNotNull();
        assertThat(result).isEqualTo(result2);
    }
}
