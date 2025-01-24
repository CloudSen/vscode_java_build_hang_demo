package cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5;

import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.springframework.stereotype.Component;

/**
 * MD5摘要算法配置校验
 *
 * @author xxx
 */
@Component
public class Md5CryptoConfigValidStrategy implements DigestCryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.MD5;
    }
}
