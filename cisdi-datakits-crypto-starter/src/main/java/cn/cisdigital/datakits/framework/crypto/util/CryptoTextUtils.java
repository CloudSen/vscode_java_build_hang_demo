package cn.cisdigital.datakits.framework.crypto.util;

import cn.cisdigital.datakits.framework.crypto.model.enums.CryptoTextTypeEnum;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/**
 * @author xxx
 * @since 2023-11-24
 */
public class CryptoTextUtils {

    public static String encodeText(byte[] bytes, CryptoTextTypeEnum cryptoTextTypeEnum) {
        switch (cryptoTextTypeEnum) {
            case NORMAL:
                return new String(bytes, StandardCharsets.UTF_8);
            case HEX:
                return Hex.toHexString(bytes);
            default:
                return Base64.toBase64String(bytes);
        }
    }

    public static byte[] decodeText(String text, CryptoTextTypeEnum cryptoTextTypeEnum) {
        switch (cryptoTextTypeEnum) {
            case NORMAL:
                return text.getBytes(StandardCharsets.UTF_8);
            case HEX:
                return Hex.decode(text);
            default:
                return Base64.decode(text);
        }
    }

}
