package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 视图修改DTO
 *
 * @author xxx
 * @since 2022-11-04-15:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterViewDto extends ViewBase implements Serializable {

    /**
     * 列信息
     */
    @NotEmpty
    private List<@NotNull ColumnBase> columnList;

    /**
     * SQL
     */
    @NotBlank
    private String sql;

}
