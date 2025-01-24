package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import cn.cisdigital.datakits.framework.model.enums.ColumnTypeDeserializer;
import cn.cisdigital.datakits.framework.model.enums.ColumnTypeSerializer;
import cn.cisdigital.datakits.framework.model.enums.DorisAggregateEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author xxx
 * @since 2022-11-03-10:34
 * 基准列
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ColumnBase implements Serializable {

    /**
     * 列名
     */
    String columnName;
    /**
     * 列类型字段枚举, 依赖底层各个数据库列类型枚举
     */
    @JsonSerialize(using = ColumnTypeSerializer.class)
    @JsonDeserialize(using = ColumnTypeDeserializer.class)
    ColumnType columnType;
    /**
     * 数值精度 字符长度
     */
    Long precision;
    /**
     * 标度
     */
    Integer scale;
    /**
     * 是否主键
     */
    @Builder.Default
    Boolean primaryKey = Boolean.FALSE;
    /**
     * 是否带有无符号标致，true：带有符号位信息  默认不带符号位
     */
    @Builder.Default
    Boolean unsigned = Boolean.FALSE;
    /**
     * 是否必填
     */
    Boolean required;
    /**
     * 默认值
     */
    String defaultValue;
    /**
     * 字段描述 注释 desc
     */
    String comment;
    /**
     * Doris特有的聚合类型
     */
    DorisAggregateEnum aggregateType;
    /**
     * 带有精度信息的建表语句DDL的数据类型 比如bigint(20)，int(10) unsigned
     */
    String columnTypeQualified;
}
