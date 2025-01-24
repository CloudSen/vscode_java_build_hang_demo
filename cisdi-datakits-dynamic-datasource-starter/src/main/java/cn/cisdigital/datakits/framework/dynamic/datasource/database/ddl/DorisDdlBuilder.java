package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;

import cn.cisdigital.datakits.framework.model.dto.database.*;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.PartitionConfigDto;
import cn.cisdigital.datakits.framework.model.enums.AlterEnum;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.DorisColumnEnum;
import cn.cisdigital.datakits.framework.model.enums.DorisPartitionModelEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.*;
import static cn.cisdigital.datakits.framework.model.enums.AlterEnum.*;


/**
 * Doris的DDL构建者
 *
 * @author xxx
 */
@Slf4j
public class DorisDdlBuilder implements DatabaseDdlBuilderStrategy {

    @Override
    public DataBaseTypeEnum databaseType() {
        return DataBaseTypeEnum.DORIS;
    }

    @Override
    public String createTableSql(CreateDto createDto) {
        checkCreateDto(createDto);
        StringBuilder sqlBuilder = new StringBuilder();
        List<ColumnBase> columnList = createDto.getColumnList();
        DorisTableConfigDto dorisConfig = createDto.getDorisConfig();
        //doris 创建表语句 需要把key列放在前面,所以先排序
        List<ColumnBase> columnListOrder = columnList.stream().sorted(Comparator.comparing(ColumnBase::getPrimaryKey).reversed()).collect(Collectors.toList());
        sqlBuilder.append(CREATE_TABLE).append(BLANK).append(createDto.getFullyTableName()).append(LINE_BREAK).append(LEFT_BRACKET)
            .append(LINE_BREAK_AND_TABS);
        for (ColumnBase columnBase : columnListOrder) {
            sqlBuilder.append(singleColumnInfoBuild(columnBase)).append(COMMA).append(LINE_BREAK_AND_TABS);
        }
        // v2.4.0 新增索引语法
        if (CollUtil.isNotEmpty(createDto.getIndexAttrDtoList())) {
            String indexDdl = DatabaseIndexBuilderFactory.getBuilder(createDto.getIndexAttrDtoList().get(0).getIndexType()).createIndexSqlSegment(createDto.getIndexAttrDtoList());
            sqlBuilder.append(indexDdl);
            sqlBuilder.append(COMMA).append(LINE_BREAK_AND_TABS);
        }
        //删除多余逗号，换行符
        sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length());
        //右括号收尾
        sqlBuilder.append(LINE_BREAK).append(RIGHT_BRACKET).append(LINE_BREAK);
        //主键信息 eg: UNIQUE KEY(k1, k2)
        List<String> primaryKeyList = columnList.stream().filter(ColumnBase::getPrimaryKey).map(ColumnBase::getColumnName).collect(Collectors.toList());
        String primaryKeyToString = buildColumnNameList(primaryKeyList);
        if (CollUtil.isNotEmpty(primaryKeyList)) {
            sqlBuilder.append(dorisConfig.getDataModel().name()).append(BLANK).append(KEY).append(primaryKeyToString).append(LINE_BREAK);
        }
        //增加表描述
        if (Objects.nonNull(createDto.getTableComment())) {
            sqlBuilder.append(COMMENT).append(BLANK).append(QUOTE).append(createDto.getTableComment()).append(QUOTE).append(LINE_BREAK);
        }
        //分区配置 eg：PARTITION BY RANGE(k1)()
        PartitionConfigDto partitionConfig = dorisConfig.getPartitionConfig();
        if (dorisConfig.isPartition()) {
            DorisPartitionModelEnum partitionModel = partitionConfig.getPartitionModel();
            //获取分区字段
            List<String> partitionColumn = partitionConfig.getPartitionColumnList();
            if (CollectionUtils.isEmpty(partitionColumn)) {
                throw new RuntimeException("手动分区未配置分区字段");
            }
            String partitionColNames = buildColumnNameList(partitionColumn);
            //1.0版本只支持range分区
            sqlBuilder.append(PARTITION_BY).append(BLANK).append(partitionModel.name()).append(partitionColNames)
                .append(BLANK).append(LEFT_BRACKET).append(RIGHT_BRACKET).append(LINE_BREAK);
        }
        // 设置分桶 eg:DISTRIBUTED BY HASH (k1, k2) BUCKETS 1
        String bucketNum = dorisConfig.getBucketConfig().getBucketNum();
        List<String> bucketColumnList = dorisConfig.getBucketConfig().getBucketColumnList();
        String distributedNameList = buildColumnNameList(bucketColumnList);
        //如果没有设置
        sqlBuilder.append(DISTRIBUTED_BY_HASH).append(distributedNameList).append(BLANK).append(BUCKETS).append(BLANK).append(bucketNum);
        // 特殊配置  PROPERTIES ("replication_num" = "1");
        if (CollUtil.isNotEmpty(createDto.getProperties())) {
            sqlBuilder.append(LINE_BREAK);
            sqlBuilder.append(PROPERTIES).append(BLANK).append(LEFT_BRACKET);
            // 设置 replication_num 后续有其他属性，需要定制化增加
            String dorisProperties = buildDorisProperties(createDto.getProperties());
            sqlBuilder.append(dorisProperties).append(LINE_BREAK).append(RIGHT_BRACKET);
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
        String fullyTableName = renameDto.getFullyTableName();
        return String.format(ALTER_TABLE_RENAME, fullyTableName, renameDto.getRename());
    }

