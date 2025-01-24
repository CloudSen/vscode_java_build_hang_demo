package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import com.zaxxer.hikari.HikariConfig;

import java.util.Optional;

/**
 * @author xxx
 * @since 2022-08-09-10:09
 */
public class HikariDefaultConfig {

    /**
     * 获取连接池默认连接配置
     *
     * @return
     */
    public static HikariConfig getDefaultConfig(DataSourceDto dataSourceDto) {
        HikariConfig hikariConfig = new HikariConfig();
        //允许连接在池中闲置的最长时间 毫秒，MinimumIdle<MaximumPoolSize生效
        hikariConfig.setIdleTimeout(Optional.ofNullable(dataSourceDto.getIdleTimeoutMs()).orElse(600000L));
        //池中连接的最大生存期 30分钟
        hikariConfig.setMaxLifetime(Optional.ofNullable(dataSourceDto.getMaxLifetimeMs()).orElse(1800000L));
        //控制池允许达到的最大大小，包括空闲和正在使用的连接
        hikariConfig.setMaximumPoolSize(Optional.ofNullable(dataSourceDto.getMaximumPoolSize()).orElse(10));
        //连接池定期对池里的连接做数据库的测试操作，只对空闲连接做探活操作，非空闲的连接不做探活 毫秒
        hikariConfig.setKeepaliveTime(Optional.ofNullable(dataSourceDto.getKeepAliveTimeMs()).orElse(60000L));
        //最小空闲连接数
        hikariConfig.setMinimumIdle(Optional.ofNullable(dataSourceDto.getMinimumIdle()).orElse(10));
        //建立链接超时时间 毫秒
        hikariConfig.setConnectionTimeout(Optional.ofNullable(dataSourceDto.getConnectionTimeoutMs()).orElse(15000L));
        //连接池名称
        hikariConfig.setPoolName(Optional.ofNullable(dataSourceDto.getPoolName()).orElse("ds-cache-pool"));
        return hikariConfig;
    }

}
