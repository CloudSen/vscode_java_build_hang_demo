package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.doris.BucketConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.DorisBucketType;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xxx
 */
public class DorisBucketFromCreateSqlParser implements DorisCreateSqlParser {

    private static final String BUCKET_KEYWORD = "BUCKETS";
    private static final int BUCKET_KEYWORD_LENGTH = BUCKET_KEYWORD.length();

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        DorisTableDto tableInfo = parserInfo.getTableInfo();
        String surplusSql = parserInfo.getSurplusSql();
        for (DorisBucketType bucketType : DorisBucketType.values()) {
            if (surplusSql.startsWith(bucketType.getDorisCode())) {
                BucketConfigDto bucketConfig = tableInfo.getTableConfig().getBucketConfig();
                bucketConfig.setBucketType(bucketType);

                int endIndex = surplusSql.indexOf(StrUtil.LF);
                String bucketInfo = surplusSql.substring(0, endIndex);
                if (DorisBucketType.HASH.equals(bucketType)) {
                    int columnBeginIndex = bucketInfo.indexOf(LEFT_PARENTHESIS) + 1;
                    int columnEndIndex = bucketInfo.indexOf(RIGHT_PARENTHESIS);
                    String bucketColumnInfo = bucketInfo.substring(columnBeginIndex, columnEndIndex);
                    List<String> bucketColumnList = Stream.of(bucketColumnInfo.split(StrUtil.COMMA))
                            .map(column -> DataBaseTypeEnum.DORIS.unwrappedEscapeCharacter(column.trim()))
                            .collect(Collectors.toList());
                    bucketConfig.setBucketColumnList(bucketColumnList);
                }

                String bucketNumber =
                        bucketInfo.substring(bucketInfo.lastIndexOf(BUCKET_KEYWORD) + BUCKET_KEYWORD_LENGTH).trim();
                bucketConfig.setBucketNum(bucketNumber);
                // 已成功获取分桶信息，SQL中去除相关内容
                surplusSql = surplusSql.substring(endIndex + 1);
                parserInfo.setSurplusSql(surplusSql);
                break;
            }
        }
        return parserInfo;
    }
}
