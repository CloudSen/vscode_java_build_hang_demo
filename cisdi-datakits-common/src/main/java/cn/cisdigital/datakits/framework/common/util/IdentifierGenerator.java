package cn.cisdigital.datakits.framework.common.util;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2024-05-16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdentifierGenerator {

    /**
     * 生成唯一的请求编码
     */
    public static String generateRequestCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
