package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static cn.hutool.core.text.StrPool.DOT;

/**
 * @author xxx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TableBase implements Serializable {

    /**
     * 数据库类型
     */
    DataBaseTypeEnum dataBaseTypeEnum;
    /**
     * MySQL、Doris 为数据库名
     * Oracle、DB2、PGSQL、SQL Server 为schema名
     */
    String schema;
    /**
     * 操作的表
     */
    String tableName;

    public String getFullyTableName() {
        //todo 待确认，好像并不是所有数据库在schema或者表名层级，都完美支持转义符
        String wrappedSchema = this.dataBaseTypeEnum.wrappedEscapeCharacter(this.schema);
        String wrappedTableName = this.dataBaseTypeEnum.wrappedEscapeCharacter(this.tableName);
        return wrappedSchema + DOT + wrappedTableName;
    }
}
