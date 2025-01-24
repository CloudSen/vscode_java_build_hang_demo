package cn.cisdigital.datakits.framework.model.dto.doris;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 库表信息
 *
 * @author xxx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DatabaseTableDto {

    /**
     * 库名称
     */
    String databaseName;
    /**
     * 表名称
     */
    String tableName;
}
