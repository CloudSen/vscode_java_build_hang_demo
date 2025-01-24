package cn.cisdigital.datakits.framework.crypto.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 明文及密码文加密类型
 *
 * @author xxx
 * @since 2023-11-24
 */
@Getter
@RequiredArgsConstructor
public enum CryptoTextTypeEnum {

    NORMAL("normal", "不加密"),

    BASE64("BASE64", "编解码器"),

    HEX("hex", "16进制");

    /**
     * 类型
     */
    private final String type;
    /**
     * 描述
     */
    private final String desc;
}
