package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.doris.*;
import cn.cisdigital.datakits.framework.model.enums.DorisDataModelEnum;
import cn.cisdigital.datakits.framework.model.enums.MaterializeTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.PartitionModelEnum;
import cn.cisdigital.datakits.framework.util.doris.api.DorisFeApi;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.*;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Doris属性信息解析服务
 *
 * @author xxx
 */
public class DorisParserService {

    private DorisParserService(){}

    private static final List<DorisCreateSqlParser> PARSER_LIST = new ArrayList<>();

    static {
        // 责任链模式的变体实现，列表顺序即为链式调用顺序
        PARSER_LIST.add(new DorisTableInfoForCreateSqlParser());
        PARSER_LIST.add(new DorisEngineTypeFromCreateSqlParser());
        PARSER_LIST.add(new DorisModelFromCreateSqlParser());
        PARSER_LIST.add(new DorisCommentFromCreateSqlParser());
        PARSER_LIST.add(new DorisPartitionFromCreateSqlParser());
        PARSER_LIST.add(new DorisBucketFromCreateSqlParser());
        PARSER_LIST.add(new DorisPropertiesFromCreateSqlParser());
    }

    /**
     * 获取Doris表详情
     */
    public static List<DorisTableDto> listTableDetail(DataSourceDto source, String databaseName,
            List<String> tableNameList) {
        List<DatabaseTableDto> databaseTableList = tableNameList.stream()
                .distinct()
                .map(tableName -> new DatabaseTableDto(databaseName, tableName))
                .collect(Collectors.toList());
        return listTableDetail(source, databaseTableList);
    }

    /**
     * 获取Doris表详情
     */
    public static List<DorisTableDto> listTableDetail(DataSourceDto source, List<DatabaseTableDto> databaseTableList) {
        DorisBaseDto doris = buildDorisBaseDto(source);
        Map<DatabaseTableDto, String> createTableSqlMap = DorisFeApi.queryCreateTableSql(doris, databaseTableList);

        List<CreateSqlParserDto> parseInfo = databaseTableList.stream().map(databaseTable -> {
            DorisTableDto dorisTableInfo = newDefaultInstance();
            dorisTableInfo.setDatabaseName(databaseTable.getDatabaseName());
            dorisTableInfo.setTableName(databaseTable.getTableName());
            String createTableSql = createTableSqlMap.get(databaseTable);
            return StringUtils.hasText(createTableSql) ? new CreateSqlParserDto(createTableSql, dorisTableInfo) : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        PARSER_LIST.forEach(parser -> parser.parseBatch(source, parseInfo));
        return parseInfo.stream().map(CreateSqlParserDto::getTableInfo).collect(Collectors.toList());
    }

    private static DorisBaseDto buildDorisBaseDto(DataSourceDto source) {
        DorisBaseDto dorisBaseDto = new DorisBaseDto();
        dorisBaseDto.setHost(source.getHost());
        dorisBaseDto.setFePort(source.getDorisFePort());
        dorisBaseDto.setUsername(source.getUsername());
        dorisBaseDto.setPassword(source.getPassword());
        return dorisBaseDto;
    }

    private static DorisTableDto newDefaultInstance() {
        DorisTableConfigDto dorisTableConfigDto = new DorisTableConfigDto();
        dorisTableConfigDto.setDataModel(DorisDataModelEnum.DUPLICATE);
        dorisTableConfigDto.setPartitionMode(PartitionModelEnum.DEFAULT_PARTITION);
        // 默认没有分区配置, 后续解析时再需要的情况下设置分区配置
        dorisTableConfigDto.setPartitionConfig(null);
        dorisTableConfigDto.setBucketConfig(new BucketConfigDto());
        dorisTableConfigDto.setParamConfigList(Lists.newArrayList());

        DorisTableDto dorisTableDto = new DorisTableDto();
        dorisTableDto.setTableType(MaterializeTypeEnum.TABLE);
        dorisTableDto.setColumnList(Lists.newArrayList());
        dorisTableDto.setTableConfig(dorisTableConfigDto);
        return dorisTableDto;
    }

}
