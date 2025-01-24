package cn.cisdigital.datakits.framework.mp.handler;

import cn.cisdigital.datakits.framework.model.interfaces.BaseEnum;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * mybatis 枚举转换策略
 *
 * @author xxx
 */
@MappedTypes(BaseEnum.class)
@MappedJdbcTypes(JdbcType.INTEGER)
public class EnumTypeHandler<T extends BaseEnum> extends BaseTypeHandler<T> {

    private final T[] enums;

    public EnumTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        } else {
            this.enums = type.getEnumConstants();
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            return;
        }
        if (jdbcType == null) {
            ps.setInt(i, parameter.getCode());
        } else {
            ps.setObject(i, parameter.getCode(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int enumValue = rs.getInt(columnName);
        return parseToEnum(enumValue);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int enumValue = rs.getInt(columnIndex);
        return parseToEnum(enumValue);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int enumValue = cs.getInt(columnIndex);
        return parseToEnum(enumValue);
    }

    private T parseToEnum(int enumValue) {
        return Arrays.stream(enums).filter(e -> e.getCode() == enumValue).findFirst().orElse(null);
    }
}
