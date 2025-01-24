package cn.cisdigital.datakits.framework.crypto.model.enums;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64.Base64CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.hmacmd5.HmacMd5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.md5.Md5CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha1.Sha1CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha256.Sha256CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sha512.Sha512CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3.Sm3CryptoConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 加解密算法类型
 *
 * @author xxx
 * @since 2023-03-09
 */
@Getter
@RequiredArgsConstructor
public enum CryptoTypeEnum {

    //<editor-fold desc="非对称加密">
    // RSA("加密", "RSA", RsaCryptoConfig.class),
    // //</editor-fold>

    // //<editor-fold desc="脱敏">
    // MASKING("掩码", "掩码", MaskCryptoConfig.class),
    //</editor-fold>

    //<editor-fold desc="编解码器">
    BASE64("编解码器", "BASE64", Base64CryptoConfig.class),
    //</editor-fold>

    //<editor-fold desc="摘要">
    MD5("哈希", "MD5", Md5CryptoConfig.class),
    SHA1("哈希", "SHA1", Sha1CryptoConfig.class),
    SHA256("哈希", "SHA256", Sha256CryptoConfig.class),
    SHA512("哈希", "SHA512", Sha512CryptoConfig.class),
    SM3("哈希", "SM3", Sm3CryptoConfig.class),
    HmacMD5("哈希", "HmacMD5", HmacMd5CryptoConfig.class),
	// HmacSHA1("哈希", "HmacSHA1", HmacSha1CryptoConfig.class),
	// HmacSHA256("哈希", "HmacSHA256", HmacSha256CryptoConfig.class),
	// HmacSHA512("哈希", "HmacSHA512", HmacSha512CryptoConfig.class),
	// /** HmacSM3算法实现，需要BouncyCastle库支持 */
	// HmacSM3("哈希", "HmacSM3", HmacSm3CryptoConfig.class),
    //</editor-fold>

    // //<editor-fold desc="对称加密">
    // AES("加密", "AES", AesCryptoConfig.class),
    // DES("加密", "DES", DesCryptoConfig.class),
    // SM4("加密", "SM4", Sm4CryptoConfig.class),
    // //</editor-fold>

    // /**
    //  * 不脱敏
    //  */
    // NONE("其他", "不脱敏", OriginalCryptoConfig.class),

    ;

    /**
     * 算法大类
     */
    private final String type;
    /**
     * 算法描述
     */
    private final String desc;
    /**
     * 算法配置类信息
     */
    private final Class<? extends CryptoConfig> cryptoConfigClass;
}