    /**
     * todo Doris修改表结构只支持：新增列ADD 删除列DROP 修改列类型MODIFY(仅仅支持结构修改，备注修改，是否非空以及默认值不做修改)
     *
     * @param alterDto
     * @return
     */
    @Override
    public String alterTableSql(AlterDto alterDto) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<AlterOperationDto> alterList = alterDto.getAlterList();
        //过滤change 和 主键修改枚举
        List<AlterOperationDto> operationDtoList = alterList.stream()
            .filter(dto -> !dto.getAlterEnum().equals(UPDATE_KEY))
            // 重新排序 ADD 放在 MODIFY 之前
            .sorted(Comparator.comparingInt(t -> t.getAlterEnum().getCode()))
            .collect(Collectors.toList());
        if (CollUtil.isEmpty(operationDtoList)) {
            log.error("支持修改的修改表的List<AlterOperationDto>对象为空,或者AlterEnum在Doris中不支持，无法构造sql");
            return null;
        }
        sqlBuilder.append(String.format(ALTER_TABLE, alterDto.getFullyTableName())).append(LINE_BREAK_AND_TABS);
        for (AlterOperationDto alterOperationDto : operationDtoList) {
            sqlBuilder.append(singleOperationAlter(alterOperationDto));
        }
        //删除尾部逗号和换行符
        sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length());
        sqlBuilder.append(SEMICOLON);
        return sqlBuilder.toString();
    }

    @Override
    public String buildColumnType(ColumnBase columnBase) {
        DorisColumnEnum dorisColumn = (DorisColumnEnum) columnBase.getColumnType();
        String colTypeUpperCase = dorisColumn.getType().toUpperCase();
        switch (dorisColumn) {
            case DORIS_CHAR:
            case DORIS_VARCHAR:
                return colTypeUpperCase + LEFT_BRACKET + columnBase.getPrecision() + RIGHT_BRACKET;
            case DORIS_DECIMAL:
                return colTypeUpperCase + LEFT_BRACKET + columnBase.getPrecision() + COMMA + columnBase.getScale() + RIGHT_BRACKET;
            default:
                return colTypeUpperCase;
        }
    }

    @Override
    public String alterTableComment(final AlterTableCommentDto alterTableCommentDto) {
        return String.format(ALTER_DORIS_TABLE_FORMAT, alterTableCommentDto.getSchema(), alterTableCommentDto.getTableName())
            + String.format(ALTER_DORIS_TABLE_COMMENT_FORMAT, alterTableCommentDto.getTableComment());
    }

    @Override
    public String alterColumnComment(final AlterColumnCommentDto alterColumnCommentDto) {
        if (CollectionUtils.isEmpty(alterColumnCommentDto.getColumnCommentDtos())) {
            throw new IllegalArgumentException("column comment list can not be null");
        }
        final StringBuilder builder = new StringBuilder(String.format(ALTER_DORIS_TABLE_FORMAT, alterColumnCommentDto.getSchema(), alterColumnCommentDto.getTableName()));
        final String modifyStr = alterColumnCommentDto.getColumnCommentDtos().stream()
            .map(columnDto -> String.format(ALTER_DORIS_COLUMN_COMMENT_FORMAT, columnDto.getColumnName(), columnDto.getColumnComment()))
            .collect(Collectors.joining(","));
        return builder.append(modifyStr).toString();
    }

    @Override
    public String createView(CreateViewDto dto) {
        StringBuilder ddl = new StringBuilder(String.format(CREATE_VIEW_TEMP, dto.getFullViewName()));
        String colDdl = dto.getColumnList().stream().map(col -> {
            if (CharSequenceUtil.isBlank(col.getComment())) {
                return col.getColumnName();
            } else {
                return col.getColumnName() + BLANK + COMMENT + BLANK + DOUBLE_QUOTE + col.getComment() + DOUBLE_QUOTE;
            }
        }).collect(Collectors.joining(",\n"));
        ddl.append(LEFT_BRACKET)
            .append(LINE_BREAK)
            .append(colDdl)
            .append(LINE_BREAK)
            .append(RIGHT_BRACKET)
            .append(LINE_BREAK);
        if (CharSequenceUtil.isNotBlank(dto.getViewComment())) {
            ddl.append(COMMENT)
                .append(BLANK)
                .append(DOUBLE_QUOTE)
                .append(dto.getViewComment())
                .append(DOUBLE_QUOTE)
                .append(LINE_BREAK);
        }
        ddl.append(AS)
            .append(LINE_BREAK)
            .append(dto.getSql());
        return ddl.toString();
    }

    @Override
    public String dropView(DropViewDto dto) {
        return String.format(DROP_VIEW_TEMP, dto.getFullViewName());
    }

    @Override
    public String alterView(AlterViewDto dto) {
        StringBuilder ddl = new StringBuilder(String.format(ALTER_VIEW_TEMP, dto.getFullViewName()));
        String colDdl = dto.getColumnList().stream().map(col -> {
            if (CharSequenceUtil.isBlank(col.getComment())) {
                return col.getColumnName();
            } else {
                return col.getColumnName() + BLANK + COMMENT + BLANK + DOUBLE_QUOTE + col.getComment() + DOUBLE_QUOTE;
            }
        }).collect(Collectors.joining(",\n"));
        ddl.append(LEFT_BRACKET)
            .append(LINE_BREAK)
            .append(colDdl)
            .append(LINE_BREAK)
            .append(RIGHT_BRACKET)
            .append(LINE_BREAK);
        ddl.append(AS)
            .append(LINE_BREAK)
            .append(dto.getSql());
        return ddl.toString();
    }

    @Override
    public String createIndex(CreateIndexDto dto) {
        return DatabaseIndexBuilderFactory.getBuilder(dto.getIndexType()).createIndex(dto);
    }

    @Override
    public String dropIndex(DropIndexDto dropIndexDto) {
        return DatabaseIndexBuilderFactory.getBuilder(dropIndexDto.getIndexType()).dropIndex(dropIndexDto);
    }

    @Override
    public String alterIndex(AlterIndexDto alterIndexDto) {
        return DatabaseIndexBuilderFactory.getBuilder(alterIndexDto.getIndexType()).alterTableIndexSql(alterIndexDto);
    }


    /**
     * 单行的字段信息构建，不带逗号
     *
     * @param columnBase
     * @return
     */
    private String singleColumnInfoBuild(ColumnBase columnBase) {
        StringBuilder sqlBuilder = new StringBuilder();
        //获取类型
        String columnType = this.buildColumnType(columnBase);
        //设置列名 列类型
        sqlBuilder.append(BACK_QUOTE).append(columnBase.getColumnName()).append(BACK_QUOTE).append(BLANK)
            .append(columnType);
        //设置聚合模型
        if (Objects.nonNull(columnBase.getAggregateType())) {
            sqlBuilder.append(BLANK).append(columnBase.getAggregateType().name());
        }
        //设置是否必填
        if (Objects.nonNull(columnBase.getRequired())) {
            if (Boolean.TRUE.equals(columnBase.getRequired())) {
                sqlBuilder.append(BLANK).append(NOT_NULL);
            }
        }
        //设置默认值
        if (Objects.nonNull(columnBase.getDefaultValue())) {
            sqlBuilder.append(BLANK).append(DEFAULT).append(BLANK).append(DOUBLE_QUOTE).append(columnBase.getDefaultValue()).append(DOUBLE_QUOTE);
        }
        //设置备注
        if (StringUtils.isNotBlank(columnBase.getComment())) {
            sqlBuilder.append(BLANK).append(COMMENT).append(BLANK).append(DOUBLE_QUOTE).append(columnBase.getComment()).append(DOUBLE_QUOTE);
        }
        return sqlBuilder.toString();
    }

    /**
     * 包装列名称
     *
     * @param columnList
     * @return (` k1 `, ` k2 `)
     */
    private String buildColumnNameList(List<String> columnList) {
        if (CollUtil.isEmpty(columnList)) {
            return "";
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(LEFT_BRACKET);
        for (String column : columnList) {
            sqlBuilder.append(BACK_QUOTE).append(column).append(BACK_QUOTE).append(COMMA);
        }
        //删除多余逗号
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(RIGHT_BRACKET);
        return sqlBuilder.toString();
    }

    /**
     * ADD(1,"增加列"),DROP(2,"删除列")
     * MODIFY(4,"对列内部属性修改，不涉及改列名"),
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
                buildAlterSql(sqlBuilder, columnList, ADD);
                break;
            case DROP:
                columnList.forEach(alterColumnDto -> {
                    sqlBuilder.append(DROP).append(BLANK).append(COLUMN).append(BLANK).append(BACK_QUOTE).append(alterColumnDto.getColumnName())
                        .append(BACK_QUOTE).append(COMMA).append(LINE_BREAK_AND_TABS);
                });
                break;
            case MODIFY:
            case CHANGE:
                buildAlterSql(sqlBuilder, columnList, MODIFY);
                break;
            default:
                break;
        }
        return sqlBuilder.toString();
    }

    private void buildAlterSql(StringBuilder sqlBuilder, List<AlterColumnDto> columnList, AlterEnum alterEnum) {
        columnList.forEach(alterColumnDto -> {
            //列类型
            String columnType = this.buildColumnType(alterColumnDto);
            //特殊处理doris modify, 如果是modify,doris不支持改名，维持原来的名字
            String colName;
            if (MODIFY.equals(alterEnum)) {
                colName = alterColumnDto.getOriginColumnName();
            } else {
                colName = alterColumnDto.getColumnName();
            }
            //构建sql
            sqlBuilder.append(alterEnum).append(BLANK).append(COLUMN).append(BLANK).append(BACK_QUOTE)
                .append(colName).append(BACK_QUOTE).append(BLANK)
                .append(columnType);
            if (Boolean.TRUE.equals(alterColumnDto.getPrimaryKey())) {
                sqlBuilder.append(BLANK).append(KEY);
            } else if (Objects.nonNull(alterColumnDto.getAggregateType())) {
                sqlBuilder.append(BLANK).append(alterColumnDto.getAggregateType().name());
            }
            if (Objects.nonNull(alterColumnDto.getComment())) {
                sqlBuilder.append(BLANK).append(COMMENT).append(BLANK).append(QUOTE).append(alterColumnDto.getComment()).append(QUOTE);
            }
            //设置位置
            if (alterColumnDto.isFirst()) {
                sqlBuilder.append(BLANK).append(FIRST);
            } else if (StringUtils.isNotBlank(alterColumnDto.getAfterColumnName())) {
                sqlBuilder.append(BLANK).append(AFTER).append(BLANK).append(BACK_QUOTE).append(alterColumnDto.getAfterColumnName()).append(BACK_QUOTE);
            }
            sqlBuilder.append(COMMA).append(LINE_BREAK_AND_TABS);
        });
    }

    private String buildDorisProperties(Properties properties) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> keySetNames = properties.stringPropertyNames();
        for (String propertyKey : keySetNames) {
            String value = properties.getProperty(propertyKey);
            stringBuilder.append(LINE_BREAK_AND_TABS).append(DOUBLE_QUOTE).append(propertyKey).append(DOUBLE_QUOTE).append(BLANK)
                .append(EQUAL).append(BLANK).append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE).append(COMMA);
        }
        //删除多余逗号
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void checkCreateDto(CreateDto createDto) {
        if (CollUtil.isEmpty(createDto.getColumnList())) {
            throw new RuntimeException("创建表的List<ColumnBase>对象为空");
        }
        if (Objects.isNull(createDto.getDorisConfig())) {
            throw new RuntimeException("创建Doris表的配置对象为空");
        }
    }
}
