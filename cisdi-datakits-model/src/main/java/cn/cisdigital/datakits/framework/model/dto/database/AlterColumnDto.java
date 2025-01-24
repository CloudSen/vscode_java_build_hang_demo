package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlterColumnDto extends ColumnBase {

    /**
     * 如果要修改名字，需要传修改前的名字  该字段用于删除，改名
     * 如果originColumnName 为空字符串 且是CHANGE 操作，originColumnName = columnName
     */
    String originColumnName;
    /**
     * 设置位置的字段 eg:  ADD COLUMN column_name column_type [KEY | agg_type] [AFTER column_name|FIRST]
     * 如何不会设置 默认最后
     */
    String afterColumnName;
    /**
     * alter add 是否放在首位
     */
    boolean first = false;

    public AlterColumnDto(ColumnBase columnBase){
        this.setColumnName(columnBase.getColumnName());
        this.setColumnType(columnBase.getColumnType());
        this.setComment(columnBase.getComment());
        this.setDefaultValue(columnBase.getDefaultValue());
        this.setPrecision(columnBase.getPrecision());
        this.setPrimaryKey(columnBase.getPrimaryKey());
        this.setRequired(columnBase.getRequired());
        this.setScale(columnBase.getScale());
        this.setUnsigned(columnBase.getUnsigned());
        this.setAggregateType(columnBase.getAggregateType());
    }

}
