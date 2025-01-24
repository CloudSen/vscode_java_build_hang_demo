package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import cn.cisdigital.datakits.framework.common.AutoConfigConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = AutoConfigConstants.CONFIG_PROPERTIES_PREFIX + ".ds.hikari")
public class HikariPoolProperties {

    /**
     * KEY: 数据源使用场景
     * VALUE: HikariCP配置
     */
    @Valid
    private Map<String, HikariCpCoreConfig> poolConfigMap;

    @Data
    public static class HikariCpCoreConfig {

        /**
         * 连接池名称
         */
        @NotBlank
        private String poolName;

        /**
         * 建立链接超时时间 毫秒，默认15000(15秒)
         */
        @Min(250)
        private Long connectionTimeoutMs = 15000L;

        /**
         * 允许连接在池中闲置的最长时间 毫秒，默认600000(10分钟)
         */
        @Min(10000)
        private Long idleTimeoutMs = 600000L;

        /**
         * 池中连接的最大生存期 毫秒，建议稍微小于数据库wait_timeout，默认1800000(30分钟)
         */
        @Min(30000)
        private Long maxLifetimeMs = 1800000L;

        /**
         * 连接池定期对池里的连接做数据库的测试操作，只对空闲连接做探活操作，非空闲的连接不做探活 毫秒，默认60000(1分钟)
         */
        @Min(30000)
        private Long keepAliveTimeMs = 60000L;

        /**
         * 最小空闲连接数, 默认10
         */
        @Min(1)
        private Integer minimumIdle = 10;

        /**
         * 最大连接池大小, 默认10
         */
        @Min(1)
        private Integer maximumPoolSize = 10;
    }
}
