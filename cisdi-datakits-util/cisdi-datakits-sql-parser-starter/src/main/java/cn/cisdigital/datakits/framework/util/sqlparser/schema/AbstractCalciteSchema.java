package cn.cisdigital.datakits.framework.util.sqlparser.schema;

import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaVersion;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * calcite schema抽象类，创建了一些暂时不需要的默认实现
 *
 * @author xxx
 */
public abstract class AbstractCalciteSchema implements Schema {
    @Override
    public RelProtoDataType getType(String name) {
        return null;
    }

    @Override
    public Set<String> getTypeNames() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Function> getFunctions(String name) {
        return Collections.emptyList();
    }

    @Override
    public Set<String> getFunctionNames() {
        return Collections.emptySet();
    }

    @Override
    public Schema snapshot(SchemaVersion version) {
        return this;
    }
}
