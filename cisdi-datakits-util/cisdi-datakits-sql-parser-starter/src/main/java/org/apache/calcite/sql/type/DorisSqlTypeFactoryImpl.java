package org.apache.calcite.sql.type;

import cn.cisdigital.datakits.framework.model.enums.DorisColumnEnum;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.RelDataTypeSystemImpl;
import org.apache.calcite.sql.SqlCollation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * doris字段类型处理工厂类
 *
 * @author xxx
 */
public class DorisSqlTypeFactoryImpl extends SqlTypeFactoryImpl {

    /**
     * @param typeSystem type系统。提供有关类型限制和行为的行为。
     *                   例如，在默认系统中，DECIMAL的最大精度为19，但Hive将其覆盖到38.
     *                   默认实现是{@link RelDataTypeSystemImpl}。
     */
    public DorisSqlTypeFactoryImpl(final RelDataTypeSystem typeSystem) {
        super(typeSystem);
    }

    /**
     * 创建封装了doris类型的RelDataType
     *
     * @param typeName        typeName
     * @param dorisColumnEnum dorisType
     * @return RelDataType
     */
    public RelDataType createSqlType(SqlTypeName typeName, DorisColumnEnum dorisColumnEnum, String comment) {
        this.assertBasic(typeName);
        // 这里主要是从数据库解析出类型后，创建，目前暂时统一设置可为空
        return new DorisBasicSqlType(this.typeSystem, typeName, dorisColumnEnum, comment, true);
    }

    @Override
    public RelDataType createTypeWithNullability(final RelDataType type, final boolean nullable) {
        // 不需要判断是否空值，防止取缓存，重写直接返回
        return type;
    }


    @Override
    public RelDataType createSqlType(final SqlTypeName typeName) {
        assertBasic(typeName);
        // 这里主要是由语法树解析出来SQL中的参数，如xx = "balabala"这类的类型，默认是设置为不可为空
        RelDataType newType = new DorisBasicSqlType(typeSystem, typeName);
        // 设置字符集
        newType = SqlTypeUtil.addCharsetAndCollation(newType, this);
        return canonize(newType);
    }

    @Override
    public RelDataType createSqlType(final SqlTypeName typeName, final int precision) {
        // 暂不处理精度
        return createSqlType(typeName);
    }

    @Override
    public RelDataType createSqlType(final SqlTypeName typeName, final int precision, final int scale) {
        // 暂不处理精度,刻度
        return createSqlType(typeName);
    }

    @Override
    public RelDataType createTypeWithCharsetAndCollation(final RelDataType type, final Charset charset, final SqlCollation collation) {
        assert SqlTypeUtil.inCharFamily(type) : type;
        requireNonNull(charset, "charset");
        requireNonNull(collation, "collation");
        RelDataType newType;
        DorisBasicSqlType sqlType = (DorisBasicSqlType) type;
        newType = sqlType.createWithCharsetAndCollation(charset, collation);
        return canonize(newType);
    }

    @Override
    public Charset getDefaultCharset() {
        return StandardCharsets.UTF_8;
    }

    private void assertBasic(SqlTypeName typeName) {
        assert typeName != null;

        assert typeName != SqlTypeName.MULTISET : "use createMultisetType() instead";

        assert typeName != SqlTypeName.ARRAY : "use createArrayType() instead";

        assert typeName != SqlTypeName.MAP : "use createMapType() instead";

        assert typeName != SqlTypeName.ROW : "use createStructType() instead";

        assert !SqlTypeName.INTERVAL_TYPES.contains(typeName) : "use createSqlIntervalType() instead";

    }
}
