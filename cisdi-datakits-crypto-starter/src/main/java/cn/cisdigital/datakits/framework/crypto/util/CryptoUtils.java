package cn.cisdigital.datakits.framework.crypto.util;

import cn.cisdigital.datakits.framework.common.constant.NumberConstants;
import cn.cisdigital.datakits.framework.common.constant.SymbolConstants;
import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoModeEnum;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoPaddingEnum;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;

/**
 * 加解密会用到的工具
 *
 * @author xxx
 * @since 2023-11-20
 */
@NoArgsConstructor(access = PRIVATE)
public final class CryptoUtils {

    /**
     * 构建固定长度的字符串，不足的补*，超过的截断
     *
     * @param origin 原始字符串
     * @param length 长度
     * @return 固定长度字符串
     */
    public static String buildFixLenStr(final String origin, final int length) {
        if (StringUtils.isBlank(origin) || length < NumberConstants.ZERO) {
            throw new IllegalArgumentException();
        }
        final int currentLen = origin.length();
        // 满足长度，直接截取
        if (currentLen > length) {
            return origin.substring(0, length);
        }
        // 不满足则追加*
        return origin + StringUtils.repeat(SymbolConstants.ASTERISK, length - currentLen);
    }

    /**
     * 获得盐值
     *
     * @param random     是否随机盐
     * @param randomSize 随机盐长度
     * @param fixSalt    用户配置的固定盐
     * @return 实际的盐值
     */
    public static String getSalt(Boolean random, int randomSize, String fixSalt) {
        if (BooleanUtils.isTrue(random)) {
            final Random r = new SecureRandom();
            byte[] salt = new byte[randomSize];
            r.nextBytes(salt);
            return Base64.toBase64String(salt);
        }
        return StringUtils.isBlank(fixSalt) ? StringUtils.EMPTY : fixSalt;
    }

    /**
     * 获得偏移量
     *
     * @param random     是否随机偏移量
     * @param randomSize 随机偏移量的字节长度
     * @param fixIv      用户配置的固定偏移量
     * @return
     */
    public static byte[] getIv(boolean random, int randomSize, byte[] fixIv) throws NoSuchAlgorithmException {
        if (random) {
            // getInstanceStrong在不同环境下,存在性能问题,这里使用直接创建SecureRandom
            final Random secureRandom = new SecureRandom();
            byte[] iv = new byte[randomSize];
            secureRandom.nextBytes(iv);
            return iv;
        }
        return fixIv;
    }

    public static String getKey(boolean random, int keySize, String fixKey) {
        if (random) {
            final Random r = new SecureRandom();
            byte[] bytes = new byte[keySize];
            r.nextBytes(bytes);
            return StringUtils.substring(Base64.toBase64String(bytes), NumberConstants.ZERO, keySize);
        }
        return StringUtils.isBlank(fixKey) ? StringUtils.EMPTY : fixKey;
    }

    /**
     * 构建JCE标准的算法标识
     *
     * @param cryptoType    算法类型
     * @param cryptoMode    算法模式
     * @param cryptoPadding 填充模式
     * @return 格式：algorithm/mode/padding
     */
    public static String buildAlgorithm(CryptoTypeEnum cryptoType, CryptoModeEnum cryptoMode,
            CryptoPaddingEnum cryptoPadding) {
        return String.join(SymbolConstants.SLASH, cryptoType.name(), cryptoMode.name(), cryptoPadding.getDesc());
    }

    /**
     * 构建JCE标准的摘要算法标识
     *
     * @param cryptoType 摘要算法类型
     * @return 格式：algorithm
     */
    public static String buildDigestAlgorithm(CryptoTypeEnum cryptoType) {
        return cryptoType.name();
    }

    /**
     * 反序列化算法配置
     *
     * @param configJson 算法配置JSON
     * @return 算法配置对象
     */
    public static Optional<CryptoConfig> deserializeConfig(String configJson) throws JsonProcessingException {
        JsonNode node = JsonUtils.readTree(configJson);
        CryptoTypeEnum cryptoType = CryptoTypeEnum.valueOf(node.get("cryptoType").asText());
        return deserializeConfig(cryptoType, configJson);
    }

    /**
     * 反序列化算法配置
     *
     * @param configJson 算法配置JSON
     * @return 算法配置对象
     */
    public static Optional<CryptoConfig> deserializeConfig(CryptoTypeEnum cryptoType, String configJson)
            throws JsonProcessingException {
        if (StringUtils.isBlank(configJson) || cryptoType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(JsonUtils.parseObject(configJson, cryptoType.getCryptoConfigClass()));
    }

    /**
     * 十六进制字符串，转换为字节数组，如"FFAABBCC"
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        return Hex.decodeStrict(hexString);
    }

    /**
     * 字节数组，转换为十六进制字符串，如"FFAABBCC"
     *
     * @param hexBytes 字节数组
     * @return 十六进制字符串
     */
    public static String bytesToHexString(byte[] hexBytes) {
        return Hex.toHexString(hexBytes);
    }
}
