package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3;

import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.springframework.stereotype.Component;

/**
 * SM3摘要算法参数校验
 */
@Component
public class Sm3CryptoConfigValidStrategy implements DigestCryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SM3;
    }
}
