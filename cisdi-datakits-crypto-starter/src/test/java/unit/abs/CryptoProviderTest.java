package unit.abs;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoProvider;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5.HmacMd5CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5.Md5CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha1.Sha1CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256.Sha256CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512.Sha512CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3.Sm3CryptoServiceImpl;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CryptoProviderTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    /**
     * 成功管理所有crypto service实现类
     */
    @Test
    @DisplayName("加载所有crypto service bean")
    void success_load_crypto_service() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertThat(provider.getCryptoServiceCount()).isEqualTo(7);
        });
    }

    @Test
    @DisplayName("成功提供base64服务")
    void success_provide_base64() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.BASE64);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Base64CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供MD5服务")
    void success_provide_md5() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.MD5);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Md5CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供SHA1服务")
    void success_provide_sha1() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.SHA1);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Sha1CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供SHA256服务")
    void success_provide_sha256() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.SHA256);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Sha256CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供SHA512服务")
    void success_provide_sha512() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.SHA512);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Sha512CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供SM3服务")
    void success_provide_sm3() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.SM3);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(Sm3CryptoServiceImpl.class);
            });
        });
    }

    @Test
    @DisplayName("成功提供HmacMD5服务")
    void success_provide_hmac_md5() {
        runner.run(context -> {
            CryptoProvider provider = context.getBean(CryptoProvider.class);
            assertThat(provider).isNotNull();
            assertDoesNotThrow(() -> {
                CryptoService service = provider.getCryptoServiceOf(CryptoTypeEnum.HmacMD5);
                assertThat(service).isNotNull();
                assertThat(service).isInstanceOf(HmacMd5CryptoServiceImpl.class);
            });
        });
    }

}
