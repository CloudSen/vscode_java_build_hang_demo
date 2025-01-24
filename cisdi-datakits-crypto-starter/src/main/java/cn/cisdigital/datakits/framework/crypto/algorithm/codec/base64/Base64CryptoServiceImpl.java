package cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfigValidator;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Base64编解码
 *
 * @author xxx
 */
@Service
@RequiredArgsConstructor
public class Base64CryptoServiceImpl implements CryptoService {

    private final CryptoConfigValidator cryptoConfigValidator;

    @Override
    public CryptoTypeEnum support() {
        return CryptoTypeEnum.BASE64;
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return Base64.toBase64String(text);
    }

    @Override
    public String encrypt(CryptoConfig cryptoConfig, String ciphertext) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, ciphertext.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] encryptToBytes(CryptoConfig cryptoConfig, String ciphertext) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, ciphertext).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encryptToBytes(CryptoConfig cryptoConfig, byte[] text) {
        cryptoConfigValidator.valid(cryptoConfig);
        return encrypt(cryptoConfig, text).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String decrypt(CryptoConfig cryptoConfig, String ciphertext) {
        cryptoConfigValidator.valid(cryptoConfig);
        return new String(Base64.decode(ciphertext));
    }

    @Override
    public byte[] decryptToBytes(CryptoConfig cryptoConfig, String ciphertext) {
        cryptoConfigValidator.valid(cryptoConfig);
        return Base64.decode(ciphertext);
    }

    @Override
    public boolean check(CryptoConfig cryptoConfig, String text, String ciphertext) {
        cryptoConfigValidator.valid(cryptoConfig);
        return ciphertext.equals(encrypt(cryptoConfig, text));
    }
}
