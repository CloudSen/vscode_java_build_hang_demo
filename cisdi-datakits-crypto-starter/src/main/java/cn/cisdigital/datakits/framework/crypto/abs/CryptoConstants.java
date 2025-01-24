package cn.cisdigital.datakits.framework.crypto.abs;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CryptoConstants {

    /**
     * 加密类型
     */
    public static final String CRYPTO_TYPE = "cryptoType";

    /**
     * 加密模式
     */
    public static final String MODE = "mode";

    /**
     * 加密密钥
     */
    public static final String KEY = "key";

    /**
     * 密钥大小
     */
    public static final String KEY_SIZE = "keySize";

    /**
     * 填充模式
     */
    public static final String PADDING = "padding";

    /**
     * 初始化向量
     */
    public static final String IV = "iv";

    /**
     * 初始化向量大小
     */
    public static final String IV_SIZE = "ivSize";

    /**
     * 是否随机生成初始化向量
     */
    public static final String RANDOM_IV = "randomIv";

    /**
     * 盐值
     */
    public static final String SALT = "salt";

    /**
     * 盐值大小
     */
    public static final String SALT_SIZE = "saltSize";

    /**
     * 是否随机生成盐值
     */
    public static final String RANDOM_SALT = "randomSalt";

    /**
     * 掩码字符
     */
    public static final String MASKING_CHARACTER = "maskingCharacter";

    /**
     * 掩码模式
     */
    public static final String MASKING_MODE = "maskingMode";

    /**
     * 掩码值
     */
    public static final String MASKING_VALUE = "maskingValue";

    /**
     * 掩码结果大小
     */
    public static final String MASKING_RESULT_SIZE = "maskingResultSize";

}
