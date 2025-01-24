package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations;

import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.DatabaseOperation;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;

/**
 * 解析每一种操作类型，获取其操作的库，表等信息
 *
 * @author xxx
 * @since 2024/4/28 8:41
 */
public interface IOperationTypeParse {

    /**
     * 操作解析结果
     *
     * @param param param
     * @return 操作结果
     */
    DatabaseOperation databaseOperationParse(final ParseSqlParam param);

    /**
     * 操作类型
     *
     * @return 操作类型
     */
    ParseOperationEnum getParseOperationEnum();
}
