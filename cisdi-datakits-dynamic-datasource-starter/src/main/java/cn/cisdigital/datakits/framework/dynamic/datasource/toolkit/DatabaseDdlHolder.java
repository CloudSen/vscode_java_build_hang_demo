package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;

import cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl.DatabaseDdlBuilderFactory;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl.DatabaseIndexBuilderFactory;
import cn.cisdigital.datakits.framework.model.dto.database.*;
import cn.cisdigital.datakits.framework.model.dto.database.AlterColumnCommentDto;
import cn.cisdigital.datakits.framework.model.dto.database.AlterDto;
import cn.cisdigital.datakits.framework.model.dto.database.AlterTableCommentDto;
import cn.cisdigital.datakits.framework.model.dto.database.AlterViewDto;
import cn.cisdigital.datakits.framework.model.dto.database.CreateDto;
import cn.cisdigital.datakits.framework.model.dto.database.CreateViewDto;
import cn.cisdigital.datakits.framework.model.dto.database.DropViewDto;
import cn.cisdigital.datakits.framework.model.dto.database.RenameTableNameDto;
import cn.cisdigital.datakits.framework.model.dto.database.TableBase;

/**
 * SQL工具类
 *
 * @author xxx
 */
public class DatabaseDdlHolder {

    public static String createTableSql(CreateDto creatDto) {
        return DatabaseDdlBuilderFactory.getBuilder(creatDto.getDataBaseTypeEnum()).createTableSql(creatDto);
    }

    public static String dropTableSql(TableBase tableBase) {
        return DatabaseDdlBuilderFactory.getBuilder(tableBase.getDataBaseTypeEnum()).dropTableSql(tableBase);
    }

    public static String truncateTableSql(TableBase tableBase) {
        return DatabaseDdlBuilderFactory.getBuilder(tableBase.getDataBaseTypeEnum()).truncateTableSql(tableBase);
    }

    public static String alterTableSql(AlterDto alterDto) {
        return DatabaseDdlBuilderFactory.getBuilder(alterDto.getDataBaseTypeEnum()).alterTableSql(alterDto);
    }

    public static String renameTableSql(RenameTableNameDto renameDto){
        return DatabaseDdlBuilderFactory.getBuilder(renameDto.getDataBaseTypeEnum()).renameTableSql(renameDto);
    }

    public static String alterTableCommentSql(AlterTableCommentDto alterTableCommentDto){
        return DatabaseDdlBuilderFactory.getBuilder(alterTableCommentDto.getDataBaseTypeEnum()).alterTableComment(alterTableCommentDto);
    }

    public static String alterTableColumnCommentSql(AlterColumnCommentDto alterTableCommentDto){
        return DatabaseDdlBuilderFactory.getBuilder(alterTableCommentDto.getDataBaseTypeEnum()).alterColumnComment(alterTableCommentDto);
    }

    public static String createView(CreateViewDto createViewDto) {
        return DatabaseDdlBuilderFactory.getBuilder(createViewDto.getDataBaseTypeEnum()).createView(createViewDto);
    }

    public static String alterView(AlterViewDto dto){
        return DatabaseDdlBuilderFactory.getBuilder(dto.getDataBaseTypeEnum()).alterView(dto);
    }

    public static String dropView(DropViewDto dto) {
        return DatabaseDdlBuilderFactory.getBuilder(dto.getDataBaseTypeEnum()).dropView(dto);
    }

    public static String createIndexSql(CreateIndexDto createIndexDto){
        return DatabaseIndexBuilderFactory.getBuilder(createIndexDto.getIndexType()).createIndex(createIndexDto);
    }

    public static String dropIndexSql(DropIndexDto dropIndexDto){
        return DatabaseIndexBuilderFactory.getBuilder(dropIndexDto.getIndexType()).dropIndex(dropIndexDto);
    }

    public static String alterTableIndexSql(AlterIndexDto alterIndexDto){
        return DatabaseIndexBuilderFactory.getBuilder(alterIndexDto.getIndexType()).alterTableIndexSql(alterIndexDto);
    }
}
