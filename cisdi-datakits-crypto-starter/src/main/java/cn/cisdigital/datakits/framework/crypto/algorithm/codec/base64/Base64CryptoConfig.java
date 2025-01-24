package cn.cisdigital.datakits.framework.crypto.algorithm.codec.base64;

import cn.cisdigital.datakits.framework.crypto.abs.CryptoConfig;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Base64配置参数
 *
 * @author xxx
 */
@Data
@Accessors(chain = true)
public class Base64CryptoConfig implements CryptoConfig {

    @Override
    public CryptoTypeEnum getCryptoType() {
        return CryptoTypeEnum.BASE64;
    }
}
