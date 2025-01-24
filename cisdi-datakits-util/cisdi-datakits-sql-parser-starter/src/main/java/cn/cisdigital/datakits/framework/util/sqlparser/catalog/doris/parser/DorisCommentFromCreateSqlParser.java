package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.hutool.core.util.StrUtil;

/**
 * @author xxx
 */
public class DorisCommentFromCreateSqlParser implements DorisCreateSqlParser {

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        String surplusSql = parserInfo.getSurplusSql();
        // 备注已通过读取表信息获取，此处直接跳过
        surplusSql = surplusSql.substring(surplusSql.indexOf(StrUtil.LF) + 1);
        parserInfo.setSurplusSql(surplusSql);
        return parserInfo;
    }
}
