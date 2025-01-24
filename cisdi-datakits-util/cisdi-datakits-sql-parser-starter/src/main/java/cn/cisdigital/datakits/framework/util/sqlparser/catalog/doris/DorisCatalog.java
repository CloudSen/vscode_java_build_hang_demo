package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris;

import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.AbstractCatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ObjectPath;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ColumnInfoFromDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.schema.Table;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * doris catalog
 *
 * @author xxx
 */
@Slf4j
public class DorisCatalog extends AbstractCatalogDiscover {

    /**
     * 查看表的元数据
     */
    private static final String DESC_SQL_FORMAT = "SHOW FULL COLUMNS FROM %s";
    /**
     * 查看数据库
     */
    private static final String SHOW_DATABASE_FORMAT = "SHOW DATABASES like '%s';";

    private static final String SHOW_ALL_DATABASE_FORMAT = "SHOW DATABASES";

    /**
     * 检查表是否存在
     */
    private static final String SHOW_TABLE_FORMAT = "SHOW TABLES FROM `%s` like '%s';";
    /**
     * desc结果Field列
     */
    protected static final String FIELD_KEY = "Field";
    /**
     * desc结果Type列
     */
    protected static final String TYPE_KEY = "Type";
    /**
     * comment列
     */
    protected static final String COMMENT_KEY = "Comment";
    /**
     * desc结果Key列
     */
    protected static final String KEY_KEY = "Key";

    private static final String KEY_FORMAT = "%s.%s";

    public DorisCatalog(String defaultDatabase, DataSourceDto dataSourceDto) {
        super(defaultDatabase, dataSourceDto);
    }

    @Override
    public Table getTable(ObjectPath objectPath) throws BusinessException {
        Preconditions.checkArgument(tableExists(objectPath), "table not exists " + objectPath);
        final String tableName = getTableName(objectPath);
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(String.format(DESC_SQL_FORMAT, tableName))) {
            final Map<String, ColumnInfoFromDatabase> props = new LinkedHashMap<>();
            while (rs.next()) {
                props.put(rs.getString(FIELD_KEY),
                        ColumnInfoFromDatabase.builder()
                                .typeString(getFieldType(rs.getString(TYPE_KEY)))
                                .comment(rs.getString(COMMENT_KEY))
                                .build()
                );
            }
            return DorisTable.of(props);
        } catch (SQLException sqlException) {
            log.error("getTable connection error", sqlException);
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public boolean databaseExists(String databaseName) throws BusinessException {
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(String.format(SHOW_DATABASE_FORMAT, databaseName))) {
            return rs.next();
        } catch (SQLException sqlException) {
            log.error("databaseExists connection error", sqlException);
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public Set<String> listDatabases() throws BusinessException {
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(SHOW_ALL_DATABASE_FORMAT)) {
            final Set<String> databases = new HashSet<>();
            while (rs.next()) {
                databases.add(rs.getString(1));
            }
            return databases;
        } catch (SQLException sqlException) {
            log.error("listDatabases connection error", sqlException);
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public boolean tableExists(ObjectPath objectPath) throws BusinessException {
        final String sql = String.format(SHOW_TABLE_FORMAT, objectPath.getDatabaseName(), objectPath.getObjectName());
        log.info("check table exists sql : {}", sql);
        try (Connection connection = DynamicDataSourceHolder.getConnection(dataSourceDto);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            return rs.next();
        } catch (SQLException sqlException) {
            log.error("tableExists connection error", sqlException);
            throw new RuntimeException(sqlException.getMessage());
        }
    }

    @Override
    public Set<String> listTables(final String s) throws BusinessException {
        // 当表不存在时，会走到这个方法里，所以直接返回空
        return Collections.emptySet();
    }

    /**
     * doris元数据返回的类型带有长度，如CHAR(32)
     *
     * @param dorisTypeStr doris返回结果
     * @return 类型字符串
     */
    private String getFieldType(String dorisTypeStr) {
        return dorisTypeStr.split("\\(")[0];
    }

    private String getTableName(ObjectPath objectPath) {
        if (StringUtils.isEmpty(objectPath.getDatabaseName())) {
            return objectPath.getObjectName();
        }
        return String.format(KEY_FORMAT, objectPath.getDatabaseName(), objectPath.getObjectName());
    }


}
