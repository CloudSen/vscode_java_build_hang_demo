package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.Data;

@Data
public class SqlParseMetaInfo {
    /**
     * 字段中文名
     */
    private String columnName;
    /**
     * 字段英文名
     */
    private String columnEnName;
    /**
     * 字段描述
     */
    private String columnDesc;
    /**
     * 表英文名
     */
    private String tableEnName;
    /**
     * 表中文名
     */
    private String tableName;

    /**
     * 字段类型
     */
    private String columnType;
}
