package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256;

import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Sha256摘要算法参数校验
 *
 * @author xxx
 * @since 2023-11-20
 */
@Component
public class Sha256CryptoConfigValidStrategy implements DigestCryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SHA256;
    }
}
