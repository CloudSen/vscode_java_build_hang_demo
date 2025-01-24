package integration.algorithm;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoProvider;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5.Md5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class Md5CryptoTest extends BaseIntegrationTest {

    @Autowired
    private CryptoProvider cryptoProvider;

    @Test
    public void testMd5WithoutSalt() {
        CryptoService md5Service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.MD5);
            CryptoConfig config = new Md5CryptoConfig().setRandomSalt(false);
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isEqualTo("098f6bcd4621d373cade4e832627b4f6");

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result).isEqualTo(result2);
    }

    @Test
    public void testMd5WithRandomSalt() {
        CryptoService md5Service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.MD5);
            CryptoConfig config = new Md5CryptoConfig().setRandomSalt(true).setSaltSize(8);
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isNotNull();

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isNotEqualTo(result2);
    }

    @Test
    public void testMd5WithFixSalt() {
        CryptoService md5Service = cryptoProvider.getCryptoServiceOf(CryptoTypeEnum.MD5);
            CryptoConfig config = new Md5CryptoConfig()
                    .setRandomSalt(false)
                    .setSalt("123");
            String result = md5Service.encrypt(config, "test");
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("cc03e747a6afbbcbf8be7668acfebee5");

            String result2 = md5Service.encrypt(config, "test");
            assertThat(result2).isNotNull();
            assertThat(result).isEqualTo(result2);
    }
}
