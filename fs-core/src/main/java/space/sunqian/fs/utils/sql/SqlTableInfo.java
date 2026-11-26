package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nullable;

/**
 * Describes the SQL table, view, or query result set that a Java bean is mapped to.
 *
 * @author sunqian
 */
@Immutable
public interface SqlTableInfo {

    /**
     * Returns the table or view name, or {@code null} if the mapping has no name, such as a query result set.
     *
     * @return the table or view name, or {@code null} if the mapping has no name
     */
    @Nullable
    String name();
}
