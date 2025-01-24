package cn.cisdigital.datakits.framework.util.sqlparser.util;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForColumnDto;
import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForTableDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ContextDataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.FieldParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.FieldRecord;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.type.DorisBasicSqlType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024/4/18 9:16
 */
public class Convertor {

    private Convertor() {
    }

    public static UniqueCodeForTableDto createUniqueCodeForTableDto(ContextDataSourceDto dto, String schema, String table) {
        UniqueCodeForTableDto sourceTableDto = new UniqueCodeForTableDto();
        sourceTableDto.setDataSourceDto(toDataSourceDto(dto));
        sourceTableDto.setSchemaName(schema);
        sourceTableDto.setTableName(table);
        return sourceTableDto;
    }

    public static UniqueCodeForColumnDto createUniqueCodeForColumnDto(ContextDataSourceDto dto, String schema, String table, String columnName) {
        final UniqueCodeForColumnDto uniqueCodeForColumnDto = new UniqueCodeForColumnDto();
        uniqueCodeForColumnDto.setDataSourceDto(toDataSourceDto(dto));
        uniqueCodeForColumnDto.setSchemaName(schema);
        uniqueCodeForColumnDto.setTableName(table);
        uniqueCodeForColumnDto.setColumnName(columnName);
        return uniqueCodeForColumnDto;
    }

    public static FieldParseResult fromRelColumnOrigins(String queryName, Set<RelColumnOrigin> origins) {
        FieldParseResult fieldParseResult = new FieldParseResult();
        fieldParseResult.setQueryName(queryName);

        if (CollectionUtils.isEmpty(origins)) {
            fieldParseResult.setOriginIds(Collections.singletonList(
                    FieldRecord.builder().columnEnName(queryName).build()
            ));
            return fieldParseResult;
        }
        fieldParseResult.setOriginIds(origins.stream().map(origin -> {
                    String originTableName = null;
                    String originDatabaseName = null;
                    List<String> qualifiedName = origin.getOriginTable().getQualifiedName();
                    if (CollectionUtils.isNotEmpty(qualifiedName)) {
                        if (Objects.equals(qualifiedName.size(), 1)) {
                            originTableName = qualifiedName.get(0);
                        } else if (Objects.equals(qualifiedName.size(), 2)) {
                            originDatabaseName = qualifiedName.get(0);
                            originTableName = qualifiedName.get(1);
                        } else if (Objects.equals(qualifiedName.size(), 3)) {
                            originDatabaseName = qualifiedName.get(1);
                            originTableName = qualifiedName.get(2);
                        }
                    }
                    // 这里拿出来的是缓存记录，doris特殊类型如largeInt也使用的SqlTypeName.BIGINT，则如果先查询了largeInt的字段，
                    // 再查询BIGINT,这里拿到的DorisType就还是LargeInt,需要从table中获取
                    RelDataTypeField relDataTypeField = origin.getOriginTable().getRowType().getFieldList().get(origin.getOriginColumnOrdinal());
                    final DorisBasicSqlType type = (DorisBasicSqlType) relDataTypeField.getType();
                    return
                            FieldRecord.builder()
                                    .columnEnName(relDataTypeField.getKey())
                                    .tableName(originTableName)
                                    .originDatabase(originDatabaseName)
                                    .columnType(type.getFieldColumnType())
                                    .comment(type.getFieldComment())
                                    .build();
                })
                .collect(Collectors.toList()));
        return fieldParseResult;
    }


    /**
     * 根据数据源连接构建唯一key
     *
     * @param dataSourceDto 连接信息
     * @return key
     */
    public static String buildUniqueCodeForCatalog(final ContextDataSourceDto dataSourceDto) {
        return
                dataSourceDto.getDataBaseTypeEnum() + "-" +
                        dataSourceDto.getHost() + "-" +
                        dataSourceDto.getPort() + "-" +
                        dataSourceDto.getUsername();
    }

    /**
     * 构造数据源上下文
     * @param dataSourceDto dataSource
     * @return dto
     */
    public static ContextDataSourceDto fromDataSourceDto(final DataSourceDto dataSourceDto){
        Preconditions.checkNotNull(dataSourceDto.getDataBaseTypeEnum(), "dataBaseTypeEnum can not be null");
        Preconditions.checkNotNull(dataSourceDto.getUsername(), "username can not be null");
        Preconditions.checkNotNull(dataSourceDto.getPassword(), "password can not be null");
        Preconditions.checkNotNull(dataSourceDto.getHost(), "host can not be null");
        Preconditions.checkNotNull(dataSourceDto.getPort(), "port can not be null");

        ContextDataSourceDto contextDataSourceDto = new ContextDataSourceDto();
        contextDataSourceDto.setDataBaseTypeEnum(dataSourceDto.getDataBaseTypeEnum());
        contextDataSourceDto.setDatabaseDriver(dataSourceDto.getDatabaseDriver());
        contextDataSourceDto.setUrl(dataSourceDto.getUrl());
        contextDataSourceDto.setUsername(dataSourceDto.getUsername());
        contextDataSourceDto.setPassword(dataSourceDto.getPassword());
        contextDataSourceDto.setHost(dataSourceDto.getHost());
        contextDataSourceDto.setPort(dataSourceDto.getPort());
        contextDataSourceDto.setSchemaName(dataSourceDto.getSchemaName());
        return contextDataSourceDto;
    }

    /**
     * 构造model模块数据源上下文
     * @param contextDataSourceDto dataSource
     * @return dto
     */
    public static DataSourceDto toDataSourceDto(final ContextDataSourceDto contextDataSourceDto){
        DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setDataBaseTypeEnum(contextDataSourceDto.getDataBaseTypeEnum());
        dataSourceDto.setDatabaseDriver(contextDataSourceDto.getDatabaseDriver());
        dataSourceDto.setUrl(contextDataSourceDto.getUrl());
        dataSourceDto.setUsername(contextDataSourceDto.getUsername());
        dataSourceDto.setPassword(contextDataSourceDto.getPassword());
        dataSourceDto.setHost(contextDataSourceDto.getHost());
        dataSourceDto.setPort(contextDataSourceDto.getPort());
        dataSourceDto.setSchemaName(contextDataSourceDto.getSchemaName());
        return dataSourceDto;
    }


}
