package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xxx
 * @since 2024/4/18 18:34
 */
@Data
@NoArgsConstructor
public class SqlParserConfig {
    /**
     * 大小写是否敏感
     */
    private boolean caseSensitive;
}
