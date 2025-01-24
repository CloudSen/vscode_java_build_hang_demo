package cn.cisdigital.datakits.framework.util.sqlparser.schema;

import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ObjectPath;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Schemas;
import org.apache.calcite.schema.Table;

import java.util.HashSet;
import java.util.Set;

public class DatabaseCalciteSchemaAdapter extends AbstractCalciteSchema {
    private final String databaseName;

    private final ICatalogDiscover catalogDiscover;


    public DatabaseCalciteSchemaAdapter(
            String databaseName,
            ICatalogDiscover catalogDiscover) {
        this.databaseName = databaseName;
        this.catalogDiscover = catalogDiscover;
    }

    @Override
    public Table getTable(String tableName) {
        final ObjectPath objectPath = new ObjectPath(databaseName, tableName);
        try {
            return catalogDiscover.getTable(objectPath);
        }catch (Exception e){
            // calcite解析时，会把如SELECT * FROM dw_dev_ods.tt时会把默认schema中的dw_dev加到解析path中，org.apache.calcite.sql.validate.EmptyScope.resolve_方法第一行
            // 然后dw_dev.dw_dev_ods两个两个进行解析，所以在解析dw_dev.dw_ods时也会报错,如果抛出了异常，后续的解析也就断了
            // Ignore.
            return null;
        }
    }

    @Override
    public Set<String> getTableNames() {
        return catalogDiscover.listTables(databaseName);
    }

    @Override
    public Schema getSubSchema(String s) {
        return null;
    }

    @Override
    public Set<String> getSubSchemaNames() {
        return new HashSet<>();
    }

    @Override
    public Expression getExpression(SchemaPlus parentSchema, String name) {
        return Schemas.subSchemaExpression(parentSchema, name, getClass());
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
