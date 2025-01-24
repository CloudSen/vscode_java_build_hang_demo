package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.DorisIndexTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.IndexType;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeDeserializer;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * 视图创建DTO
 *
 * @author xxx
 * @since 2022-11-04-15:35
 */
@Data
public class CreateIndexDto extends TableBase implements Serializable {

    /**
     * 索引类型 2.4 默认只支持倒排索引
     */
    @JsonSerialize(using = IndexTypeSerializer.class)
    @JsonDeserialize(using = IndexTypeDeserializer.class)
    private IndexType indexType = DorisIndexTypeEnum.DORIS_INVERTED;


    /**
     * 索引名
     */
    @NotBlank
    private String indexName;

    /**
     * 索引列名集合
     */
    @NotEmpty
    private List<@NotNull String> columnNameLists;

    /**
     * 索引额外属性
     */
    private Properties properties;

    /**
     * 索引注释
     */
    private String indexComment;


}
