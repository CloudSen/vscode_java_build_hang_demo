package cn.cisdigital.datakits.framework.model.dto.database;


import static cn.hutool.core.text.StrPool.DOT;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2023-04-24-8:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RenameTableNameDto extends TableBase {

    /**
     * 如果要修改表名字，传修改后的名字
     */
    String rename;

    public String getRenameFullTableName() {
        //todo 待确认，好像并不是所有数据库在schema或者表名层级，都完美支持转义符
        String wrappedSchema = super.dataBaseTypeEnum.wrappedEscapeCharacter(this.schema);
        String wrappedTableName = super.dataBaseTypeEnum.wrappedEscapeCharacter(this.rename);
        return wrappedSchema + DOT + wrappedTableName;
    }
}
