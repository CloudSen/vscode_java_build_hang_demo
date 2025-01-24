package cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * MD5摘要算法实现
 *
 * @author xxx
 */
@Service
@RequiredArgsConstructor
public class Md5CryptoServiceImpl extends DigestCryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.MD5;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        MD5Digest digest = new MD5Digest();
        String salt = CryptoUtils.getSalt(cryptoConfig.getRandomSalt(), cryptoConfig.getSaltSize(),
                cryptoConfig.getSalt());
        return digest(digest, salt, text);
    }
}
