package unit.config;

import cn.cisdigital.datakits.framework.crypto.CryptoAutoConfiguration;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoProvider;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import org.assertj.core.api.MapAssert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.annotation.DirtiesContext;

import java.security.Provider;
import java.security.Security;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));

    private final ApplicationContextRunner filteredRunner = new ApplicationContextRunner()
            .withClassLoader(new FilteredClassLoader(BouncyCastleProvider.class))
            .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class));


    /**
     * 加解密starter，底层依赖BouncyCastleProvider的标准算法实现。
     * 底层有BouncyCastleProvider.class才会开始自动装配。
     */
    @Test
    @DirtiesContext
    @DisplayName("自动装配生效")
    void success_load_starter() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(CryptoAutoConfiguration.class);
            Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
            assertThat(provider).isNotNull();
            assertThat(provider).isInstanceOf(BouncyCastleProvider.class);
        });
    }

    /**
     * 加解密starter，底层依赖BouncyCastleProvider的标准算法实现。
     * 若使用方自己排除了BouncyCastle依赖，则不会自动装配。
     */
    @Test
    @DirtiesContext
    @DisplayName("自动装配失效")
    void failed_load_starter() {
        filteredRunner.run(context -> {
            assertThat(context).doesNotHaveBean(CryptoAutoConfiguration.class);
        });
    }

    /**
     * 加解密starter，会提供以下顶层Bean：
     * - CryptoAutoConfiguration: 算法自动装配
     * - CryptoProvider: 算法工厂
     * - CryptoConfigValidator: 算法配置校验器
     *
     * 以及以下多态Bean
     * - CryptoService: 算法服务接口
     * - CryptoConfigValidStrategy: 算法配置校验策略接口
     */
    @Test
    @DisplayName("bean成功注册")
    void success_register_algorithm_beans() {
        runner.run(context -> {
            MapAssert<String, CryptoService> algorithmBeans = assertThat(context).getBeans(CryptoService.class);
            algorithmBeans.extracting(Map::values).satisfies(values -> {
                values.forEach(value -> System.out.println("Algorithm Bean: " + value));
            });
            MapAssert<String, CryptoConfigValidStrategy> validBeans = assertThat(context)
                    .getBeans(CryptoConfigValidStrategy.class);
            validBeans.extracting(Map::values).satisfies(values -> {
                values.forEach(value -> System.out.println("ValidStrategy Bean: " + value));
            });

            assertThat(context).hasSingleBean(CryptoProvider.class);
            assertThat(context).hasSingleBean(CryptoConfigValidator.class);

            assertThat(context).hasBean("base64CryptoServiceImpl");
            assertThat(context).hasBean("md5CryptoServiceImpl");
            assertThat(context).hasBean("sha1CryptoServiceImpl");
            assertThat(context).hasBean("sha256CryptoServiceImpl");
            assertThat(context).hasBean("sha512CryptoServiceImpl");
            assertThat(context).hasBean("sm3CryptoServiceImpl");

            assertThat(context).hasBean("base64CryptoConfigValidStrategy");
            assertThat(context).hasBean("md5CryptoConfigValidStrategy");
            assertThat(context).hasBean("sha1CryptoConfigValidStrategy");
            assertThat(context).hasBean("sha256CryptoConfigValidStrategy");
            assertThat(context).hasBean("sha512CryptoConfigValidStrategy");
            assertThat(context).hasBean("sm3CryptoConfigValidStrategy");
            algorithmBeans.hasSize(6);
            validBeans.hasSize(6);
        });
    }
}
