package cn.cisdigital.datakits.framework.crypto.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 掩码模式
 *
 * @author xxx
 * @since 2023-11-21
 */
@Getter
@RequiredArgsConstructor
public enum MaskingModeEnum {

    /**
     * 遮掩头
     */
    MASKING_HEAD,
    /**
     * 遮掩尾
     */
    MASKING_TAIL,
    /**
     * 遮掩指定位置
     */
    MASKING_INDEX,
    /**
     * 遮掩正则匹配
     */
    MASKING_REGEX
}
