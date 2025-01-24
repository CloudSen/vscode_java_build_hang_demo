package cn.cisdigital.datakits.framework.util.sqlparser.catalog;

/**
 * @author xxx
 */
public interface IFunctionConfig {
    /**
     * 函数名
     */
    String getFunctionName();

    /**
     * 函数入参，数组方式，可选值：CHARACTER,BINARY,NUMERIC,DATE,TIME,TIMESTAMP,BOOLEAN,INTERVAL_YEAR_MONTH,INTERVAL_DAY_TIME,STRING,APPROXIMATE_NUMERIC,EXACT_NUMERIC,DECIMAL,INTEGER,DATETIME,DATETIME_INTERVAL,MULTISET,ARRAY,MAP,NULL,ANY,CURSOR,COLUMN_LIST,GEO,IGNORE;
     */
    String getFunctionArgumentTypes();

    /**
     * 函数出参，可选值：BOOLEAN,TINYINT,SMALLINT,INTEGER,BIGINT,DECIMAL,FLOAT,DOUBLE,DATE,TIME,TIMESTAMP
     */
    String getFunctionReturnType();

    /**
     * 精度
     */
    Integer getFunctionReturnPrecision();
}
