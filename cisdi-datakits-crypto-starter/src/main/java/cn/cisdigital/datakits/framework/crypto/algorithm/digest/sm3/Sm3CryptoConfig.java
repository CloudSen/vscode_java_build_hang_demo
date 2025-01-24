package cn.cisdigital.datakits.framework.crypto.algorithm.digest.sm3;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * SM3摘要算法配置参数
 *
 * @author xxx
 */
@Data
@Accessors(chain = true)
public class Sm3CryptoConfig implements CryptoConfig {

    /**
     * 是否为随机盐(32位)，默认false
     */
    private Boolean randomSalt = false;

    /**
     * 当不是随机盐时，可以指定固定盐值，不指定就不加盐，建议长度大于32
     */
    private String salt;

    /**
     * 随机盐值，默认长度为8
     */
    private Integer saltSize = 8;

    @Override
    public CryptoTypeEnum getCryptoType() {
        return CryptoTypeEnum.SM3;
    }
}
