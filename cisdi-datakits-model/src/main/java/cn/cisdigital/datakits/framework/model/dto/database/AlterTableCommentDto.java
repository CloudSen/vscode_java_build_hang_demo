package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author xxx
 * @since 2024/6/7 17:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AlterTableCommentDto extends TableBase{

    /**
     * 表描述
     */
    @NotEmpty(message = "描述不能为空")
    private String tableComment;
}
