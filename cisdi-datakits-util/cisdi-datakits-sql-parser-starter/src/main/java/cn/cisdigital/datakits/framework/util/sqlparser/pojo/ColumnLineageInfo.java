package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForColumnDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * 字段血缘信息
 *
 * @author xxx
 * @since 2024/4/28 15:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnLineageInfo {
    @NonNull
    private UniqueCodeForColumnDto targetColumn;
    @NonNull
    private UniqueCodeForColumnDto sourceColumn;
}
