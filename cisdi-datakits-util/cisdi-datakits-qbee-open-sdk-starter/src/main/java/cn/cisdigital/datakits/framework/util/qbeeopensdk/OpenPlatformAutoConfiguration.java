package cn.cisdigital.datakits.framework.util.qbeeopensdk;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.properties.OpenPlatformProperties;
import im.qingtui.qbee.open.platfrom.base.common.utils.ConfigUtils;
import im.qingtui.qbee.open.platfrom.base.common.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 开放平台sdk初始化
 *
 * @author xxx
 * @since 2022-11-11
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "cn.cisdigital.datakits.framework.util.qbeeopensdk")
public class OpenPlatformAutoConfiguration implements ApplicationRunner {

    private final OpenPlatformProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ConfigUtils.setValue("service.id", properties.getServiceId());
        ConfigUtils.setValue("service.secret", properties.getServiceSecret());
        ConfigUtils.setValue("base.url", properties.getBaseUrl());
        ConfigUtils.setValue("conflict.first", properties.getConflictFirst());
        TokenUtils.refreshToken();
        log.info(AutoConfigConstants.LOG_PREFIX + "已配置开放平台SDK");
    }
}
