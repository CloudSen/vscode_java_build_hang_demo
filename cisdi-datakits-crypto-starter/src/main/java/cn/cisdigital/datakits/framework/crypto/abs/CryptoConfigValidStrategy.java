package cn.cisdigital.datakits.framework.crypto.abs;

import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;

/**
 * 算法配置校验
 *
 * @author xxx
 * @since 2023-11-20
 */
public interface CryptoConfigValidStrategy {

    /**
     * 支持校验的算法类型
     */
    CryptoTypeEnum support();

    /**
     * 校验算法配置
     *
     * @param cryptoConfig 算法配置
     * @throws cn.cisdigital.datakits.framework.crypto.exception.CryptoException 算法配置错误
     */
    void valid(CryptoConfig cryptoConfig);
}
