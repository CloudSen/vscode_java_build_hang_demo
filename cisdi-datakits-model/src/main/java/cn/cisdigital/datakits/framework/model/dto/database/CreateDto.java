package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableConfigDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * @author xxx
 * @since 2022-11-04-13:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDto extends TableBase implements Serializable{

    /**
     * 列信息
     */
    List<ColumnBase> columnList;
    /**
     * 针对某些特定数据库，需要额外建表的定制信息
     */
    Properties properties;
    /**
     * 表描述
     */
    String tableComment;
    /**
     * doris建表配置
     */
    DorisTableConfigDto dorisConfig;

    /**
     * 索引配置
     */
    List<IndexAttrDto> indexAttrDtoList;
}


