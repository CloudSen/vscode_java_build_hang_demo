package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import lombok.Data;

/**
 * 解析sql入参
 * @author xxx
 * @since 2024/4/19 16:20
 */
@Data
public class ParseSqlParam {
    /**
     * 数据源连接
     */
    private DataSourceDto dataSourceDto;

    /**
     * 单条sql语句
     */
    private String sql;
}
