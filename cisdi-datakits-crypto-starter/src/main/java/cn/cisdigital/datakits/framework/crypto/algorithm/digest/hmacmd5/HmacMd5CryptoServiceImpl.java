package cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.DigestCryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * HMAC MD5摘要算法实现
 *
 * @author xxx
 */
@Service
@RequiredArgsConstructor
public class HmacMd5CryptoServiceImpl extends DigestCryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.HmacMD5;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        HMac hmac = new HMac(new MD5Digest());
        String key = CryptoUtils.getSalt(cryptoConfig.getRandomSalt(), cryptoConfig.getSaltSize(),
                cryptoConfig.getSalt());

        hmac.init(new KeyParameter(key.getBytes(StandardCharsets.UTF_8)));
        hmac.update(text, 0, text.length);

        byte[] hmacResult = new byte[hmac.getMacSize()];
        hmac.doFinal(hmacResult, 0);
        return Hex.toHexString(hmacResult);
    }
}
