package cn.cisdigital.datakits.framework.model.dto.database;

import cn.cisdigital.datakits.framework.model.enums.AlterEnum;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xxx
 * @since 2022-11-08-18:46
 */
@Data
public class AlterIndexOperationDto implements Serializable {
    /**
     * 修改类型
     */
    @NotNull
    private AlterEnum alterEnum;

    /**
     * 变更后的索引信息
     */
    @Valid
    @NotNull
    private IndexAttrDto indexDto;

}
