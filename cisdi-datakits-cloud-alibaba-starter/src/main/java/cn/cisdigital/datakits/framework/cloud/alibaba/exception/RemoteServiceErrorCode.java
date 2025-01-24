package cn.cisdigital.datakits.framework.cloud.alibaba.exception;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xxx
 * @since 2024-03-21
 */
@Getter
@RequiredArgsConstructor
public enum RemoteServiceErrorCode implements ErrorCode {

    /**
     * 远程服务未配置
     */
    SERVICE_MISSING("REMOTE-001", "cloud-alibaba.exception.service_missing"),
    /**
     * 元素类型不能为空
     */
    SERVICE_TYPE_MISSING("REMOTE-002", "cloud-alibaba.exception.service_type_missing"),
    ;

    private final String code;
    private final String key;
}
