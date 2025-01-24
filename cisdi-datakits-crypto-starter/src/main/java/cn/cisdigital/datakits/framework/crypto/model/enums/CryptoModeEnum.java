package cn.cisdigital.datakits.framework.crypto.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 加解密模式
 *
 * @author xxx
 * @since 2023-11-20
 */
@Getter
@RequiredArgsConstructor
public enum CryptoModeEnum {

    /**
     * 密码块链模式，每个明文块在加密之前都会与前一个密文块进行异或运算，然后再进行加密。这样，每个密文块的生成都依赖于前一个密文块，从而消除了 ECB 可预测性的问题
     */
    CBC,
    /**
     * 电子密码本模式，将明文分割成固定大小的块（例如 64 位），并对每个块独立地进行加密
     */
    ECB,
    /**
     * 密码反馈模式：将前一个密文块作为输入来加密当前明文块。适用于流加密算法。
     */
    CFB,
    /**
     * 输出反馈模式：将前一个输出块作为输入来加密当前明文块。适用于流加密算法。
     */
    OFB,
    /**
     * 计数器模式：使用一个计数器和密钥生成加密的伪随机流，并将该流与明文进行异或操作。适用于流加密算法。
     */
    CTR,
    /**
     * Galois/Counter Mode，结合了加密模式和消息认证码，提供加密和完整性验证。适用于块加密算法。
     */
    GCM,
    /**
     * 通过相关数据进行身份验证的加密，结合了加密模式和消息认证码，并允许附加数据进行认证。适用于块加密算法。
     */
    EAX,
    /**
     * Counter with CBC-MAC，结合了加密模式和消息认证码，并提供加密和完整性验证。适用于块加密算法。
     */
    CCM,
    /**
     * XTS-AES，针对磁盘加密而设计的模式，提供加密和随机访问的功能。
     */
    XTS

}
