package cn.cisdigital.datakits.framework.model.dto.lineage;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 构建数仓表唯一标识dto
 * @author xxx
 * @since 2024/4/20 15:24
 */
@Data
public class UniqueCodeForTableDto {
    @NotNull
    private DataSourceDto dataSourceDto;
    @NotNull
    private String schemaName;
    @NotNull
    private String tableName;
}
