package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 字段解析结果封装类
 * @author xxx
 * @since 2024/4/19 14:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldParseResult {
    /**
     * 查询时指定的列名,eg:
     * <p>Create table (a int)</p>
     * <p>select a as aa from A : queryName = aa</p>
     * <p>select * from A : queryName = a</p>
     * <p>={@link FieldRecord#columnEnName}</p>
     */
    @NonNull
    private String queryName;

    /**
     * 查询字段依赖的来源字段信息列表
     * <p>可能为空，eg: select now();</p>
     */
    @Nullable
    private List<FieldRecord> originIds;

}
