package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations;

import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.OperationParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;

/**
 * 数据库操作类型转换
 *
 * @author xxx
 * @since 2024/4/28 8:41
 */
public interface IOperationResultParse {

    /**
     * 解析sql操作对象
     *
     * @param param param
     * @return 解析结果
     */
    OperationParseResult databaseOperationObjectParse(final ParseSqlParam param);

    /**
     * 解析sql操作类型
     *
     * @param param param
     * @return 解析结果
     */
    ParseOperationEnum checkSqlOperation(final ParseSqlParam param);
}
