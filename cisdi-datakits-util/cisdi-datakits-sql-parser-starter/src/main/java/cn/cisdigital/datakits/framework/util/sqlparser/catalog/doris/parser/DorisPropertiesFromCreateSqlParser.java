package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.util.FunctionUtil;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.ParamDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableDto;
import cn.cisdigital.datakits.framework.model.enums.PartitionModelEnum;
import cn.cisdigital.datakits.framework.model.enums.PartitionTimeSpanEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.EMPTY;
import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.LF;

/**
 * @author xxx
 */
@Slf4j
public class DorisPropertiesFromCreateSqlParser implements DorisCreateSqlParser {

    private static final String DYNAMIC_PARTITION_TIME_UNIT = "dynamic_partition.time_unit";
    private static final String DYNAMIC_PARTITION_START = "dynamic_partition.start";
    private static final String DYNAMIC_PARTITION_END = "dynamic_partition.end";
    private static final String DYNAMIC_PARTITION_PREFIX = "dynamic_partition.prefix";
    private static final String DYNAMIC_PARTITION_CREATE_HISTORY_PARTITION = "dynamic_partition" +
            ".create_history_partition";
    private static final String DYNAMIC_PARTITION_HISTORY_PARTITION_NUM = "dynamic_partition.history_partition_num";

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        DorisTableDto tableInfo = parserInfo.getTableInfo();
        String surplusSql = parserInfo.getSurplusSql();
        int propertiesBeginIndex = surplusSql.indexOf(LEFT_PARENTHESIS) + 1;
        int propertiesEndIndex = surplusSql.indexOf(RIGHT_PARENTHESIS);
        String propertiesInfo = surplusSql.substring(propertiesBeginIndex, propertiesEndIndex);
        propertiesInfo = propertiesInfo.replaceAll(DOUBLE_QUOTATION, EMPTY).replaceAll(COMMA + LF, LF);

        Properties properties = this.parseStringToProperties(propertiesInfo);
        List<ParamDto> paramList = properties.entrySet().stream()
                .map(entry -> new ParamDto(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
                .collect(Collectors.toList());
        DorisTableConfigDto tableConfig = tableInfo.getTableConfig();
        tableConfig.setParamConfigList(paramList);
        // 动态分区信息
        // 动态分区调度的单位
        String timeSpanProperty = properties.getProperty(DYNAMIC_PARTITION_TIME_UNIT);
        PartitionTimeSpanEnum partitionTimeSpanEnum = PartitionTimeSpanEnum.valueOfByName(timeSpanProperty);
        if (Objects.nonNull(partitionTimeSpanEnum)) {
            tableConfig.getOrInitPartitionConfig().setTimeSpan(partitionTimeSpanEnum);
        }
        // 动态分区的起始偏移
        String partitionStart = properties.getProperty(DYNAMIC_PARTITION_START);
        if (StringUtils.hasText(partitionStart) && !DYNAMIC_PARTITION_DEFAULT_START.toString().equals(partitionStart)) {
            tableConfig.getOrInitPartitionConfig().setPartitionStart(Math.abs(NumberUtil.parseInt(partitionStart)));
        }
        // 动态分区的结束偏移
        String partitionEndProperty = properties.getProperty(DYNAMIC_PARTITION_END);
        if (StringUtils.hasText(partitionEndProperty)) {
            tableConfig.getOrInitPartitionConfig().setPartitionEnd(NumberUtil.parseInt(partitionEndProperty));
        }
        // 动态创建的分区名前缀
        String partitionPrefix = properties.getProperty(DYNAMIC_PARTITION_PREFIX);
        if (StringUtils.hasText(partitionEndProperty)) {
            tableConfig.getOrInitPartitionConfig().setPartitionPrefix(partitionPrefix);
        }
        // 是否创建历史分区
        String createHistoryPartition = properties.getProperty(DYNAMIC_PARTITION_CREATE_HISTORY_PARTITION);
        if (StringUtils.hasText(createHistoryPartition)) {
            tableConfig.getOrInitPartitionConfig().setCreateHistoryPartition(Boolean.valueOf(createHistoryPartition));
        }
        // 历史分区数量
        String historyPartitionNum = properties.getProperty(DYNAMIC_PARTITION_HISTORY_PARTITION_NUM);
        if (StringUtils.hasText(historyPartitionNum) && !DYNAMIC_PARTITION_DEFAULT_HISTORY_PARTITION.toString().equals(historyPartitionNum)) {
            tableConfig.getOrInitPartitionConfig().setHistoryPartitionNum(NumberUtil.parseInt(historyPartitionNum));
        }
        // 设置是否分区
        tableConfig.setPartitionMode(tableConfig.isPartition() ? PartitionModelEnum.CUSTOM_PARTITION :
                PartitionModelEnum.DEFAULT_PARTITION);
        return parserInfo;
    }

    public Properties parseStringToProperties(String propertiesString) {
        return FunctionUtil.get(() -> {
            Properties properties = new Properties();
            properties.load(new StringReader(propertiesString));
            return properties;
        }, "解析Doris属性失败, properties: " + propertiesString);
    }
}
