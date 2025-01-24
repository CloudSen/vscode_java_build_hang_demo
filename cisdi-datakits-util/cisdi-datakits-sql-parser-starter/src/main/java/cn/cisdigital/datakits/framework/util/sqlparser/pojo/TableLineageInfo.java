package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.model.dto.lineage.UniqueCodeForTableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * 表血缘信息
 * @author xxx
 * @since 2024/4/28 15:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableLineageInfo {

    @NonNull
    private UniqueCodeForTableDto targetTable;
    @NonNull
    private UniqueCodeForTableDto sourceTable;
}
