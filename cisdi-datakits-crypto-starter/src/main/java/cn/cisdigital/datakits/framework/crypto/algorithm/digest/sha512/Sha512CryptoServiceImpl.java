package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * SHA512摘要算法实现
 *
 * @author xxx
 */
@Service
@RequiredArgsConstructor
public class Sha512CryptoServiceImpl extends DigestCryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SHA512;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String text) {
        return encrypt(cryptoConfig, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        SHA512Digest digest = new SHA512Digest();
        String salt = CryptoUtils.getSalt(cryptoConfig.getRandomSalt(), cryptoConfig.getSaltSize(),
                cryptoConfig.getSalt());
        return digest(digest, salt, text);
    }
}
