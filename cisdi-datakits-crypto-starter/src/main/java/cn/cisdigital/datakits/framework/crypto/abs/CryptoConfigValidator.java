package cn.cisdigital.datakits.framework.crypto.abs;

import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 算法配置校验器
 *
 * @author xxx
 * @since 2023-03-09
 */
@Component
public class CryptoConfigValidator {

    private final Map<CryptoTypeEnum, CryptoConfigValidStrategy> validStrategyMap;

    public CryptoConfigValidator(List<CryptoConfigValidStrategy> validStrategies) {
        this.validStrategyMap = validStrategies.stream()
                .collect(Collectors.toMap(CryptoConfigValidStrategy::support, strategy -> strategy));
    }

    /**
     * 根据算法类型，校验算法配置
     *
     * @param config 算法配置
     * @throws cn.cisdigital.datakits.framework.crypto.exception.CryptoException sdk初始化错误，算法配置错误等
     */
    public void valid(CryptoConfig config) {
        CryptoTypeEnum cryptoType = config.getCryptoType();
        if (MapUtils.isEmpty(validStrategyMap)) {
            throw new CryptoException(CryptoException.CryptoErrorCode.CRYPTO_CONFIG_VALIDATOR_INIT_EXCEPTION);
        }
        if (!validStrategyMap.containsKey(cryptoType)) {
            throw new CryptoException(CryptoException.CryptoErrorCode.CRYPTO_CONFIG_VALIDATOR_NOT_EXISTS, cryptoType);
        }
        CryptoConfigValidStrategy validator = validStrategyMap.get(cryptoType);
        validator.valid(config);
    }
}
