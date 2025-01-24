package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 数据库操作，一条SQL可能对应多条数据库操作
 *
 * @author xxx
 * @since 2024/4/19 16:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DatabaseOperation {
    /**
     * 操作类型
     */
    @NonNull
    private ParseOperationEnum parseOperationEnum;

    /**
     * 操作的数据库
     */
    @Nullable
    private String dbName;

    /**
     * 操作的表(可能为空)
     */
    @Nullable
    private String tableName;

    /**
     * 是否可执行explain，默认为false
     * <p>如果是true，代表需要通过EXPLAIN判断权限，因为同样是写权限，update和delete不能执行explain</p>
     */
    private boolean explainEnable;
}
