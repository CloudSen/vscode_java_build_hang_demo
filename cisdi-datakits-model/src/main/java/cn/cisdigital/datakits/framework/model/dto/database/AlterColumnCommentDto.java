package cn.cisdigital.datakits.framework.model.dto.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author xxx
 * @since 2024/6/7 17:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AlterColumnCommentDto extends TableBase {

    /**
     * 修改列注释信息
     */
    private List<ColumnCommentDto> columnCommentDtos;
}
