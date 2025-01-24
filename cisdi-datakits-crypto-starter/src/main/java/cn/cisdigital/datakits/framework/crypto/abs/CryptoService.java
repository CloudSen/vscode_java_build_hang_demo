package cn.cisdigital.datakits.framework.crypto.abs;

import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import cn.cisdigital.datakits.framework.model.util.I18nUtils;

import javax.validation.constraints.NotNull;

/**
 * 加密解密服务
 *
 * @author xxx
 * @apiNote 算法配置必须位于第一个参数，方便AOP拦截校验
 * @since 2022-10-11
 */
public interface CryptoService {

    /**
     * 支持的加密类型
     *
     * @return CryptoTypeEnum
     */
    CryptoTypeEnum support();

    //<editor-fold desc="解密">

    /**
     * 根据算法配置，解密密文
     *
     * @param ciphertext   密文字符串
     * @param cryptoConfig 算法配置
     * @return 原文字符串
     */
    default String decrypt(@NotNull CryptoConfig cryptoConfig, @NotNull String ciphertext) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 根据算法配置，解密密文
     *
     * @param ciphertext   密文字节数组
     * @param cryptoConfig 算法配置
     * @return 原文字符串
     */
    default String decrypt(@NotNull CryptoConfig cryptoConfig, @NotNull byte[] ciphertext) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 根据算法配置，解密密文
     *
     * @param ciphertext   密文字符串
     * @param cryptoConfig 算法配置
     * @return 原文字节数组
     */
    default byte[] decryptToBytes(@NotNull CryptoConfig cryptoConfig, @NotNull String ciphertext) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 根据算法配置，解密密文
     *
     * @param ciphertext   密文字节数组
     * @param cryptoConfig 算法配置
     * @return 原文字节数组
     */
    default byte[] decryptToBytes(@NotNull CryptoConfig cryptoConfig, @NotNull byte[] ciphertext) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    //</editor-fold>

    //<editor-fold desc="加密">

    /**
     * 加密原文
     *
     * @param text         原文字符串
     * @param cryptoConfig 算法配置
     * @return 密文字符串
     */
    default String encrypt(@NotNull CryptoConfig cryptoConfig, @NotNull String text) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 加密原文
     *
     * @param text         原文字节
     * @param cryptoConfig 算法配置
     * @return 密文字符串
     */
    default String encrypt(@NotNull CryptoConfig cryptoConfig, @NotNull byte[] text) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 加密原文
     *
     * @param text         原文字符串
     * @param cryptoConfig 算法配置
     * @return 密文字节数组
     */
    default byte[] encryptToBytes(@NotNull CryptoConfig cryptoConfig, @NotNull String text) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }

    /**
     * 加密原文
     *
     * @param text         原文字节
     * @param cryptoConfig 算法配置
     * @return 密文字节数组
     */
    default byte[] encryptToBytes(@NotNull CryptoConfig cryptoConfig, @NotNull byte[] text) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }
    //</editor-fold>

    //<editor-fold desc="校验">

    /**
     * 校验原文加密后与密文是否一致
     *
     * @param cryptoConfig 算法配置
     * @param text         原文
     * @param ciphertext   密文
     * @return true：一致； false：不一致
     */
    default boolean check(@NotNull CryptoConfig cryptoConfig, @NotNull String text, @NotNull String ciphertext) {
        throw new UnsupportedOperationException(I18nUtils.getMessage("common.exception.unsupported_operation"));
    }
    //</editor-fold>

}
