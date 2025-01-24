package cn.cisdigital.datakits.framework.util.sqlparser.catalog;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author xxx
 * @since 2024/4/17 17:24
 */
@Getter
public class ObjectIdentifier implements Serializable {
    static final String UNKNOWN = "<UNKNOWN>";

    private final @Nullable String catalogName;
    private final @Nullable String databaseName;
    private final String objectName;

    public static ObjectIdentifier of(String catalogName, String databaseName, String objectName) {
        if (Objects.equals(catalogName, UNKNOWN) || Objects.equals(databaseName, UNKNOWN)) {
            throw new IllegalArgumentException(
                    String.format("Catalog or database cannot be named '%s'", UNKNOWN));
        }
        return new ObjectIdentifier(
                Preconditions.checkNotNull(catalogName, "Catalog name must not be null."),
                Preconditions.checkNotNull(databaseName, "Database name must not be null."),
                Preconditions.checkNotNull(objectName, "Object name must not be null."));
    }

    /**
     * This method allows to create an {@link ObjectIdentifier} without catalog and database name,
     * in order to propagate anonymous objects with unique identifiers throughout the stack.
     *
     * <p>This method for no reason should be exposed to users, as this should be used only when
     * creating anonymous tables with uniquely generated identifiers.
     */
    static ObjectIdentifier ofAnonymous(String objectName) {
        return new ObjectIdentifier(
                null,
                null,
                Preconditions.checkNotNull(objectName, "Object name must not be null."));
    }

    private ObjectIdentifier(
            @Nullable String catalogName, @Nullable String databaseName, String objectName) {
        this.catalogName = catalogName;
        this.databaseName = databaseName;
        this.objectName = objectName;
    }

    public String getCatalogName() {
        if (catalogName == null) {
            return UNKNOWN;
        }
        return catalogName;
    }

    public String getDatabaseName() {
        if (catalogName == null) {
            return UNKNOWN;
        }
        return databaseName;
    }

    /**
     * Convert this {@link ObjectIdentifier} to {@link ObjectPath}.
     */
    public ObjectPath toObjectPath() {

        return new ObjectPath(databaseName, objectName);
    }

    /** List of the component names of this object identifier. */
    public List<String> toList() {
        if (catalogName == null) {
            return Collections.singletonList(getObjectName());
        }
        return Arrays.asList(getCatalogName(), getDatabaseName(), getObjectName());
    }

    /** Returns a string that summarizes this instance for printing to a console or log. */
    public String asSummaryString() {
        if (catalogName == null) {
            return objectName;
        }
        return String.join(".", catalogName, databaseName, objectName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectIdentifier that = (ObjectIdentifier) o;
        return Objects.equals(catalogName, that.catalogName)
                && Objects.equals(databaseName, that.databaseName)
                && objectName.equals(that.objectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalogName, databaseName, objectName);
    }
}
