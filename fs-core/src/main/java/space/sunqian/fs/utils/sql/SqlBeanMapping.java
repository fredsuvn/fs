package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.meta.ObjectMeta;

import java.util.Map;

/**
 * Represents a mapping between a Java bean and a SQL structure, such as a table, a view, or a query result set.
 * <p>
 * The mapping can be used both to read query results into Java beans and to generate SQL statements such as
 * {@code INSERT}, {@code UPDATE}, and {@code DELETE}.
 *
 * @author sunqian
 */
@Immutable
public interface SqlBeanMapping {

    /**
     * Returns the name of the underlying SQL source. For a table or a view, this is the table name or view name; for a
     * query result set, this method returns {@code null}.
     *
     * @return the name of the underlying SQL source, or {@code null} if the source has no name
     */
    @Nullable
    String sourceName();

    /**
     * Returns an immutable map from column names to column mappings.
     *
     * @return an immutable map from column names to column mappings
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull SqlColumnMapping> columnsByColumnName();

    /**
     * Returns the column mapping with the specified column name, or {@code null} if no such column mapping exists.
     *
     * @param columnName the specified column name
     * @return the column mapping with the specified column name, or {@code null} if no such column mapping exists
     */
    default @Nullable SqlColumnMapping getColumnByColumnName(@Nonnull String columnName) {
        return columnsByColumnName().get(columnName);
    }

    /**
     * Returns an immutable map from bean property names to column mappings.
     *
     * @return an immutable map from bean property names to column mappings
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull SqlColumnMapping> columnsByPropertyName();

    /**
     * Returns the column mapping with the specified bean property name, or {@code null} if no such column mapping
     * exists.
     *
     * @param propertyName the specified bean property name
     * @return the column mapping with the specified bean property name, or {@code null} if no such column mapping
     * exists
     */
    default @Nullable SqlColumnMapping getColumnByPropertyName(@Nonnull String propertyName) {
        return columnsByPropertyName().get(propertyName);
    }

    /**
     * Returns the primary key column mapping of this bean mapping, or {@code null} if this bean mapping has no primary
     * key. If this bean mapping has multiple primary key columns, this method returns the first one encountered in the
     * properties of the mapped Java bean.
     *
     * @return the primary key column mapping of this bean mapping, or {@code null} if this bean mapping has no primary
     * key column
     */
    @Nullable
    SqlColumnMapping primaryKey();

    /**
     * Returns the metadata of the mapped Java bean.
     *
     * @return the metadata of the mapped Java bean
     */
    @Nonnull
    ObjectMeta beanMeta();
}
