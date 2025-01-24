package cn.cisdigital.datakits.framework.crypto.abs;

import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoModeEnum;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoPaddingEnum;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.crypto.model.enums.MaskingModeEnum;

import java.io.Serializable;

/**
 * 代表这是一个算法配置类
 *
 * @author xxx
 * @since 2023-11-20
 */
public interface CryptoConfig extends Serializable {

    /**
     * 获取算法类型
     */
    CryptoTypeEnum getCryptoType();

    /**
     * 获取加密模式
     */
    default CryptoModeEnum getMode() {
        return null;
    }

    /**
     * 获取密钥字节数组
     */
    default byte[] getKey() {
        return null;
    }

    /**
     * 获取密钥字节大小
     */
    default Integer getKeySize() {
        return null;
    }

    /**
     * 获取填充模式
     */
    default CryptoPaddingEnum getPadding() {
        return null;
    }

    /**
     * 获取偏移量
     */
    default byte[] getIv() {
        return null;
    }

    /**
     * 获取偏移量字节大小
     */
    default Integer getIvSize() {
        return null;
    }

    /**
     * 是否为随机偏移量，如果使用随机iv，会忽略自己配置的盐
     *
     * @return 默认返回true，使用随机偏移量
     * @apiNote 不同加密算法，随机偏移量位数不同, DES用8个字节，AES用16字节
     */
    default Boolean getRandomIv() {
        return null;
    }

    /**
     * 获取盐
     */
    default String getSalt() {
        return null;
    }

    /**
     * 获取随机言盐长度
     */
    default Integer getSaltSize() {
        return null;
    }

    /**
     * 是否为随机盐，如果使用随机盐，会忽略自己配置的盐
     *
     * @return 默认返回true，随机盐默认为8位
     * @apiNote 随机盐位
     */
    default Boolean getRandomSalt() {
        return null;
    }

    /**
     * 掩码-遮掩字符
     */
    default String getMaskingCharacter() {
        return null;
    }

    /**
     * 掩码-遮掩模式
     */
    default MaskingModeEnum getMaskingMode() {
        return null;
    }

    /**
     * 掩码-配置值（遮掩长度或遮掩正则匹配规则）
     */
    default String getMaskingValue() {
        return null;
    }

    /**
     * 遮掩-加密结果长度
     */
    default Integer getMaskingResultSize() {
        return null;
    }
}
