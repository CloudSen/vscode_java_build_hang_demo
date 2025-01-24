package cn.cisdigital.datakits.framework.mp.interceptor.convert;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * MybatisPlus SQL字段转换服务
 *
 * @author xxx
 * @since 2023/9/14 9:03
 */
public interface DbFieldConvertService {

    /**
     * 转换sql
     *
     * @param sql 原始sql
     * @return 转换后的sql
     */
    default String convertSql(String sql) {
        return sql;
    }

    /**
     * 数据库类型
     *
     * @return 数据库类型
     */
    DbType getDbType();
}
