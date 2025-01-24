package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.DorisIndexTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.IndexType;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeDeserializer;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 视图删除DTO
 *
 * @author xxx
 * @since 2022-11-04-15:35
 */
@Data
public class DropIndexDto extends TableBase implements Serializable {

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

}
