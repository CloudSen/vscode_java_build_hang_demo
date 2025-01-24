package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;

import lombok.Data;

/**
 * doris连接信息
 */
@Data
public class DorisConnectionInfo {
    /**
     * ip或域名
     */
    private String host;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * http服务端口, 默认8030
     */
    private Integer dorisHttpPort;

    /**
     * jdbc连接端口, 默认9030
     */
    private Integer port;

}
