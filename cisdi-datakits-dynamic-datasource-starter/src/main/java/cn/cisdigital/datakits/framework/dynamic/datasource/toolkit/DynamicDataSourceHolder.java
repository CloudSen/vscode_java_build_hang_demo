package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;


import cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants;
import cn.cisdigital.datakits.framework.dynamic.datasource.config.HikariDefaultConfig;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.engine.DatabaseEngineStrategy;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.engine.DatabaseEngineStrategyFactory;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.JdbcNullValueRowMapper;
import cn.cisdigital.datakits.framework.dynamic.datasource.exception.SqlExecuteErrorCode;
import cn.cisdigital.datakits.framework.dynamic.datasource.exception.SqlExecuteException;
import cn.cisdigital.datakits.framework.dynamic.datasource.utils.PasswordUtils;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.TestResultDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.model.util.SpringContext;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static cn.cisdigital.datakits.framework.dynamic.datasource.common.Constants.*;


/**
 * @author xxx
 * @since 2022-08-05-15:14
 * 1.修改为工具类 不和spring耦合
 * 2.维护MapCache，传递DTO，重写hashcode equal
 * 3.扩展性考虑
 */
@Slf4j
public class DynamicDataSourceHolder {

    public static final String ZH = "zh";
    public static final String ABAP_AS_WITH_POOL = "ABAP_AS_WITH_POOL";

    /**
     * 默认cache过期时间
     */
    private static Long expireTime = MAX_EXPIRE_TIME_TO_CACHE_ACCESS;

    /**
     * 将驱动程序等待 Statement 对象执行的秒数设置为给定的秒数。默认情况下，运行语句完成所允许的时间量没有限制。如果超出限制，则会引发 SQLTimeoutException,默认20秒
     */
    private static int statementTime = STATEMENT_TIMEOUT;
    /**
     * 数据源池子
     */
    private static LoadingCache<DataSourceDto, JdbcTemplate> dataSourceMapCache;

    static {
        DynamicDataSourceHolder.init();
    }

    public static void init() {
        dataSourceMapCache = CacheBuilder.newBuilder().expireAfterAccess(expireTime, TimeUnit.MINUTES)
                .removalListener((RemovalListener<DataSourceDto, JdbcTemplate>) notification -> {
                    JdbcTemplate jdbc = notification.getValue();
                    HikariDataSource source;
                    if (jdbc != null && (source = (HikariDataSource) jdbc.getDataSource()) != null) {
                        // 关闭连接池
                        source.close();
                        DataSourceDto sourceDto = notification.getKey();
                        log.info(notification.getCause() + "--->  数据源[ {} ]已被移除...", sourceDto != null ? sourceDto.getUrl() : "");
                    }
                }).build(new CacheLoader<DataSourceDto, JdbcTemplate>() {
                    @Override
                    public JdbcTemplate load(DataSourceDto dataSourceDto) throws Exception {
                        log.info("新增数据源[ {} ]...", dataSourceDto.getUrl());
                        HikariDataSource source = (HikariDataSource) createDataSourcePool(dataSourceDto);
                        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
                        if (Objects.nonNull(dataSourceDto.getQueryTimeout())) {
                            jdbcTemplate.setQueryTimeout(dataSourceDto.getQueryTimeout());
                        } else {
                            jdbcTemplate.setQueryTimeout(STATEMENT_TIMEOUT);
                        }
                        return jdbcTemplate;
                    }
                });

    }


    /**
     * 获取JDBC对象
     */
    public static JdbcTemplate getJdbcTemplate(DataSourceDto dataSourceDto) {
        try {
            return dataSourceMapCache.get(dataSourceDto);
        } catch (ExecutionException e) {
            log.error("获取数据源JdbcTemplate失败，url : {}", dataSourceDto.getUrl());
            throw new RuntimeException("数据源连接[ " + dataSourceDto.getUrl() + " ]错误..." + e.getCause(), e);
        }
    }

    /**
     * 获取JDBC Connection
     */
    public static Connection getConnection(DataSourceDto dataSourceDto) {
        try {
            return dataSourceMapCache.get(dataSourceDto).getDataSource().getConnection();
        } catch (ExecutionException | SQLException e) {
            log.error("获取数据源Connection失败，url : {}", dataSourceDto.getUrl());
            throw new RuntimeException("数据源连接[ " + dataSourceDto.getUrl() + " ]错误..." + e.getCause(), e);
        }
    }

    /**
     * 移除缓存
     */
    public static void deleteCache(DataSourceDto dataSourceDto) {
        JdbcTemplate jdbc = dataSourceMapCache.getIfPresent(dataSourceDto);
        if (Objects.nonNull(jdbc)) {
            HikariDataSource dataSource = (HikariDataSource) jdbc.getDataSource();
            if (Objects.nonNull(dataSource)) {
                dataSource.close();
            }
        }
        dataSourceMapCache.invalidate(dataSourceDto);
        log.info("已经移除连接缓存，Url = {}", dataSourceDto.getUrl());
    }

