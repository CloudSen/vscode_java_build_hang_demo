package cn.cisdigital.datakits.framework.util.sqlparser.catalog.doris;

import cn.cisdigital.datakits.framework.model.enums.DorisColumnEnum;
import org.apache.calcite.sql.type.DorisSqlTypeFactoryImpl;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ColumnInfoFromDatabase;
import com.mysql.cj.MysqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Util;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * 从Doris获取的表数据。
 */
@Slf4j
public class DorisTable extends AbstractTable {

    private final Map<String, ColumnInfoFromDatabase> columnProperties;

    private DorisTable(Map<String, ColumnInfoFromDatabase> columnProperties) {
        this.columnProperties = columnProperties;
    }

    public static DorisTable of(Map<String, ColumnInfoFromDatabase> columnProperties) {
        return new DorisTable(columnProperties);
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        return buildRelProtoDataType(relDataTypeFactory);
    }

    private RelDataType buildRelProtoDataType(RelDataTypeFactory relDataTypeFactory) {
        final DorisSqlTypeFactoryImpl typeFactory = (DorisSqlTypeFactoryImpl) relDataTypeFactory;
        final RelDataTypeFactory.Builder builder = typeFactory.builder();
        columnProperties.forEach((columnName, infoFromDatabase) -> {
            final int dataType = getMySqlType(infoFromDatabase.getTypeString());
            // 暂不涉及精度
            final int precision = -1;
            // 暂时不涉及位数
            final int scale = -1;
            RelDataType sqlType = sqlType(typeFactory, dataType, precision, scale, infoFromDatabase);
            boolean nullable = true;
            builder.add(columnName, sqlType).nullable(nullable);
        });
        // 这里面涉及缓存获取
        // org.apache.calcite.rel.type.RelDataTypeFactoryImpl.canonize(org.apache.calcite.rel.type.StructKind, java.util.List<java.lang.String>, java.util.List<org.apache.calcite.rel.type.RelDataType>, boolean)
        // 判断是否有cache
        // org.apache.calcite.rel.type.RelDataTypeFactoryImpl.keyToType 构造返回值
        return builder.build();
    }

    private static RelDataType sqlType(DorisSqlTypeFactoryImpl typeFactory, int dataType,
                                       int precision, int scale, @Nullable ColumnInfoFromDatabase infoFromDatabase) {
        final String typeString = infoFromDatabase.getTypeString();
        // Fall back to ANY if type is unknown
        final SqlTypeName sqlTypeName =
                Util.first(SqlTypeName.getNameForJdbcType(dataType), SqlTypeName.ANY);
        DorisColumnEnum columnType = DorisColumnEnum.parse(typeString);
        if (Objects.isNull(columnType)) {
            log.warn("convert doris type {} failed no result found in DorisColumnEnum", typeString);
            columnType = DorisColumnEnum.DORIS_STRING;
        }
        return typeFactory.createSqlType(sqlTypeName, columnType, infoFromDatabase.getComment());
    }

    private static RelDataType parseTypeString(RelDataTypeFactory typeFactory,
                                               String typeString) {
        int precision = -1;
        int scale = -1;
        int open = typeString.indexOf("(");
        if (open >= 0) {
            int close = typeString.indexOf(")", open);
            if (close >= 0) {
                String rest = typeString.substring(open + 1, close);
                typeString = typeString.substring(0, open);
                int comma = rest.indexOf(",");
                if (comma >= 0) {
                    precision = Integer.parseInt(rest.substring(0, comma));
                    scale = Integer.parseInt(rest.substring(comma));
                } else {
                    precision = Integer.parseInt(rest);
                }
            }
        }
        try {
            final SqlTypeName typeName = SqlTypeName.valueOf(typeString);
            return typeName.allowsPrecScale(true, true)
                    ? typeFactory.createSqlType(typeName, precision, scale)
                    : typeName.allowsPrecScale(true, false)
                    ? typeFactory.createSqlType(typeName, precision)
                    : typeFactory.createSqlType(typeName);
        } catch (IllegalArgumentException e) {
            return typeFactory.createTypeWithNullability(
                    typeFactory.createSqlType(SqlTypeName.ANY), true);
        }
    }

    public static int getMySqlType(String typeString) {
        int mysqlType;
        switch (typeString) {
            case "BIT":
                mysqlType = MysqlType.BIT.getJdbcType();
                break;
            case "TINYINT":
                mysqlType = MysqlType.TINYINT.getJdbcType();
                break;
            case "SMALLINT":
                mysqlType = MysqlType.SMALLINT.getJdbcType();
                break;
            case "INT":
                mysqlType = MysqlType.INT.getJdbcType();
                break;
            case "BIGINT":
            case "LARGEINT":
                mysqlType = MysqlType.BIGINT.getJdbcType();
                break;
            case "FLOAT":
                mysqlType = MysqlType.FLOAT.getJdbcType();
                break;
            case "DOUBLE":
                mysqlType = MysqlType.DOUBLE.getJdbcType();
                break;
            case "DECIMAL":
            case "DECIMALV3":
                mysqlType = MysqlType.DECIMAL.getJdbcType();
                break;
            case "DATE":
                mysqlType = MysqlType.DATE.getJdbcType();
                break;
            case "DATETIME":
                mysqlType = MysqlType.DATETIME.getJdbcType();
                break;
            case "CHAR":
                mysqlType = MysqlType.CHAR.getJdbcType();
                break;
            case "VARCHAR":
                mysqlType = MysqlType.VARCHAR.getJdbcType();
                break;
            case "BOOLEAN":
                mysqlType = MysqlType.BOOLEAN.getJdbcType();
                break;
            case "JSONB":
                mysqlType = MysqlType.JSON.getJdbcType();
                break;
            case "TEXT":
                mysqlType = MysqlType.TEXT.getJdbcType();
                break;
            default:
                mysqlType = MysqlType.VARCHAR.getJdbcType();
                log.warn("parse doris type warn unknown type :{}", typeString);
        }
        return mysqlType;
    }
}
