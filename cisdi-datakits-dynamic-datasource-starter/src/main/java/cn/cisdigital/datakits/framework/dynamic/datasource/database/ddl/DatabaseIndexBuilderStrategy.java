package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;


import cn.cisdigital.datakits.framework.model.dto.database.AlterIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.CreateIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.DropIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.IndexAttrDto;

import java.util.List;

/**
 * 数据源索引创建厕所
 *
 * @author xxx
 */
public interface DatabaseIndexBuilderStrategy {

    /**
     * 创建索引
     */
    String createIndex(CreateIndexDto createIndexDto);

    /**
     * 删除索引
     */
    String dropIndex(DropIndexDto dropIndexDto);

    /**
     * 创建表时一并创建索引SQL片段
     */
    String createIndexSqlSegment(List<IndexAttrDto> createIndexDtoList);

    /**
     * 拼接ALTER TABLE 操作index的语句
     */
    String alterTableIndexSql(AlterIndexDto dto);
}
