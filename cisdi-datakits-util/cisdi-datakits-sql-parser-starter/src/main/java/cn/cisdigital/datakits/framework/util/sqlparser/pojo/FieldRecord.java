package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2024/4/18 9:10
 */
@Data
@NoArgsConstructor
public class FieldRecord {
    /**
     * 字段名
     */
    private String columnEnName;

    /**
     * 字段所属物理数据库
     */
    private String fromDataBase;

    /**
     * 字段所属物理表名
     */
    private String fromTableName;

    /**
     * 字段类型
     */
    private ColumnType columnType;

    /**
     * 字段描述
     */
    private String comment;

    /**
     * 字段元数据唯一标识
     */
    private String code;


    @Builder
    public FieldRecord(String columnEnName, String tableName, String originDatabase, ColumnType columnType, String comment) {
        this.setColumnEnName(columnEnName);
        this.setFromDataBase(originDatabase);
        this.setFromTableName(tableName);
        this.setColumnType(columnType);
        this.setComment(comment);
    }
}
