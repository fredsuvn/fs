package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.meta.ObjectMeta;

import java.util.Map;

/**
 * Represents a mapping from a Java bean to a SQL table, view, or query result set.
 *
 * @author sunqian
 */
@Immutable
public interface SqlBeanMapping {

    /**
     * Returns the name of the target SQL structure. For a table or view, this method returns the table name or view
     * name; for a query result set, this method returns {@code null}.
     *
     * @return the name of the target SQL structure, or {@code null} if the target SQL structure has no name
     */
    @Nullable
    String targetName();

    /**
     * Returns an immutable map from the property names to the property mappings.
     *
     * @return an immutable map from the property names to the property mappings
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull SqlPropertyMapping> propertyMappingsByPropertyName();

    /**
     * Returns an immutable map from the column names to the property mappings.
     *
     * @return an immutable map from the column names to the property mappings
     */
    @Immutable
    @Nonnull
    Map<@Nonnull String, @Nonnull SqlPropertyMapping> propertyMappingsByColumnName();

    /**
     * Returns the property mapping with the specified bean property name, or {@code null} if no such property mapping
     * exists.
     *
     * @param propertyName the specified bean property name
     * @return the property mapping with the specified bean property name, or {@code null} if no such property mapping
     * exists
     */
    default @Nullable SqlPropertyMapping getPropertyMappingByPropertyName(@Nonnull String propertyName) {
        return propertyMappingsByPropertyName().get(propertyName);
    }

    /**
     * Returns the property mapping with the specified column name, or {@code null} if no such property mapping exists.
     *
     * @param columnName the specified column name
     * @return the property mapping with the specified column name, or {@code null} if no such property mapping exists
     */
    default @Nullable SqlPropertyMapping getPropertyMappingByColumnName(@Nonnull String columnName) {
        return propertyMappingsByColumnName().get(columnName);
    }

    /**
     * Returns the property mapping of the primary key column of this bean mapping, or {@code null} if this bean mapping
     * has no primary key column mapping. If this bean mapping has multiple primary key column mappings, this method
     * returns the first one encountered in the properties of the mapped Java bean.
     *
     * @return the property mapping of the primary key column of this bean mapping, or {@code null} if this bean mapping
     * has no primary key column mapping
     */
    @Nullable
    SqlPropertyMapping primaryKey();

    /**
     * Returns the type of the mapped Java bean.
     *
     * @return the type of the mapped Java bean
     */
    @Nonnull
    ObjectMeta beanType();
}
