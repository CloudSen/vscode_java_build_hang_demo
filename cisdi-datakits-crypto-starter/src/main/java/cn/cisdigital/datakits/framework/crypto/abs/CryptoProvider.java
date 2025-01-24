package cn.cisdigital.datakits.framework.crypto.abs;

import cn.cisdigital.datakits.framework.crypto.exception.CryptoException;
import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTypeEnum;
import lombok.NonNull;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 算法服务工厂类
 *
 * @author xxx
 * @since 2023-03-09
 */
@Component
public class CryptoProvider {

    private final Map<CryptoTypeEnum, CryptoService> cryptoServiceMap;

    public CryptoProvider(List<CryptoService> cryptoServices) {
        this.cryptoServiceMap = cryptoServices.stream()
                .collect(Collectors.toMap(CryptoService::support, service -> service));
    }

    /**
     * 根据算法类型，获取对应的服务类
     *
     * @param cryptoType 算法类型
     * @return 算法类型对应的服务类
     * @throws CryptoNotImplException 算法未实现
     * @throws CryptoSdkInitException sdk初始化错误
     */
    public CryptoService getCryptoServiceOf(@NonNull CryptoTypeEnum cryptoType) {
        if (MapUtils.isEmpty(cryptoServiceMap)) {
            throw new CryptoException(CryptoException.CryptoErrorCode.INIT_EXCEPTION);
        }
        if (!cryptoServiceMap.containsKey(cryptoType)) {
            throw new CryptoException(CryptoException.CryptoErrorCode.UNSUPPORTED_ALGORITHM, cryptoType);
        }
        return cryptoServiceMap.get(cryptoType);
    }

    /**
     * 获取已注册的算法服务数量
     *
     * @return 已注册的算法服务数量
     */
    public int getCryptoServiceCount() {
        return cryptoServiceMap.size();
    }
}
