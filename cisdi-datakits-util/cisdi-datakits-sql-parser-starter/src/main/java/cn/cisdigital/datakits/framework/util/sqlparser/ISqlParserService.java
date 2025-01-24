package cn.cisdigital.datakits.framework.util.sqlparser;

import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.LineageParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.OperationParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SelectParseResult;

/**
 * @author xxx
 * @since 2024/4/19 11:38
 */
public interface ISqlParserService {

    /**
     * 返回sql语句通过解析后的执行类型
     * <p>目前支持返回的类型:{@link ParseOperationEnum}</p>
     * <p>不支持的类型返回:{@link ParseOperationEnum#OTHERS}</p>
     *
     * @param param 执行参数
     * @return 执行类型
     */
    ParseOperationEnum checkSqlOperation(final ParseSqlParam param);

    /**
     * 解析select语句结果
     * <p>适用于数据服务，数据脱敏等场景</p>
     * <p>非SELECT预计解析会抛出异常</p>
     * <p>有兜底策略，可根据参数设置，兜底策略仅返回查询结果的字段信息，其他如数据安全分类分级，来源表字段等缺失</p>
     * <p>通过{@link cn.cisdigital.datakits.framework.model.dto.DataSourceDto#setSchemaName(String)}}指定默认数据库</p>
     *
     * @param param              param
     * @param enableBackStrategy true 开启兜底策略
     * @return 解析结果
     * @throws RuntimeException 数据源异常，sql错误时抛出
     */
    SelectParseResult parseSelect(final ParseSqlParam param, boolean enableBackStrategy) throws RuntimeException;


    /**
     * 解析血缘
     * <p>适用血缘解析</p>
     * <p>解析错误会抛出异常，业务方可根据异常信息，做一些标记处理</p>
     * <p>有些SQL可能不存在血缘关系，则血缘结果为空</p>
     *
     * @param param param
     * @return 解析结果
     * @throws RuntimeException 数据源异常，sql语法错误时抛出
     */
    LineageParseResult parseLineage(final ParseSqlParam param) throws RuntimeException;


    /**
     * 解析sql操作对象
     * <p>用于发布任务时，访问控制校验</p>
     * <p>目前使用Druid进行解析，解析错误会抛出异常</p>
     * <p>代码式开发任务中限制了相关sql操作类型的白名单，目前支持针对库，表，视图，相关CURD操作，非白名单的操作类型，会抛出异常</p>
     *
     * @param param param
     * @return 解析结果
     * @throws RuntimeException sql语法错误，非白名单的操作类型，druid解析异常
     */
    OperationParseResult parseOperation(final ParseSqlParam param) throws RuntimeException;
}
