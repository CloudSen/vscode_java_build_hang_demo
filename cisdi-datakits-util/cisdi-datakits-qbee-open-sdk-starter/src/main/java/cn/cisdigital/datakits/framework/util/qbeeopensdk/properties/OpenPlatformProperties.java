package cn.cisdigital.datakits.framework.util.qbeeopensdk.properties;


import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 开放平台参数配置
 *
 * @author xxx
 * @since 2022-11-11
 */
@Data
@Component
@ConfigurationProperties(prefix = "op")
public class OpenPlatformProperties implements Serializable {

    /**
     * 开放应用名
     */
    @NotBlank(message = "qbee-open-sdk.valid.service_id_not_blank")
    private String serviceId;

    /**
     * 开放应用密码
     */
    @NotBlank(message = "qbee-open-sdk.valid.service_secret_not_blank")
    private String serviceSecret;

    /**
     * 开放平台地址前缀
     */
    @NotBlank(message = "qbee-open-sdk.valid.baseurl_not_blank")
    private String baseUrl;

    private String conflictFirst;

    public OpenPlatformProperties() {
        conflictFirst = "black";
    }
}
