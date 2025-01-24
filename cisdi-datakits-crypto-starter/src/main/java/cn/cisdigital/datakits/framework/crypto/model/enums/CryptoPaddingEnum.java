package cn.cisdigital.datakits.framework.crypto.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 算法填充方式
 *
 * @author xxx
 * @since 2023-11-20
 */
@Getter
@RequiredArgsConstructor
public enum CryptoPaddingEnum {

    /**
     * 常用于对称加密算法，要求块大小为8字节。如果数据块不足8字节，将使用额外的字节来填充，填充字节的值等于需要填充的字节数。
     */
    PKCS5PADDING("PKCS5Padding"),
    /**
     * 填充与 PKCS#5 填充类似，但用于更通用的块大小，例如16字节。填充字节的值等于需要填充的字节数。
     */
    PKCS7PADDING("PKCS7Padding"),
    /**
     * 不进行任何填充操作
     */
    NOPADDING("NoPadding"),
    /**
     * 在数据块末尾添加填充字节，其值为随机生成的字节。
     * 在填充字节之前添加一个字节，其值等于填充字节数。
     */
    ISO10126PADDING("ISO10126Padding"),
    /**
     * 在数据块末尾添加填充字节，其值为 80（十六进制）。
     * 如果数据块长度已经是块大小的倍数，会在数据块末尾添加一个完整的块。
     */
    ISO7816_4PADDING("ISO7816-4Padding"),
    ;

    private final String desc;
}
