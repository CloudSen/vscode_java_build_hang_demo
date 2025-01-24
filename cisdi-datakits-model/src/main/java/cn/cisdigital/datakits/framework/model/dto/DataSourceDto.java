package cn.cisdigital.datakits.framework.model.dto;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import lombok.*;
import lombok.ToString.Exclude;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author xxx
 * @since 2022-08-05-14:24
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class DataSourceDto implements Serializable {

    /**
     * 数据源名称 非必须
     */
    private String datasourceName;

    /**
     * 数据库名字 mysql/doris为数据库名，其他数据库为schema名字
     */
    @EqualsAndHashCode.Include
    private String schemaName;

    /**
     * 数据源类型
     */
    @NonNull
    @EqualsAndHashCode.Include
    private DataBaseTypeEnum dataBaseTypeEnum;

    /**
     * 数据源类型版本号 非必须
     */
    private String version;

    /**
     * JDBC url 地址
     */
    @NonNull
    @EqualsAndHashCode.Include
    private String url;

    /**
     * JDBC 用户名
     */
    @EqualsAndHashCode.Include
    private String username;

    /**
     * JDBC 密码
     */
    @Exclude
    @EqualsAndHashCode.Include
    private String password;

    /**
     * JDBC 驱动
     */
    private String databaseDriver;

    /**
     * 数据源IP地址
     */
    private String host;

    /**
     * 数据源端口
     */
    private Integer port;
    /**
     * doris的FE端口
     */
    private Integer dorisFePort;

    /**
     * 自定义驱动协议
     */
    private String driverClassName;

    /**
     * 查询超时时间 秒，默认20秒
     */
    private Integer queryTimeout;

    /* ----------------------------- sap数据库配置----------------------------- */
    /**
     * 系统编号
     */
    private String sysnr;
    /**
     * 客户端编号
     */
    private String client;
    /**
     * 实例 SID
     */
    private String r3name;

    /* ----------------------------- Hikari连接池配置，没指定就用默认的(没弄成对象是因为要兼容历史代码) ----------------------------- */
    /**
     * 连接池名称
     */
    @EqualsAndHashCode.Include
    private String poolName;

    /**
     * 建立链接超时时间 毫秒，建议15000(15秒)
     */
    private Long connectionTimeoutMs;

    /**
     * 允许连接在池中闲置的最长时间 毫秒，建议600000(10分钟)
     */
    private Long idleTimeoutMs;

    /**
     * 池中连接的最大生存期 毫秒，建议稍微小于数据库wait_timeout，默认1800000(30分钟)
     */
    private Long maxLifetimeMs;

    /**
     * 连接池定期对池里的连接做数据库的测试操作，只对空闲连接做探活操作，非空闲的连接不做探活 毫秒，建议60000(1分钟)
     */
    private Long keepAliveTimeMs;

    /**
     * 最小空闲连接数, 建议10
     */
    private Integer minimumIdle;

    /**
     * 最大连接池大小, 建议10
     */
    private Integer maximumPoolSize;

    public DataSourceDto(DataBaseTypeEnum dataBaseTypeEnum, String url, String username, String password) {
        this.dataBaseTypeEnum = dataBaseTypeEnum;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 重写get方法
     */
    public String getDatabaseDriver() {
        return this.dataBaseTypeEnum.getDriverClassName();
    }
}
