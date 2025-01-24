package cn.cisdigital.datakits.framework.dynamic.datasource.utils;

import static cn.hutool.core.text.StrPool.COLON;

import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;

/**
 * @author xxx
 * @since 2024-05-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthUtils {

    private static final String BASIC = "Basic ";

    /**
     * 生成base密码
     */
    public static String basicAuthHeader(String username, String password) {
        final String tobeEncode = username + COLON + password;
        byte[] encoded = Base64.encodeBase64(tobeEncode.getBytes(StandardCharsets.UTF_8));
        return BASIC + new String(encoded);
    }
}
