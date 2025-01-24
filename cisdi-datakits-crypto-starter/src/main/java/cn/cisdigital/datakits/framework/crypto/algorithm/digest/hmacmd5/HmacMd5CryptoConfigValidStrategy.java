package cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * HMAC MD5摘要算法配置校验
 *
 * @author xxx
 */
@Component
public class HmacMd5CryptoConfigValidStrategy implements DigestCryptoConfigValidStrategy {

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.HmacMD5;
    }

    @Override
    public void valid(CryptoConfig cryptoConfig) {
        DigestCryptoConfigValidStrategy.super.valid(cryptoConfig);
        boolean missingKey = BooleanUtils.isNotTrue(cryptoConfig.getRandomSalt())
                && StringUtils.isBlank(cryptoConfig.getSalt());
        if (missingKey) {
            throw new CryptoException(CryptoException.CryptoErrorCode.HMAC_MISSING_KEY);
        }
    }
}
