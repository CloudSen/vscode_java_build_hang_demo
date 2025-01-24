package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;


import cn.cisdigital.datakits.framework.model.dto.database.*;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.DatabaseConstants.TRUNCATE_TABLE_TEMPLATE;

/**
 * 数据源DDL语句构建策略
 *
 * @author xxx
 */
public interface DatabaseDdlBuilderStrategy {

    /**
     * 当前构建者绑定的数据库类型
     */
    DataBaseTypeEnum databaseType();

    /**
     * 创建表
     * 说明：不会对自增、默认值、字符集、等特性创建表，仅仅建立类型一致，主键一致,是否为空的表
     */
    String createTableSql(CreateDto createDto);

    /**
     * 删除表
     */
    String dropTableSql(TableBase tableBase);

    /**
     * 清空表
     */
    default String truncateTableSql(TableBase tableBase) {
        return String.format(TRUNCATE_TABLE_TEMPLATE, tableBase.getFullyTableName());
    }

    /**
     * 对表名修改
     */
    String renameTableSql(RenameTableNameDto renameDto);

    /**
     * 修改表
     */
    String alterTableSql(AlterDto alterDto);

    /**
     * 根据数据类型返回构建Sql的列类型，比如根据类型decimal和精度信息返回 decimal(20,15)
     * @param columnBase
     * @return
     */
    String buildColumnType(ColumnBase columnBase);

    String alterTableComment(AlterTableCommentDto alterTableCommentDto);
    String alterColumnComment(AlterColumnCommentDto alterColumnCommentDto);

    /**
     * 创建视图
     * @return
     */
    String createView(CreateViewDto dto);

    /**
     * 删除视图
     */
    String dropView(DropViewDto dto);

    /**
     * 修改视图
     */
    String alterView(AlterViewDto dto);

    /**
     * 创建索引
     */
    String createIndex(CreateIndexDto createIndexDto);

    /**
     * 删除索引
     */
    String dropIndex(DropIndexDto dropIndexDto);

    /**
     * alter table操作索引
     */
    String alterIndex(AlterIndexDto alterIndexDto);

}
