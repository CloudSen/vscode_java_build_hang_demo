package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库中获取的字段共性信息
 *
 * @author xxx
 * @since 2024/4/22 16:12
 */
@Data
@NoArgsConstructor
public class ColumnInfoFromDatabase {
    private String typeString;
    private String comment;

    @Builder
    public ColumnInfoFromDatabase(final String typeString, final String comment) {
        this.typeString = typeString;
        this.comment = comment;
    }
}
