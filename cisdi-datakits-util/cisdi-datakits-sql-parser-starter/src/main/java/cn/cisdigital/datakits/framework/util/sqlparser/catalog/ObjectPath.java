package cn.cisdigital.datakits.framework.util.sqlparser.catalog;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import com.mysql.cj.util.StringUtils;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;


/**
 * @author xxx
 * @since 2024/4/17 14:16
 */
@Getter
public class ObjectPath implements Serializable {

    private final String databaseName;
    private final String objectName;

    public ObjectPath(String databaseName, String objectName) {
        Preconditions.checkNotNull(
                !StringUtils.isEmptyOrWhitespaceOnly(databaseName),
                "databaseName cannot be null or empty");
        Preconditions.checkNotNull(
                !StringUtils.isEmptyOrWhitespaceOnly(objectName),
                "objectName cannot be null or empty");

        this.databaseName = databaseName;
        this.objectName = objectName;
    }

    public String getFullName() {
        return String.format("%s.%s", databaseName, objectName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectPath that = (ObjectPath) o;

        return Objects.equals(databaseName, that.databaseName)
                && Objects.equals(objectName, that.objectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseName, objectName);
    }

    @Override
    public String toString() {
        return String.format("%s.%s", databaseName, objectName);
    }
}
