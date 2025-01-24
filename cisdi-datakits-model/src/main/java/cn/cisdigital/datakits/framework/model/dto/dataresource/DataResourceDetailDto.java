package cn.cisdigital.datakits.framework.model.dto.dataresource;

import cn.cisdigital.datakits.framework.model.dto.ParamDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.EnvironmentEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * 数据源详情Dto
 *
 * @author xxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResourceDetailDto {

    /**
     * 数据源 ID
     */
    Long resourceId;

    /**
     * 环境
     */
    EnvironmentEnum environment;

    /**
     * 数据连接名称
     */
    String resourceName;

    /**
     * 数据连接类型,1: MySQL 2: RocketMQ等
     *
     * @see DataBaseTypeEnum#getValue()
     */
    DataBaseTypeEnum resourceType;

    /**
     * 版本号
     */
    String resourceVersion;

    /**
     * 连接地址(完整的url，包含参数，可以直接使用)
     */
    String resourceUrl;

    /**
     * 连接Host
     */
    String host;

    /**
     * 连接端口
     */
    String port;

    /**
     * 数据库名称
     */
    String databaseName;

    /**
     * 数据源用户名
     */
    String accessKey;

    /**
     * 数据源密码
     */
    String secretKey;

    /**
     * doris的HTTP接口的端口号，用于HTTP协议写入数据
     */
    Integer dorisHttpPort;

    /**
     * 数据表空间
     */
    String tableSpace;

    /**
     * 表索引空间
     */
    String tableIndex;

    /**
     * 协议参数配置
     */
    List<ParamDto> param;

    /**
     * Access 或 SQLite文件路径（不包含参数）
     */
    String filePath;

    /**
     * 边缘侧IP地址，用于流量代理通信或代理执行SQL
     */
    String edgeAgentIp;

    /**
     * 边缘侧流量代理通信端口，用于流量代理通信
     */
    String edgeAgentPort;

    /**
     * 根据代理地址和端口生成的代理 Url，包含参数信息，统一在这里生成，使用方不用考虑如何替换
     */
    String edgeAgentUrl;

    /**
     * oracle数据库的容器数据库名称
     */
    String cdbName;


}

