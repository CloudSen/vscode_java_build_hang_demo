package cn.cisdigital.datakits.framework.dynamic.datasource.database.engine;

import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import lombok.Cleanup;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * database引擎相关服务策略
 *
 * @author xxx
 */
public interface DatabaseEngineStrategy<T> {

    /**
     * 当前构建者绑定的数据库类型
     */
    DataBaseTypeEnum databaseType();

    /**
     * 获取查询数据库引擎信息的SQL
     */
    String getQueryEnginesSql();

    /**
     * 获取查询数据库引擎信息的SQL
     */
    Class<T> getEngineModelClass();

    /**
     * 检查真实版本信息
     */
    boolean doCheckActualDatabaseType(List<T> engineModelList);

    /**
     * 检查数据库部署实际类型是否匹配
     */
    default boolean checkActualDatabaseType(DataSourceDto sourceDto) {
        @Cleanup HikariDataSource source = (HikariDataSource) DynamicDataSourceHolder.createDataSourcePool(sourceDto);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
        List<T> engineModelList = jdbcTemplate.query(this.getQueryEnginesSql(), BeanPropertyRowMapper.newInstance(this.getEngineModelClass()));
        return this.doCheckActualDatabaseType(engineModelList);
    }
}
