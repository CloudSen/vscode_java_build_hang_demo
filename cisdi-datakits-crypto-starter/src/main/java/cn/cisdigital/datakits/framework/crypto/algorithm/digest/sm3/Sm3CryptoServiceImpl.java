package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * SM3摘要算法实现
 *
 * @author xxx
 */
@Service
@RequiredArgsConstructor
public class Sm3CryptoServiceImpl extends DigestCryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SM3;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        SM3Digest digest = new SM3Digest();
        String salt = CryptoUtils.getSalt(cryptoConfig.getRandomSalt(), cryptoConfig.getSaltSize(),
                cryptoConfig.getSalt());
        return digest(digest, salt, text);
    }
}
