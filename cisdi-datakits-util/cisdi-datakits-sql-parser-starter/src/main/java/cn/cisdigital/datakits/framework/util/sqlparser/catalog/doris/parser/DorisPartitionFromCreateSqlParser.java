package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.ColumnBase;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableDto;
import cn.cisdigital.datakits.framework.model.dto.doris.PartitionConfigDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.DorisPartitionModelEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Set;
import java.util.stream.Stream;

/**
 * @author xxx
 */
public class DorisPartitionFromCreateSqlParser implements DorisCreateSqlParser {

    private static final String PARTITION_END_KEYWORD = "))\n";
    private static final int PARTITION_END_KEYWORD_LENGTH = PARTITION_END_KEYWORD.length();

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        DorisTableDto tableInfo = parserInfo.getTableInfo();
        String surplusSql = parserInfo.getSurplusSql();
        for (DorisPartitionModelEnum partitionModel : DorisPartitionModelEnum.values()) {
            if (surplusSql.startsWith(partitionModel.getDorisCode())) {
                Set<String> columnNameSet = CollStreamUtil.toSet(tableInfo.getColumnList(), ColumnBase::getColumnName);
                PartitionConfigDto partitionConfig = tableInfo.getTableConfig().getOrInitPartitionConfig();
                partitionConfig.setPartitionModel(partitionModel);
                // 截取分区字段
                String partitionColumnStr = surplusSql.substring(surplusSql.indexOf(LEFT_PARENTHESIS) + 1,
                        surplusSql.indexOf(RIGHT_PARENTHESIS));
                String[] splitColumn = partitionColumnStr.split(StrUtil.COMMA);
                Stream.of(splitColumn)
                        .map(column -> DataBaseTypeEnum.DORIS.unwrappedEscapeCharacter(column.trim()))
                        .filter(columnNameSet::contains)
                        .forEach(partitionConfig.getPartitionColumnList()::add);
                // 已成功获取分区信息，SQL中去除相关内容
                surplusSql =
                        surplusSql.substring(surplusSql.indexOf(PARTITION_END_KEYWORD) + PARTITION_END_KEYWORD_LENGTH);
                parserInfo.setSurplusSql(surplusSql);
                break;
            }
        }
        return parserInfo;
    }
}
