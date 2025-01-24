package cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author xxx
 */
@Component
public class Base64CryptoConfigValidStrategy implements CryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.BASE64;
    }

    @Override
    public void valid(CryptoConfig cryptoConfig) {
        // 不需要校验
    }

}
