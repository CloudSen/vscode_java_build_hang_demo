package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model;

import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 建表SQL的解析参数
 *
 * @author xxx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSqlParserDto {

    /**
     * 各个解析器去除自身已解析内容的剩余SQL
     */
    String surplusSql;
    /**
     * 已解析的表信息，后续解析器需在此基础上补充自己负责的内容信息
     */
    DorisTableDto tableInfo;
}
