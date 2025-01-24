package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author xxx
 * @since 2024/4/19 13:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationParseResult {
    /**
     * sql语句的操作类型，可能存在多条，如INSERT XX SELECT * FROM XX
     * 就会有2条记录对应：INSERT XX.xxx，SELECT XX.xx
     * <p>目前SELECT和INSERT语句均使用EXPLAIN判断其权限，该字段暂时只有1个值</p>
     */
    @NonNull
    private Set<DatabaseOperation> databaseOperations;
}
