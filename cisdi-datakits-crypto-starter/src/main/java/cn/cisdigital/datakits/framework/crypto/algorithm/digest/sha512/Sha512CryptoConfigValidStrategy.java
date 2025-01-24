package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512;

import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Sha512摘要算法参数校验
 *
 * @author xxx
 */
@Component
public class Sha512CryptoConfigValidStrategy implements DigestCryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SHA512;
    }
}
