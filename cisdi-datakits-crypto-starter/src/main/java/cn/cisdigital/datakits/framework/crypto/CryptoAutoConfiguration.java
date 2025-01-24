package cn.cisdigital.datakits.framework.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

/**
 * @author xxx
 * @since 2023-03-09
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"cn.cisdigital.datakits.framework.crypto"})
@ConditionalOnClass(BouncyCastleProvider.class)
public class CryptoAutoConfiguration {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        log.info("[自动装配] 加解密已加载");
    }
}
