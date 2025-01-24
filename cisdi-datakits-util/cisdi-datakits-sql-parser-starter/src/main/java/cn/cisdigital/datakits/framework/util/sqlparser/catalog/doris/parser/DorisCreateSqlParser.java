package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.util.FunctionUtil;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris.parser.model.CreateSqlParserDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 属性信息解析器
 *
 * @author xxx
 */
public interface DorisCreateSqlParser {

    String LEFT_PARENTHESIS = "(";
    String RIGHT_PARENTHESIS = ")";
    String LEFT_ANGLE_BRACKET = "<";
    String RIGHT_ANGLE_BRACKET = ">";
    String DOUBLE_QUOTATION = "\"";
    String ASTERISK = "*";
    Long CHAR_MAX_LENGTH = 255L;
    Long VARCHAR_MAX_LENGTH = 65533L;
    long DORIS_DECIMAL_DEFAULT_PRECISION = 9L;
    int DORIS_DECIMAL_DEFAULT_SCALE = 0;
    /**
     * 动态分区的起始偏移, 如果不人为指定, 则默认值是 -2147483648, "dynamic_partition.start" = "-2147483648"
     */
    Integer DYNAMIC_PARTITION_DEFAULT_START = -2147483648;
    /**
     * 动态分区需要创建的历史分区数, 如果不人为指定, 则默认值是 -1, 即不设置历史分区数量, "dynamic_partition.history" = "-1"
     */
    Integer DYNAMIC_PARTITION_DEFAULT_HISTORY_PARTITION = -1;

    /**
     * 解析属性信息，责任链模式
     *
     * @param dataSource 数据源的连接信息
     * @param parserInfo 建表信息解析参数
     * @return 下一个解析器需要解析的建表信息
     * @throws BusinessException 抛出异常中断解析
     */
    default List<CreateSqlParserDto> parseBatch(DataSourceDto dataSource, List<CreateSqlParserDto> parserInfo) throws BusinessException {
        return parserInfo.stream()
                .map(info -> FunctionUtil.get(() -> parse(dataSource, info), this + " 解析失败" + info.getSurplusSql()))
                .collect(Collectors.toList());
    }

    /**
     * 解析属性信息，责任链模式
     *
     * @param dataSource 数据源的连接信息
     * @param parserInfo 建表信息解析参数
     * @return 下一个解析器需要解析的建表信息
     * @throws BusinessException 抛出异常中断解析
     */
    CreateSqlParserDto parse(DataSourceDto dataSource, CreateSqlParserDto parserInfo) throws BusinessException;

}
