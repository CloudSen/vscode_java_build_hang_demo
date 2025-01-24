package cn.cisdigital.datakits.framework.util.sqlparser.parser.operations;

import cn.cisdigital.datakits.framework.model.enums.OperationTypeEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xxx
 * @since 2024/4/28 8:48
 */
@Slf4j
public class RegularOperationTypeConvertor {

    private static final String pattern = "^\\s*(USE|SHOW|SELECT|INSERT|UPDATE|UPSHERT|DELETE|DROP|TRUNCATE)\\b";

    private static final Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

    public OperationTypeEnum convert(final ParseSqlParam param) {
        Matcher matcher = regex.matcher(param.getSql());
        if (matcher.find()) {
            String sqlType = matcher.group(1).toUpperCase();
            switch (sqlType) {
                case "USE":
                case "SHOW":
                case "SELECT": {
                    return OperationTypeEnum.READ;
                }
                case "INSERT":
                case "LOAD":
                case "UPDATE":
                case "UPSERT": {
                    return OperationTypeEnum.UPDATE;
                }
                case "DELETE":
                case "DROP":
                case "TRUNCATE": {
                    return OperationTypeEnum.DROP;
                }
                case "CREATE": {
                    return OperationTypeEnum.CREATE;
                }
                case "ALTER": {
                    return OperationTypeEnum.ALTER;
                }
                default:

            }
        }
        log.warn("Illegal sql {}", param.getSql());
        return OperationTypeEnum.OTHERS;
    }
}
