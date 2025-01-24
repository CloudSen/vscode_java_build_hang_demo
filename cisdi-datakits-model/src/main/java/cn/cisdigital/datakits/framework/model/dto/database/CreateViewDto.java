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
 * 视图创建DTO
 *
 * @author xxx
 * @since 2022-11-04-13:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateViewDto extends ViewBase implements Serializable {

    /**
     * 列信息
     */
    @NotEmpty
    private List<@NotNull ColumnBase> columnList;

    /**
     * 视图描述
     */
    private String viewComment;

    /**
     * SQL
     */
    @NotBlank
    private String sql;

}


