package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * SHA256摘要算法实现
 *
 * @author xxx
 * @since 2023-03-09
 */
@Service
@RequiredArgsConstructor
public class Sha256CryptoServiceImpl extends DigestCryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.SHA256;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        SHA256Digest digest = new SHA256Digest();
        String salt = CryptoUtils.getSalt(cryptoConfig.getRandomSalt(), cryptoConfig.getSaltSize(),
                cryptoConfig.getSalt());
        return digest(digest, salt, text);
    }
}
