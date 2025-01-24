package org.apache.calcite.sql.type;

import cn.cisdigital.datakits.framework.model.enums.ColumnType;
import cn.cisdigital.datakits.framework.model.enums.DorisColumnEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.IDatakitsFieldType;
import com.google.common.base.Preconditions;
import lombok.Builder;
import org.apache.calcite.rel.type.RelDataTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.sql.SqlCollation;
import org.apache.calcite.util.SerializableCharset;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * @author xxx
 * @see DorisBasicSqlType
 */
@Deprecated
public class DorisDatakitsFieldType extends BasicSqlType implements IDatakitsFieldType {

    private static final long serialVersionUID = -3153064397826373987L;

    private final ColumnType columnType;

    private final String comment;

    private final SqlTypeName currentSqlType;

    @Builder
    public DorisDatakitsFieldType(final RelDataTypeSystem typeSystem, final SqlTypeName typeName,
                                  final DorisColumnEnum dorisColumnEnum,
                                  final String comment,
                                  final boolean nullable) {
        super(typeSystem, typeName, nullable);
        this.columnType = dorisColumnEnum;
        this.currentSqlType = typeName;
        this.comment = comment;
    }

    /**
     * 重写equals方法，以doris type为判断依据，防止获取到错误缓存
     * <p>{@link RelDataTypeFactoryImpl#canonize(StructKind, List, List, boolean)}</p>
     *
     * @param obj obj
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DorisDatakitsFieldType)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final DorisDatakitsFieldType other = (DorisDatakitsFieldType) obj;
        if (Objects.isNull(this.columnType) || Objects.isNull(other.columnType)) {
            return Objects.equals(this.typeName, other.typeName);
        }
        return this.columnType.getType().equals(other.columnType.getType());
    }

    /**
     * 重写hashcode方法
     *
     * @return int
     */
    public int hashCode() {
        return Objects.hash(columnType);
    }

    @Override
    public ColumnType getFieldColumnType() {
        return columnType;
    }

    @Override
    public String getFieldComment() {
        return comment;
    }
}
