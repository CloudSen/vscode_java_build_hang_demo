package cn.cisdigital.datakits.framework.util.sqlparser.schema;

import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import java.util.Collections;
import java.util.Set;

/**
 * 解耦calcite schema与catalog
 *
 * @author xxx
 */
public class CatalogCalciteSchemaAdapter extends AbstractCalciteSchema {

    private final ICatalogDiscover catalogDiscover;

    public CatalogCalciteSchemaAdapter(ICatalogDiscover catalogDiscover) {
        this.catalogDiscover = catalogDiscover;
    }


    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Set<String> getTableNames() {
        return Collections.emptySet();
    }

    @Override
    public Schema getSubSchema(String schemaName) {
        if (catalogDiscover.databaseExists(schemaName)) {
            return new DatabaseCalciteSchemaAdapter(schemaName, catalogDiscover);
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getSubSchemaNames() {
        return catalogDiscover.listDatabases();
    }

    @Override
    public Expression getExpression(SchemaPlus parentSchema, String name) {
        return null;
    }

    @Override
    public boolean isMutable() {
        return false;
    }
}
