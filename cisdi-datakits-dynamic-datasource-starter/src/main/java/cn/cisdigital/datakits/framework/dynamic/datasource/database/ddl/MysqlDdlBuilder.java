package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;

import cn.cisdigital.datakits.framework.model.dto.database.*;
import cn.cisdigital.datakits.framework.model.enums.MysqlColumnEnum;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.*;
import static cn.cisdigital.datakits.framework.model.enums.AlterEnum.*;

/**
 * MySQL的DDL构建者
 *
 * @author xxx
 */
public class MysqlDdlBuilder implements DatabaseDdlBuilderStrategy {


    @Override
    public DataBaseTypeEnum databaseType() {
        return DataBaseTypeEnum.MYSQL;
    }

    @Override
    public String createTableSql(CreateDto createDto) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<ColumnBase> columnList = createDto.getColumnList();
        if (CollUtil.isEmpty(columnList)) {
            throw new RuntimeException("创建表的List<ColumnBase>对象为空");
        }
        String fullyTableName = createDto.getFullyTableName();
        //eg: CREATE TABLE `schemaName`.`tableName` (
        sqlBuilder.append(CREATE_TABLE).append(BLANK).append(fullyTableName).append(BLANK)
                  .append(LEFT_BRACKET).append(LINE_BREAK_AND_TABS);
        //循环添加 eg: `id` bigint NOT NULL
        for (ColumnBase columnBase : columnList) {
            sqlBuilder.append(singleColumnInfoBuild(columnBase)).append(COMMA).append(LINE_BREAK_AND_TABS);
        }
        //设置主键 eg:  PRIMARY KEY (`id`,`bigint_22_col`)
        List<String> primaryKeyList = columnList.stream().filter(ColumnBase::getPrimaryKey).map(ColumnBase::getColumnName).collect(Collectors.toList());
        String primaryKey = buildPrimaryKey(primaryKeyList);
        if (StringUtils.isNotBlank(primaryKey)) {
            sqlBuilder.append(primaryKey);
        } else {
            //没有主键设置项，删除尾部逗号和换行符
            sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length());
        }
        //尾部括号收尾
        sqlBuilder.append(LINE_BREAK).append(RIGHT_BRACKET);
        //增加表描述
        if (Objects.nonNull(createDto.getTableComment())) {
            sqlBuilder.append(BLANK).append(COMMENT_EQUAL).append(QUOTE).append(createDto.getTableComment()).append(QUOTE);
        }
        sqlBuilder.append(SEMICOLON);
        return sqlBuilder.toString();
    }

    @Override
    public String dropTableSql(TableBase tableBase) {
        return String.format(DROP_TABLE, tableBase.getFullyTableName());
    }

    @Override
    public String renameTableSql(RenameTableNameDto renameDto) {
        return String.format(RENAME_TABLE, renameDto.getFullyTableName(), renameDto.getRenameFullTableName());
    }

    @Override
    public String alterTableSql(AlterDto alterDto) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<AlterOperationDto> alterList = alterDto.getAlterList();
        if (CollUtil.isEmpty(alterList)) {
            throw new RuntimeException("修改表的List<AlterOperationDto>对象为空");
        }
        sqlBuilder.append(String.format(ALTER_TABLE, alterDto.getFullyTableName())).append(LINE_BREAK_AND_TABS);
        for (AlterOperationDto alterOperationDto : alterList) {
            sqlBuilder.append(singleOperationAlter(alterOperationDto));
        }
        //删除尾部逗号和换行符
        sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length());
        sqlBuilder.append(SEMICOLON);
        return sqlBuilder.toString();
    }

    @Override
    public String buildColumnType(ColumnBase columnBase) {
        MysqlColumnEnum mysqlColumn = (MysqlColumnEnum)columnBase.getColumnType();
        String colTypeUpperCase = mysqlColumn.getType().toUpperCase();
        switch (mysqlColumn){
            case MYSQL_BIT:
            case MYSQL_CHAR:
            case MYSQL_VARCHAR:
            case MYSQL_BINARY:
            case MYSQL_VARBINARY:
                return colTypeUpperCase + LEFT_BRACKET + columnBase.getPrecision() + RIGHT_BRACKET;
            case MYSQL_DECIMAL:
                return colTypeUpperCase + LEFT_BRACKET + columnBase.getPrecision() + COMMA + columnBase.getScale() + RIGHT_BRACKET;
            case MYSQL_DATETIME:
            case MYSQL_TIME:
            case MYSQL_TIMESTAMP:
                if(Objects.nonNull(columnBase.getPrecision())){
                    return colTypeUpperCase + LEFT_BRACKET + columnBase.getPrecision() + RIGHT_BRACKET;
                }else {
                    return colTypeUpperCase;
                }
            case MYSQL_ENUM:
            case MYSQL_SET:
                return columnBase.getColumnTypeQualified();
            default:
                return colTypeUpperCase;
        }
    }

    @Override
    public String alterTableComment(final AlterTableCommentDto alterTableCommentDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String alterColumnComment(final AlterColumnCommentDto alterColumnCommentDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createView(CreateViewDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String dropView(DropViewDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String alterView(AlterViewDto dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createIndex(CreateIndexDto createIndexDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String dropIndex(DropIndexDto dropIndexDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String alterIndex(AlterIndexDto alterIndexDto) {
        throw new UnsupportedOperationException();
    }

    /**
     * 单行的字段信息构建，不带逗号
     *
     * @param columnBase
     * @return
     */
    private String singleColumnInfoBuild(ColumnBase columnBase) {
        StringBuilder sqlBuilder = new StringBuilder();
        //列类型
        String columnType = this.buildColumnType(columnBase);
        //设置列名
        sqlBuilder.append(BACK_QUOTE).append(columnBase.getColumnName()).append(BACK_QUOTE).append(BLANK)
                  .append(columnType);
        //设置是否为空，MySQL会自动添加默认值，除8.0的时间类型会报错
        if (Boolean.TRUE.equals(columnBase.getRequired())) {
            sqlBuilder.append(BLANK).append(NOT_NULL);
        } else {
            sqlBuilder.append(BLANK).append(NULL);
        }
        //设置备注
        if (StringUtils.isNotBlank(columnBase.getComment())) {
            sqlBuilder.append(BLANK).append(COMMENT).append(BLANK).append(QUOTE).append(columnBase.getComment()).append(QUOTE);
        }
        return sqlBuilder.toString();
    }
    /**
     * 单行的字段信息构建，不带逗号
     * 对于alter语句，不在修改mysql的必填信息，统一都是null 并且没有默认值
     * @param alterColumnDto
     * @return
     */
    private String singleAlterColumnInfoBuild(AlterColumnDto alterColumnDto) {
        StringBuilder sqlBuilder = new StringBuilder();
        //列类型
        String columnType = this.buildColumnType(alterColumnDto);
        //设置列名
        sqlBuilder.append(BACK_QUOTE).append(alterColumnDto.getColumnName()).append(BACK_QUOTE).append(BLANK)
                .append(columnType);
        //设置备注
        if (StringUtils.isNotBlank(alterColumnDto.getComment())) {
            sqlBuilder.append(BLANK).append(COMMENT).append(BLANK).append(QUOTE).append(alterColumnDto.getComment()).append(QUOTE);
        }
        //设置位置
        if(alterColumnDto.isFirst()){
            sqlBuilder.append(BLANK).append(FIRST);
        }else if(StringUtils.isNotBlank(alterColumnDto.getAfterColumnName())){
            sqlBuilder.append(BLANK).append(AFTER).append(BLANK).append(BACK_QUOTE).append(alterColumnDto.getAfterColumnName()).append(BACK_QUOTE);
        }
        return sqlBuilder.toString();
    }

    /**
     * ADD(1,"增加列"),DROP(2,"删除列"),CHANGE(3,"对列名修改等CHANGE操作"),
     * MODIFY(4,"对列内部属性修改，不涉及改列名"), UPDATE_KEY(5,"更新主键，逻辑为先删除主键，再添加主键"),
     * 尾行都有逗号，\n\t
     *
     * @param alterOperation
     * @return
     */
    private String singleOperationAlter(AlterOperationDto alterOperation) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<AlterColumnDto> columnList = alterOperation.getColumnList();
        if (CollUtil.isEmpty(columnList)) {
            throw new RuntimeException("修改表的List<AlterColumnDto>对象为空");
        }
        switch (alterOperation.getAlterEnum()) {
            case ADD:
                columnList.forEach(alterColumnDto -> {
                    sqlBuilder.append(ADD).append(BLANK).append(COLUMN).append(BLANK).append(singleAlterColumnInfoBuild(alterColumnDto))
                              .append(COMMA).append(LINE_BREAK_AND_TABS);
                });
                break;
            case DROP:
                columnList.forEach(alterColumnDto -> {
                    sqlBuilder.append(DROP).append(BLANK).append(COLUMN).append(BLANK).append(BACK_QUOTE).append(alterColumnDto.getColumnName())
                              .append(BACK_QUOTE).append(COMMA).append(LINE_BREAK_AND_TABS);
                });
                break;
            case CHANGE:
                columnList.forEach(alterColumnDto -> {
                    sqlBuilder.append(CHANGE).append(BLANK).append(COLUMN).append(BLANK).append(BACK_QUOTE)
                              .append(StringUtils.isNotBlank(alterColumnDto.getOriginColumnName()) ? alterColumnDto.getOriginColumnName()
                                          : alterColumnDto.getColumnName())
                              .append(BACK_QUOTE).append(BLANK).append(singleAlterColumnInfoBuild(alterColumnDto))
                              .append(COMMA).append(LINE_BREAK_AND_TABS);
                });
                break;
            case MODIFY:
                columnList.forEach(alterColumnDto -> {
                    sqlBuilder.append(MODIFY).append(BLANK).append(COLUMN).append(BLANK).append(singleAlterColumnInfoBuild(alterColumnDto))
                              .append(COMMA).append(LINE_BREAK_AND_TABS);
                });
                break;
            //先删除主键，在读取所有主键列 增加主键
            case UPDATE_KEY:
                List<String> primaryKeyList = columnList.stream().filter(ColumnBase::getPrimaryKey).map(ColumnBase::getColumnName).collect(Collectors.toList());
                if (CollUtil.isEmpty(primaryKeyList)) {
                    break;
                }
                sqlBuilder.append(DROP).append(BLANK).append(PRIMARY_KEY).append(COMMA).append(BLANK)
                          .append(ADD).append(BLANK).append(buildPrimaryKey(primaryKeyList))
                          .append(COMMA).append(LINE_BREAK_AND_TABS);
                break;
            default:
                break;
        }
        return sqlBuilder.toString();
    }

    /**
     * 构建主键
     *
     * @param primaryKeyList
     * @return
     */
    private String buildPrimaryKey(List<String> primaryKeyList) {
        if (CollUtil.isEmpty(primaryKeyList)) {
            return "";
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(PRIMARY_KEY).append(BLANK).append(LEFT_BRACKET);
        for (String column : primaryKeyList) {
            sqlBuilder.append(BACK_QUOTE).append(column).append(BACK_QUOTE).append(COMMA);
        }
        //删除多余逗号
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(RIGHT_BRACKET);
        return sqlBuilder.toString();
    }
}
