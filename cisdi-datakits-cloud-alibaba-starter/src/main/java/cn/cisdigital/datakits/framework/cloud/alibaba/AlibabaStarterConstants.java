package cn.cisdigital.datakits.framework.cloud.alibaba;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2024-03-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlibabaStarterConstants {

    public static final String ALIBABA_STARTER_PROPERTIES_PREFIX = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".cloud";
    public static final String REMOTE_COUNT_PROPERTIES_PREFIX = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".element-count-service";
    public static final String REST_TEMPLATE_PROPERTIES_PREFIX = ALIBABA_STARTER_PROPERTIES_PREFIX + ".rest-template";

}
