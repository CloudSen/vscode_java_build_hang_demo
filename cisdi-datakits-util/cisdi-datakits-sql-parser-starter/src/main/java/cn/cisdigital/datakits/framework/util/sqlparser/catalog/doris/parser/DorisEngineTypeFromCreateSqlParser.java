package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;
import cn.hutool.core.util.StrUtil;

/**
 * @author xxx
 */
public class DorisEngineTypeFromCreateSqlParser implements DorisCreateSqlParser {

    @Override
    public CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException {
        String surplusSql = parserInfo.getSurplusSql();
        // 没人使用引擎信息，直接去除引擎内容
        surplusSql = surplusSql.substring(surplusSql.indexOf(StrUtil.LF) + 1);
        parserInfo.setSurplusSql(surplusSql);
        return parserInfo;
    }
}
