package cn.cisdigital.datakits.framework.util.sqlparser.pojo;

import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString.Exclude;

/**
 * @author xxx
 * @since 2022-08-05-14:24
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class ContextDataSourceDto {
    /**
     * 数据库名字 mysql/doris为数据库名，其他数据库为schema名字
     */
    @EqualsAndHashCode.Exclude
    private String schemaName;

    /**
     * 数据源类型
     */
    @NonNull
    @EqualsAndHashCode.Include
    private DataBaseTypeEnum dataBaseTypeEnum;

    /**
     * JDBC url 地址
     */
    @NonNull
    @EqualsAndHashCode.Exclude
    private String url;

    /**
     * JDBC 用户名
     */
    @EqualsAndHashCode.Include
    private String username;

    /**
     * JDBC 密码
     */
    @Exclude
    @EqualsAndHashCode.Include
    private String password;

    /**
     * JDBC 驱动
     */
    @EqualsAndHashCode.Exclude
    private String databaseDriver;

    /**
     * 数据源IP地址
     */
    @EqualsAndHashCode.Include
    private String host;

    /**
     * 数据源端口
     */
    @EqualsAndHashCode.Include
    private Integer port;

    /**
     * 重写get方法
     */
    public String getDatabaseDriver() {
        return this.dataBaseTypeEnum.getDriverClassName();
    }
}
