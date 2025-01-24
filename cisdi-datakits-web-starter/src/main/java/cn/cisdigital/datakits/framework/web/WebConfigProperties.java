package cn.cisdigital.datakits.framework.web;

import cn.cisdigital.datakits.framework.web.constant.WebConstants;
import java.time.Duration;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


/**
 * WebMvc相关的yam配置
 * @author xxx
 * @implNote 暂时去掉了@Validated校验
 * @since 2022-10-13
 */
@Data
@Component
@ConfigurationProperties(prefix = WebConstants.WEB_CONFIG_PROPERTIES_PREFIX)
public class WebConfigProperties {

    @Valid
    private GlobalExceptionHandler globalExceptionHandler;

    @Valid
    private Mvc mvc;

    @Valid
    private Serializer serializer;

    @Valid
    private AsyncPool asyncPool;

    @Data
    @Validated
    public static class Mvc {

        @NotNull
        private Boolean enabled;

        @NotNull
        private Boolean mdcFilterEnabled;

        @NotNull
        private Boolean xssSqlFilterEnabled;

        @NotNull
        private Boolean tokenInterceptorEnabled;

        @NotNull
        private Boolean headerInterceptorEnabled;

        private List<String> ignoredUrlForXssSqlFilter;
    }

    @Data
    @Validated
    public static class GlobalExceptionHandler {

        @NotNull
        private Boolean enabled;
    }

    @Data
    @Validated
    public static class Serializer {

        @NotNull
        private Boolean enabled;
    }

    @Data
    @Validated
    public static class AsyncPool {

        @NotNull
        private Boolean enabled;

        @NotBlank
        private String threadNamePrefix = "async-executor-";

        @Range(min = 1, max = Integer.MAX_VALUE)
        private Integer corePoolSize = 1;

        @Range(min = 1, max = Integer.MAX_VALUE)
        private Integer maxPoolSize = 1;

        @Range(min = 1, max = Integer.MAX_VALUE)
        private Integer queueCapacity = 200;

        @NotNull
        private Boolean allowCoreThreadTimeout = false;

        @NotNull
        private Duration keepAliveSeconds = Duration.ofSeconds(30);
    }
}
