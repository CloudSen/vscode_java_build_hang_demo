package cn.cisdigital.datakits.framework.util.sqlparser.catalog;



import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import org.apache.calcite.schema.Table;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * 该接口负责从已注册的catalog中读写元数据，如数据库/表/视图/udf。
 * <p>只处理永久元数据对象</p>
 */
public interface ICatalogDiscover {
    /**
     * 默认数据库名
     *
     * @return 返回默认数据库名
     * @throws BusinessException 运行时任何异常
     */
    String getDefaultDatabase() throws BusinessException;

    /**
     * 返回所有数据库名
     *
     * @return 数据库名
     * @throws BusinessException 运行时任何异常
     */
    Set<String> listDatabases() throws BusinessException;

    /**
     * 获取数据库下所有表名
     *
     * @return 数据库下表名集合
     * @throws BusinessException 运行时任何异常
     */
    Set<String> listTables(String databaseName) throws BusinessException;


    /**
     * 获取表元信息
     *
     * @param tablePath 表路径
     * @return 表信息
     * @throws BusinessException 运行时任何异常
     */
    Table getTable(ObjectPath tablePath) throws BusinessException;

    /**
     * 检查catalog中是否存在database
     *
     * @param databaseName database名称
     * @return 存在返回true
     * @throws BusinessException                  运行时任何异常
     */
    boolean databaseExists(String databaseName) throws BusinessException;

    /**
     * 检查catalog中是否存在table
     *
     * @param objectPath 表路径信息
     * @return 存在返回true
     * @throws BusinessException 运行时任何异常
     */
    boolean tableExists(ObjectPath objectPath) throws BusinessException;

    /**
     * catalog初始化
     *
     * @throws BusinessException 运行时任何异常
     */
    void open() throws BusinessException;

    /**
     * 当不再需要catalog时关闭它，并释放它可能持有的任何资源。
     *
     * @throws BusinessException 运行时任何异常
     */
    void close() throws BusinessException;
}
