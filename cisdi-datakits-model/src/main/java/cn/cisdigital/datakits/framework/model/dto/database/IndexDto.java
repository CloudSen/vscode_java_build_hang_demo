package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.IndexType;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeDeserializer;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * @author xxx
 * @since 2024/9/2 15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexDto implements Serializable {
    private static final long serialVersionUID = -4541825465043039412L;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 索引字段
     */
    private List<String> columnNameLists;

    /**
     * 索引属性
     */
    private Properties properties;

    /**
     * 索引注释
     */
    private String indexComment;

    /**
     * 索引类型
     */
    @JsonSerialize(using = IndexTypeSerializer.class)
    @JsonDeserialize(using = IndexTypeDeserializer.class)
    private IndexType indexType;
}
