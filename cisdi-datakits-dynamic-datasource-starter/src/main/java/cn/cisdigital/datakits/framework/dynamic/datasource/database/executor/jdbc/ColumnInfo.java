package cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import cn.cisdigital.datakits.framework.model.enums.DorisColumnEnum;
import com.mysql.cj.result.Field;
import lombok.*;

import java.io.Serializable;

/**
 * 字段信息
 *
 * @author xxx
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnInfo implements Serializable {
    private static final long serialVersionUID = -2401163901865664310L;

    /**
     * 字段名字
     */
    private String name;

    /**
     * 字段类型
     */
    private ColumnType type;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 是否非空
     */
    private Boolean notNull;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否是主键索引字段
     * mysql主键/doris 维度列等等
     */
    private Boolean primaryKey = false;

    /**
     * 是否是分区字段
     */
    private Boolean partitionKey;

    /**
     * 是否自增字段
     */
    private Boolean autoIncrement;

    /**
     * 是否无符号
     */
    private Boolean unsigned;

    /**
     * 长度
     */
    private Long length;

    /**
     * 小数长度
     */
    private Integer decimalLength;

    public ColumnInfo(String name, ColumnType type, String comment, Boolean notNull, String defaultValue,
                      Boolean primaryKey, Boolean partitionKey,
                      Boolean autoIncrement, Boolean unsigned, Integer decimalLength) {
        this.name = name;
        this.type = type;
        this.comment = comment;
        this.notNull = notNull;
        this.defaultValue = defaultValue;
        this.primaryKey = primaryKey;
        this.partitionKey = partitionKey;
        this.autoIncrement = autoIncrement;
        this.unsigned = unsigned;
        this.decimalLength = decimalLength;
    }

    public static ColumnInfo of(Field field) {
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setName(field.getName());
        String mysqlTypeName = field.getMysqlType().getName();
        columnInfo.setType(DorisColumnEnum.parse(mysqlTypeName));
        columnInfo.setNotNull(field.isNotNull());
        columnInfo.setAutoIncrement(field.isAutoIncrement());
        columnInfo.setUnsigned(field.isUnsigned());
        columnInfo.setLength(field.getLength());
        columnInfo.setDecimalLength(field.getDecimals());

        handleJdbcDriverUnknownProperty(columnInfo);
        return columnInfo;
    }

    /**
     * 这里面所有属性都是jdbc驱动无法识别的信息, 这里单独列出来
     */
    private static void handleJdbcDriverUnknownProperty(ColumnInfo columnInfo) {
        // 这个dorisKey无法直接从元信息中获取, mysql的uniqueKey与doris的uniqueKey不是一回事, 所以暂时只能写死成false, 后续再想办法解析此字段
        columnInfo.setPrimaryKey(null);
        // 这个comment也无法直接从元信息中获取,所以暂时只能写死成null, 后续再想办法解析此字段
        columnInfo.setComment(null);
        // 这个defaultValue也无法直接从元信息中获取,所以暂时只能写死成null, 后续再想办法解析此字段
        columnInfo.setDefaultValue(null);
        // 这个partitionKey也无法直接从元信息中获取,所以暂时只能写死成null, 后续再想办法解析此字段
        columnInfo.setPartitionKey(null);
    }
}
