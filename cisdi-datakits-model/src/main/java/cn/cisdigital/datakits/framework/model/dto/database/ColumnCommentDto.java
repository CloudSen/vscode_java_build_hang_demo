package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author xxx
 * @since 2024/6/7 17:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnCommentDto {
    /**
     * 列名
     */
    @NotEmpty(message = "列名不能为空")
    private String columnName;

    /**
     * 列描述
     */
    @NotEmpty(message = "列名不能为空")
    private String columnComment;
}
