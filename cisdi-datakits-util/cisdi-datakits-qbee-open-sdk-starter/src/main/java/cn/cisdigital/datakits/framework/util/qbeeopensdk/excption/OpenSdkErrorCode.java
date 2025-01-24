package cn.cisdigital.datakits.framework.util.qbeeopensdk.excption;

import cn.cisdigital.datakits.framework.model.interfaces.ErrorCode;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author xxx
 * @date 2024-07-29-16:17
 */
@Getter
@RequiredArgsConstructor
public enum OpenSdkErrorCode implements ErrorCode {

    THIS_EMPLOYEE_IS_NOT_FOUND("000005001","open-sdk.this_employee_is_not_found"),
    THIS_ORG_IS_NOT_FOUND("000005002","open-sdk.this_org_is_not_found"),
    MASTER_ORG_IS_NOT_FOUND("000005003","open-sdk.master_org_is_not_found"),
    THIS_EMPLOYEE_IS_NOT_FOUND_BY_USER_ID("000005004","open-sdk.this_employee_is_not_found_by_user_id"),
    THIS_EMPLOYEE_IS_NOT_FOUND_BY_ACCOUNT("000005005","open-sdk.this_employee_is_not_found_by_account"),

    ;

    private final String code;
    private final String key;

    public static String getMsgByCode(String code) {
        return Arrays.stream(values())
            .filter(em -> CharSequenceUtil.equals(code, em.getCode()))
            .findFirst()
            .map(ErrorCode::getMsg)
            .orElse(CharSequenceUtil.EMPTY);
    }
}
