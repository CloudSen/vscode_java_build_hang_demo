package cn.cisdigital.datakits.framework.crypto.algorithm.digest;

import cn.cisdigital.datakits.framework.common.constant.NumberConstants;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidStrategy;
import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import org.apache.commons.lang3.BooleanUtils;

/**
 * 摘要算法参数校验
 *
 * @author xxx
 */
public interface DigestCryptoConfigValidStrategy extends CryptoConfigValidStrategy {

    @Override
    default void valid(CryptoConfig cryptoConfig) {
        boolean invalidRandomSalt = BooleanUtils.isTrue(cryptoConfig.getRandomSalt())
        && (cryptoConfig.getSaltSize() < NumberConstants.ONE || cryptoConfig.getSaltSize() > NumberConstants.FIFTY);
        if (invalidRandomSalt) {
            throw new CryptoException(CryptoException.CryptoErrorCode.INVALID_SALT_LENGTH);
        }
    }

}
