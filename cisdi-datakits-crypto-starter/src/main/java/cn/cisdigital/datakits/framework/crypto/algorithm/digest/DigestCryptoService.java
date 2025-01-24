package cn.cisdigital.datakits.framework.crypto.algorithm.digest;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.util.encoders.Hex;

import javax.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * 摘要算法实现
 *
 * @author xxx
 */
public abstract class DigestCryptoService implements CryptoService {

    /**
     * 摘要算法加密
     * <p>
     * 目前都是直接吧盐加在原文后面，然后再加密
     * </p>
     *
     * @param digest      摘要算法
     * @param salt        盐值
     * @param originBytes 原文字节
     * @return 密文
     */
    public static String digest(GeneralDigest digest, String salt, byte[] originBytes) {
        originBytes = ArrayUtils.addAll(originBytes, salt.getBytes(StandardCharsets.UTF_8));
        byte[] output = new byte[digest.getDigestSize()];
        digest.update(originBytes, 0, originBytes.length);
        digest.doFinal(output, 0);
        return Hex.toHexString(output);
    }

    /**
     * 摘要算法加密
     * <p>
     * 目前都是直接吧盐加在原文后面，然后再加密
     * </p>
     *
     * @param digest      长摘要算法
     * @param salt        盐值
     * @param originBytes 原文字节
     * @return 密文
     */
    public static String digest(LongDigest digest, String salt, byte[] originBytes) {
        if (StringUtils.isNotBlank(salt)) {
            originBytes = ArrayUtils.addAll(originBytes, salt.getBytes(StandardCharsets.UTF_8));
        }
        byte[] output = new byte[digest.getDigestSize()];
        digest.update(originBytes, 0, originBytes.length);
        digest.doFinal(output, 0);
        return Hex.toHexString(output);
    }

    @Override
    public boolean check(@NotNull CryptoConfig cryptoConfig, @NotNull String text, @NotNull String ciphertext) {
        return ciphertext.equals(encrypt(cryptoConfig, text));
    }

}
