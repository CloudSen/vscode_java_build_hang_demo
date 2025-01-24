package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author xxx
 * @since 2024/4/19 13:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineageParseResult {
    /**
     * 表血缘结果
     */
    @Nullable
    private List<TableLineageInfo> tableLineageInfos;
    /**
     * 字段血缘结果
     */
    @Nullable
    private List<ColumnLineageInfo> columnLineageInfos;
}
