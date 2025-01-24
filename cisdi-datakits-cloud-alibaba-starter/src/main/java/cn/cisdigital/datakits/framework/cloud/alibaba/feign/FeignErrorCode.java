package cn.cisdigital.datakits.framework.cloud.alibaba.feign;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xxx
 * @since 2024-03-19
 */
@Getter
@RequiredArgsConstructor
public enum FeignErrorCode implements ErrorCode {
    FEIGN_CALL_EXCEPTION("FEIGN-001", "cloud-alibaba.exception.feign_call_error"),
    FEIGN_NULL_RESULT("FEIGN-002", "cloud-alibaba.exception.feign_client_null_result"),
    FEIGN_EMPLOYEE_PARSE_ERROR("FEIGN-003", "cloud-alibaba.exception.employee_name_encode_error"),
    FEIGN_DATA_IS_NULL("FEIGN-004", "cloud-alibaba.exception.data_is_null"),
    FEIGN_CALL_TIMEOUT("FEIGN-005", "cloud-alibaba.exception.feign_call_timeout"),
    FEIGN_CALL_NOT_FOUND("FEIGN-006", "cloud-alibaba.exception.feign_call_not_found"),
    FEIGN_UNKNOWN_HOST("FEIGN-007", "cloud-alibaba.exception.feign_call_unknown_host"),

    ;

    private final String code;
    private final String key;

}
