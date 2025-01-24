package cn.cisdigital.datakits.framework.cloud.alibaba.properties;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author xxx
 * @since 2024-03-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RemoteServiceInfo implements Serializable {

    /**
     * 接口提供方
     */
    @NotBlank
    private String provider;

    /**
     * nacos: http://service_name/uri
     * 波塞冬: https://portal_host/uri
     */
    @NotBlank
    private String url;

    /**
     * 元素类型，eg：数据源引用
     */
    @NotBlank
    private String type;
}
