package cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl;

import cn.cisdigital.datakits.framework.model.dto.database.AlterIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.CreateIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.DropIndexDto;
import cn.cisdigital.datakits.framework.model.dto.database.IndexAttrDto;
import cn.cisdigital.datakits.framework.model.enums.AlterEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.*;

/**
 * @author xxx
 * @date 2024/8/28 11:12
 **/
public class InvertedIndexBuilder implements DatabaseIndexBuilderStrategy {
    @Override
    public String createIndex(CreateIndexDto dto) {
        StringBuilder ddl = new StringBuilder(String.format(CREATE_INDEX_TEMP,
            dto.getFullyTableName(),
            this.escapeSqlKeyword(dto.getIndexName()),
            dto.getColumnNameLists().stream().map(this::escapeSqlKeyword).collect(Collectors.joining(",")),
            dto.getIndexType().getIndexTypeName()
        ));
        return getCreateIndexAndCommentSqlPostfix(this.createIndexDto2AttrDto(dto), ddl);
    }

    @Override
    public String dropIndex(DropIndexDto dropIndexDto) {
        return String.format(DROP_INDEX_TEMP, dropIndexDto.getFullyTableName(), this.escapeSqlKeyword(dropIndexDto.getIndexName()));
    }

    @Override
    public String createIndexSqlSegment(List<IndexAttrDto> createIndexDtoList) {
        return createIndexDtoList.stream().map(dto -> {
            StringBuilder ddl = new StringBuilder(String.format(CREATE_INDEX_WITH_TABLE_SQL_SEGMENT, this.escapeSqlKeyword(dto.getIndexName()), dto.getColumnNameLists().stream().map(this::escapeSqlKeyword).collect(Collectors.joining(",")), dto.getIndexType().getIndexTypeName()));
            return getCreateIndexAndCommentSqlPostfix(dto, ddl);
        }).collect(Collectors.joining("," + LINE_BREAK));
    }

    @Override
    public String alterTableIndexSql(AlterIndexDto dto) {
        StringBuilder ddl = new StringBuilder(String.format(ALTER_DORIS_TABLE_FORMAT, dto.getSchema(), dto.getTableName()));
        ddl.append(LINE_BREAK);
        dto.getAlterIndexOperationDtoList().forEach(indexDto -> {
            switch (indexDto.getAlterEnum()) {
                case DROP:
                    ddl.append(String.format(ALTER_DROP_INDEX_TEMP_SEG, this.escapeSqlKeyword(indexDto.getIndexDto().getIndexName())));
                    break;
                case ADD:
                    ddl.append(AlterEnum.ADD.getKeyword()).append(BLANK);
                    ddl.append(this.createIndexSqlSegment(CollUtil.newArrayList(indexDto.getIndexDto())));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            ddl.append(COMMA).append(LINE_BREAK);
        });
        return CharSequenceUtil.replaceLast(ddl.toString(), COMMA, "");
    }


    /**
     * 用于生成创建索引 properties 和 comment尾巴那一截
     */
    private String getCreateIndexAndCommentSqlPostfix(IndexAttrDto dto, StringBuilder ddl) {
        if (Objects.nonNull(dto.getProperties()) && !dto.getProperties().isEmpty()) {
            ddl.append(LINE_BREAK)
                .append(PROPERTIES)
                .append(LEFT_BRACKET)
                .append(this.buildDorisProperties(dto.getProperties()))
                .append(LINE_BREAK)
                .append(RIGHT_BRACKET)
                .append(LINE_BREAK);
        }
        ddl.append(BLANK);
        if (CharSequenceUtil.isNotBlank(dto.getIndexComment())) {
            ddl.append(COMMENT)
                .append(BLANK)
                .append(DOUBLE_QUOTE)
                .append(dto.getIndexComment())
                .append(DOUBLE_QUOTE);
        }
        return ddl.toString();
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

    private IndexAttrDto createIndexDto2AttrDto(CreateIndexDto indexDto) {
        IndexAttrDto attrDto = new IndexAttrDto();
        attrDto.setIndexType(indexDto.getIndexType());
        attrDto.setIndexName(indexDto.getIndexName());
        attrDto.setColumnNameLists(indexDto.getColumnNameLists());
        attrDto.setProperties(indexDto.getProperties());
        attrDto.setIndexComment(indexDto.getIndexComment());
        return attrDto;
    }

    private String escapeSqlKeyword(String keyword) {
        return BACK_QUOTE + keyword + BACK_QUOTE;
    }

}