    /**
     * 执行DDL sql,如果url没有携带数据库，需要添加数据库信息
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     */
    public static void executeDDL(DataSourceDto dataSourceDto, String sql) {

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_DDL_FAIL, dataSourceDto, e);
        }
    }


    /**
     * 执行DDL sql
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     */
    public static void execute(DataSourceDto dataSourceDto, String sql) {

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_DDL_FAIL, dataSourceDto, e);
        }
    }

    /**
     * 批量执行DML语句，执行增删改语句 返回操作的列，如果有多个，返回的一个
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     * @return the number of rows affected
     */
    public static int[] batchExecuteDML(DataSourceDto dataSourceDto, final String... sql) {

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            return jdbcTemplate.batchUpdate(sql);
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_DML_FAIL, dataSourceDto, e);
        }
    }

    /**
     * 执行DML语句，执行增删改语句 返回操作的列，如果有多个，返回的一个
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     * @return the number of rows affected
     */
    public static int executeDML(DataSourceDto dataSourceDto, String sql) {

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            return jdbcTemplate.update(sql);
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_DML_FAIL, dataSourceDto, e);
        }
    }

    /**
     * 查询
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     */
    public static <T> List<T> queryForList(DataSourceDto dataSourceDto, String sql, Class<T> mappedClass) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(mappedClass));
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_QUERY_FAIL, dataSourceDto, e);
        }

    }

    /**
     * 查询
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     */
    public static List<Map<String, Object>> queryForList(DataSourceDto dataSourceDto, String sql) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            return jdbcTemplate.query(sql,new JdbcNullValueRowMapper());
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_QUERY_FAIL, dataSourceDto, e);
        }

    }

    /**
     * 查询
     *
     * @param sql SQL需要显示指明database/schema 信息，因为数据源可能没有数据库信息
     */
    public static Map<String, Object> queryForMap(DataSourceDto dataSourceDto, String sql) {
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSourceDto);
            return jdbcTemplate.queryForObject(sql, new JdbcNullValueRowMapper());
        } catch (DataAccessException e) {
            throw getSqlExecuteException(SqlExecuteErrorCode.EXECUTE_QUERY_FAIL, dataSourceDto, e);
        }

    }

    /**
     * 统一处理权限异常信息，返回简要可读信息
     *
     * @param dataSourceDto 数据源信息
     * @param e             异常信息
     * @return
     */
    private static SqlExecuteException getSqlExecuteException(SqlExecuteErrorCode sqlExecuteErrorCode, DataSourceDto dataSourceDto, DataAccessException e) {
        if (e instanceof BadSqlGrammarException) {
            SQLException sqlException = ((BadSqlGrammarException) e).getSQLException();
            int errorCode = sqlException.getErrorCode();
            String sqlState = sqlException.getSQLState();
            if (StringUtils.equals(Constants.DATASOURCE_ACCESS_DENIED_ERROR_SQLSTATE, sqlState) && Constants.DATASOURCE_ACCESS_DENIED_ERROR_CODES.contains(errorCode)) {
                SqlExecuteException sqlExecuteException = new SqlExecuteException(sqlExecuteErrorCode, dataSourceDto.getDataBaseTypeEnum(), dataSourceDto.getHost(), dataSourceDto.getPort(), dataSourceDto.getSchemaName(), dataSourceDto.getUsername());
                log.error(sqlExecuteException.getMessage(), e);
                return sqlExecuteException;
            }
        }
        log.error("sql执行失败", e);
        return new SqlExecuteException(SqlExecuteErrorCode.EXECUTE_FAIL, e.getMessage());
    }

    /**
     * 判断数据源是否存在
     */
    public static boolean isExist(DataSourceDto dataSourceDto) {
        return null != dataSourceMapCache.getIfPresent(dataSourceDto);
    }

    /**
     * 测试连通性及检查数据库部署实际类型是否匹配
     */
    public static TestResultDto testConnectionAndActualDatabaseType(DataSourceDto dataSourceDto) {
        TestResultDto testResultDto = testConnection(dataSourceDto);
        if (testResultDto.isResult()) {
            return checkActualDatabaseType(dataSourceDto);
        }
        return testResultDto;
    }

    /**
     * 测试连通性
     */
    public static TestResultDto testConnection(DataSourceDto dataSourceDto) {
        DataBaseTypeEnum dataBaseTypeEnum = dataSourceDto.getDataBaseTypeEnum();
        // 当Access文件较大时，使用连接池的方式验证会导致前端页面请求超时
        if (DataBaseTypeEnum.MS_ACCESS.equals(dataBaseTypeEnum)) {
            return testAccessConnection(dataSourceDto);
        }
        TestResultDto resultDto = new TestResultDto();
        HikariDataSource dataSource = null;
        try {
            dataSourceDto.setMaximumPoolSize(1);
            dataSourceDto.setMinimumIdle(1);
            HikariConfig hikariConfig = HikariDefaultConfig.getDefaultConfig(dataSourceDto);
            String driverClassName = StringUtils.isNotBlank(dataSourceDto.getDriverClassName()) ? dataSourceDto.getDriverClassName() : dataBaseTypeEnum.getDriverClassName();
            hikariConfig.setDriverClassName(driverClassName);
            hikariConfig.setJdbcUrl(dataSourceDto.getUrl());
            hikariConfig.setUsername(dataSourceDto.getUsername());
            hikariConfig.setPassword(dataSourceDto.getPassword());
            //特殊处理oracle
            specialDealJDBCProperties(dataSourceDto, hikariConfig);
            if (Objects.nonNull(dataSourceDto.getConnectionTimeoutMs())) {
                hikariConfig.setConnectionTimeout(dataSourceDto.getConnectionTimeoutMs());
            } else {
                //等待来自池的连接的最大毫秒数
                hikariConfig.setConnectionTimeout(15000);
            }
            dataSource = new HikariDataSource(hikariConfig);
            resultDto.setResult(true);
            return resultDto;
        } catch (Exception e) {
            log.error("数据源[ {} ]连接测试未成功...", dataSourceDto.getUrl(), e);
            resultDto.setResult(false);
            resultDto.setErrorMsg(e.getMessage());
            return resultDto;
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception e) {
                    log.error("数据源[ {} ]关闭失败...", dataSourceDto.getUrl(), e);
                }
            }
        }
    }

    private static TestResultDto testAccessConnection(DataSourceDto dataSourceDto) {
        TestResultDto resultDto = new TestResultDto();
        // 连接基础信息验证
        String url = dataSourceDto.getUrl();
        if (StringUtils.isEmpty(url)) {
            resultDto.setResult(false);
            resultDto.setErrorMsg("文件地址不能为空");
            return resultDto;
        }
        String[] params = url.split(ACCESS_PARAM_SEPARATOR);
        // 验证目标文件是否存在
        String filePath = params[0].replace(ACCESS_PROTOCOL_PREFIX, "");
        File file = new File(filePath);
        if (file.isDirectory() || !file.exists()) {
            resultDto.setResult(false);
            resultDto.setErrorMsg("目标文件不存在或未指定具体文件");
            return resultDto;
        }
        // 验证是否可建立缓存文件夹
        String cacheFolderParent = Arrays.stream(params)
                .filter(p -> p.startsWith(ACCESS_PARAM_MIRROR_FOLDER_KEY))
                .map(p -> p.split(ACCESS_KV_SEPARATOR)[1])
                .map(v -> JAVA_IO_TMPDIR_KEY.equals(v) ? System.getProperty(JAVA_IO_TMPDIR_KEY) : v)
                .findFirst()
                .orElse(file.getParent());
        File cacheFolder = new File(cacheFolderParent + File.separator + "cache_temp_" + System.nanoTime());
        try {
            if (cacheFolder.mkdirs() && cacheFolder.delete()) {
                resultDto.setResult(true);
                resultDto.setErrorMsg("Access验证缓存文件夹权限成功, url: " + url + ", cacheFolderParent: " + cacheFolderParent);
                return resultDto;
            }
        } catch (Exception e) {
            log.error("Access验证缓存文件夹权限失败, url: " + url, e);
        }
        resultDto.setResult(false);
        resultDto.setErrorMsg("采集服务器用户权限不足，请授予" + cacheFolderParent + "目录下创建/修改/删除文件夹及文件的权限");
        return resultDto;
    }

    /**
     * 检查数据库部署实际类型是否匹配
     */
    public static TestResultDto checkActualDatabaseType(DataSourceDto dataSourceDto) {
        TestResultDto resultDto = new TestResultDto();
        try {
            DatabaseEngineStrategy engineStrategy = DatabaseEngineStrategyFactory.getEngine(dataSourceDto.getDataBaseTypeEnum());
            if (engineStrategy == null || engineStrategy.checkActualDatabaseType(dataSourceDto)) {
                resultDto.setResult(true);
                return resultDto;
            }

            resultDto.setResult(false);
            String errorMsg = String.format(CHECK_DATABASE_TYPE_ERROR_MESSAGE,
                    DataBaseTypeEnum.MYSQL.equals(dataSourceDto.getDataBaseTypeEnum()) ? DataBaseTypeEnum.DORIS : DataBaseTypeEnum.MYSQL);
            resultDto.setErrorMsg(errorMsg);
        } catch (Exception e) {
            // 因网络等原因报错的验证失败，不影响后续操作
            resultDto.setResult(true);
            log.error("检查数据库部署实际类型是否匹配失败", e);
        }
        return resultDto;
    }

    /**
     * 创建连接池，目前只支持hikari连接池,使用默认配置
     */
    public static DataSource createDataSourcePool(DataSourceDto dataSourceDto) {
        try {
            HikariConfig hikariConfig = HikariDefaultConfig.getDefaultConfig(dataSourceDto);
            String driverClassName = StringUtils.isNotBlank(dataSourceDto.getDriverClassName()) ? dataSourceDto.getDriverClassName() : dataSourceDto.getDataBaseTypeEnum().getDriverClassName();
            hikariConfig.setDriverClassName(driverClassName);
            hikariConfig.setJdbcUrl(dataSourceDto.getUrl());
            hikariConfig.setUsername(dataSourceDto.getUsername());
            hikariConfig.setPassword(dataSourceDto.getPassword());
            Optional.ofNullable(SpringContext.getBean(MeterRegistry.class))
                .ifPresent(meterRegistry -> {
                    hikariConfig.setMetricRegistry(meterRegistry);
                    hikariConfig.setPoolName(buildPoolName(dataSourceDto));
                });
            //todo Since 19c, the connection properties can be added at the end of the URL
            //oracle 19c以前的版本，jdbc中的配置无法写入url中，需要单独处理
            specialDealJDBCProperties(dataSourceDto, hikariConfig);
            // 如果传了schema添加默认数据库名
            if(CharSequenceUtil.isNotBlank(dataSourceDto.getSchemaName())){
                hikariConfig.addDataSourceProperty(PropertyKey.DBNAME.getKeyName(),dataSourceDto.getSchemaName());
            }
            return new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            log.error("构建数据源连接[ " + dataSourceDto.getUrl() + " ]错误...");
            throw new RuntimeException("构建数据源连接[ " + dataSourceDto.getUrl() + " ]错误..." + e.getCause(), e);
        }
    }

    /**
     * 构造连接池名字
     * @param dataSourceDto 连接参数
     * @return 数据库类型-url-user-passwordDigest，如MYSQL-jdbc://xxxxx-admin-mydb-iashdhsiuh
     */
    private static String buildPoolName(DataSourceDto dataSourceDto) {
        DataBaseTypeEnum dbType = dataSourceDto.getDataBaseTypeEnum();
        return String.format("%s-%s-%s-%s-%s",
            dataSourceDto.getPoolName(),
            dbType.name(),
            dataSourceDto.getUrl(),
            Optional.ofNullable(dataSourceDto.getUsername()).orElse("DefaultUser"),
            Optional.ofNullable(dataSourceDto.getPassword()).map(pwd -> PasswordUtils.encrypt(Constants.KEY_DEFINE, pwd)).orElse("DefaultPwd"));
    }

    private static void specialDealJDBCProperties(DataSourceDto dataSourceDto, HikariConfig hikariConfig) {
        if (DataBaseTypeEnum.ORACLE != dataSourceDto.getDataBaseTypeEnum()) {
            return;
        }
        String url = dataSourceDto.getUrl();
        String[] split = url.split("\\?");
        if (split.length > 1 && StringUtils.isNotBlank(split[1])) {
            String[] propertyList = split[1].split("&");
            Properties properties = new Properties();
            if (propertyList.length > 0) {
                Arrays.stream(propertyList).forEach(pro -> {
                    String[] splitProperty = pro.split("=");
                    if (splitProperty.length != 2) {
                        return;
                    }
                    if ("user".equals(splitProperty[0]) || "password".equals(splitProperty[0])) {
                        return;
                    }
                    properties.setProperty(splitProperty[0], splitProperty[1]);
                });
            }
            hikariConfig.setDataSourceProperties(properties);
            hikariConfig.setJdbcUrl(split[0]);
        }
    }

    /**
     * 创建连接池，目前只支持hikari连接池,使用自定义配置,但是url/用户/密码/驱动信息会覆盖配置
     */
    private static DataSource createDataSourcePool(DataSourceDto dataSourceDto, HikariConfig hikariConfig) {
        try {
            hikariConfig.setUsername(dataSourceDto.getUsername());
            hikariConfig.setJdbcUrl(dataSourceDto.getUrl());
            hikariConfig.setPassword(dataSourceDto.getPassword());
            hikariConfig.setDriverClassName(dataSourceDto.getDatabaseDriver());
            return new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            log.error("构建数据源连接[ " + dataSourceDto.getUrl() + " ]错误...");
            throw new RuntimeException("构建数据源连接[ " + dataSourceDto.getUrl() + " ]错误..." + e.getCause(), e);
        }
    }

}
