package cn.cisdigital.datakits.framework.common.util;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForColumnDto;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForTableDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 血缘模块工具类
 *
 * @author xxx
 * @since 2024/4/18 14:16
 */
@Slf4j
public class LineageUtil {

    private LineageUtil() {

    }

    private final static  String CONNECT = "-";

    /**
     * 根据数据源连接，库名，表名，构建唯一标识
     *
     * @param uniqueCodeForTableDto 表信息
     * @return code
     */
    public static String buildUniqueCodeForTable(final UniqueCodeForTableDto uniqueCodeForTableDto) {
        Preconditions.checkNotNull(uniqueCodeForTableDto.getDataSourceDto(),"datasource can not be null");
        return  buildUniqueCodeForTable( uniqueCodeForTableDto.getDataSourceDto().getDataBaseTypeEnum(),
                uniqueCodeForTableDto.getSchemaName(),
                uniqueCodeForTableDto.getTableName(),
                uniqueCodeForTableDto.getDataSourceDto().getHost(),
                uniqueCodeForTableDto.getDataSourceDto().getPort() + "");
    }

    /**
     * 根据数据源连接，库名，表名，构建唯一标识
     * @param dataBaseType 数据库类型
     * @param schemaName 库名
     * @param tableName 表名
     * @param host 域名
     * @param port 端口
     * @return code
     */
    public static String buildUniqueCodeForTable(final DataBaseTypeEnum dataBaseType,
                                                 final String schemaName,
                                                 final String tableName,
                                                 final String host,
                                                 final String port) {
        return  dataBaseType + CONNECT
                + host + CONNECT
                + port + CONNECT
                + schemaName + CONNECT
                + tableName;
    }

    /**
     * 根据数据源连接，库名构建唯一标识
     * @param dataBaseType 数据库类型
     * @param schemaName 库名
     * @param host 域名
     * @param port 端口
     * @return code
     */
    public static String buildUniqueCodeForDataBase(final DataBaseTypeEnum dataBaseType,
                                                 final String schemaName,
                                                 final String host,
                                                 final String port) {
        return  dataBaseType + CONNECT
                + host + CONNECT
                + port + CONNECT
                + schemaName;
    }

    /**
     * 构建字段唯一标识
     *
     * @param uniqueCodeForColumnDto 字段信息
     * @return code
     */
    public static String buildUniqueCodeForTableColumn(final UniqueCodeForColumnDto uniqueCodeForColumnDto) {
        Preconditions.checkNotNull(uniqueCodeForColumnDto.getDataSourceDto(),"datasource can not be null");
        return  uniqueCodeForColumnDto.getDataSourceDto().getDataBaseTypeEnum() + CONNECT
                + uniqueCodeForColumnDto.getDataSourceDto().getHost() + CONNECT
                + uniqueCodeForColumnDto.getDataSourceDto().getPort() + CONNECT
                + uniqueCodeForColumnDto.getSchemaName() + CONNECT
                + uniqueCodeForColumnDto.getTableName() + CONNECT
                + uniqueCodeForColumnDto.getColumnName()
                ;
    }

    /**
     * 构建数据源唯一标识
     *
     * @param dataSourceDto 连接信息
     * @return code
     */
    public static String buildUniqueCodeForDatasource(final DataSourceDto dataSourceDto) {
        Preconditions.checkNotNull(dataSourceDto,"datasource can not be null");
        return  dataSourceDto.getDataBaseTypeEnum() + CONNECT
                + dataSourceDto.getHost() + CONNECT
                + dataSourceDto.getPort();
    }

    /**
     * 根据数据源连接，库名，表名，构建唯一标识，反解数据库类型，host，port，库，表
     *
     * @param uniqueCode 表字符串
     * @return code
     */
    public static UniqueCodeForTableDto deCodeUniqueCodeForTable(final String uniqueCode) {
        Preconditions.checkNotNull(uniqueCode, "uniqueCode can not be null");
        final String[] params = uniqueCode.split(CONNECT);
        Preconditions.checkArgument(Objects.equals(params.length, 5), "uniqueCode error");
        try {
            UniqueCodeForTableDto tableDto = new UniqueCodeForTableDto();
            tableDto.setTableName(params[4]);
            tableDto.setSchemaName(params[3]);
            tableDto.setDataSourceDto(new DataSourceDto());
            tableDto.getDataSourceDto().setHost(params[1]);
            tableDto.getDataSourceDto().setDataBaseTypeEnum(DataBaseTypeEnum.valueOf(params[0]));
            tableDto.getDataSourceDto().setPort(Integer.parseInt(params[2]));
            return tableDto;
        } catch (Exception e) {
            log.error("uniqueCode {} is error", uniqueCode, e);
            throw new IllegalArgumentException("uniqueCode is error");
        }
    }
}
