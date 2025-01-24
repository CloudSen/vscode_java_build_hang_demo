package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.DorisIndexTypeEnum;
import cn.cisdigital.datakits.framework.model.enums.IndexType;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeDeserializer;
import cn.cisdigital.datakits.framework.model.enums.IndexTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 视图修改DTO
 *
 * @author xxx
 * @since 2022-11-04-15:35
 */
@Data
public class AlterIndexDto extends TableBase implements Serializable {
    /**
     * 索引类型 2.4 默认只支持倒排索引
     */
    @JsonSerialize(using = IndexTypeSerializer.class)
    @JsonDeserialize(using = IndexTypeDeserializer.class)
    private IndexType indexType = DorisIndexTypeEnum.DORIS_INVERTED;

    /**
     * 批量索引操作
     */
    @NotEmpty
    @Valid
    private List<AlterIndexOperationDto> alterIndexOperationDtoList;

}
