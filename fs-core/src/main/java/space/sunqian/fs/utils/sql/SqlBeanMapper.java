package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.PropertyMeta;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

/**
 * A mapper from Java beans to SQL tables, views, or query result sets.
 *
 * @author sunqian
 */
@ThreadSafe
public interface SqlBeanMapper {

    /**
     * Returns the default {@link SqlBeanMapper}.
     * <p>
     * The default {@link SqlBeanMapper} uses the following rules:
     * <ul>
     *     <li>
     *         A bean is mapped to a {@link SqlBeanMapping}. The table/view name is {@link SqlTable#value()} if the bean
     *         is marked with {@link SqlTable} and its value is non-empty; otherwise, it is derived from the bean type
     *         name by {@link #defaultToTableNameMapper()}.
     *     </li>
     *     <li>
     *         A bean property is mapped to a {@link SqlPropertyMapping} if and only if it is marked with
     *         {@link SqlColumn}. The column name is {@link SqlColumn#value()} if it is non-empty, otherwise it is
     *         derived from the property name by {@link #defaultToColumnNameMapper()}.
     *     </li>
     *     <li>
     *         The default {@link SqlBeanMapper} is a singleton and caches mappings in a {@link SimpleCache} registered
     *         via {@link Fs#registerGlobalCache(SimpleCache)}.
     *     </li>
     * </ul>
     *
     * @return the default {@link SqlBeanMapper}
     */
    static @Nonnull SqlBeanMapper defaultMapper() {
        return SqlBeanMapperBack.defaultMapper();
    }

    /**
     * Creates a new {@link SqlBeanMapper} with the given resolvers and cache. If the cache is {@code null}, then the
     * returned {@link SqlBeanMapper} does not cache the mappings.
     * <p>
     * The returned {@link SqlBeanMapper} is thread-safe if the given resolvers and cache are thread-safe.
     *
     * @param tableResolver  the table resolver
     * @param columnResolver the column resolver
     * @param cache          the cache to store the mappings, or {@code null} to not cache the mappings
     * @return a new {@link SqlBeanMapper}
     */
    static @Nonnull SqlBeanMapper of(
        @Nonnull TableResolver tableResolver,
        @Nonnull ColumnResolver columnResolver,
        @Nullable SimpleCache<@Nonnull ObjectMeta, @Nonnull SqlBeanMapping> cache
    ) {
        return SqlBeanMapperBack.newMapper(tableResolver, columnResolver, cache);
    }

    /**
     * Returns the default column name mapper.
     * <p>
     * It maps from lower camel case to upper underscore case, and the numbers will be separated independently. For
     * example:
     * <ul>
     *     <li>{@code userName} to {@code USER_NAME}</li>
     *     <li>{@code user123} to {@code USER_123}</li>
     * </ul>
     *
     * @return the default column name mapper
     */
    static @Nonnull NameMapper defaultToColumnNameMapper() {
        return SqlBeanMapperBack.defaultToColumnNameMapper();
    }

    /**
     * Returns the default property name mapper.
     * <p>
     * It maps from upper or lower underscore case to lower camel case, and the numbers will be separated independently.
     * For example:
     * <ul>
     *     <li>{@code USER_NAME}/{@code user_name} to {@code userName}</li>
     *     <li>{@code USER_123}/{@code user_123} to {@code user123}</li>
     * </ul>
     *
     * @return the default property name mapper
     */
    static @Nonnull NameMapper defaultToPropertyNameMapper() {
        return SqlBeanMapperBack.defaultToPropertyNameMapper();
    }

    /**
     * Returns the default table name mapper.
     * <p>
     * It maps from the simple upper camel case type name (without package name and declaring type name) to upper
     * underscore case, and the numbers will be separated independently. For example:
     * <ul>
     *     <li>{@code a.b.c.TableName} to {@code TABLE_NAME}</li>
     *     <li>{@code a.b.c.TableName$InnerName} to {@code INNER_NAME}</li>
     *     <li>{@code a.b.c.TableName123} to {@code TABLE_NAME_123}</li>
     * </ul>
     *
     * @return the default table name mapper
     */
    static @Nonnull NameMapper defaultToTableNameMapper() {
        return SqlBeanMapperBack.defaultToTableNameMapper();
    }

    /**
     * Returns a SQL bean mapping for the given Java bean type.
     *
     * @param beanType the type of the given Java bean
     * @return the SQL bean mapping for the Java bean type
     * @throws SqlRuntimeException if the bean can not be mapped to the target SQL structure
     */
    @Nonnull
    SqlBeanMapping toBeanMapping(@Nonnull ObjectMeta beanType) throws SqlRuntimeException;

    /**
     * Resolves the info about a SQL table, view, or query result set for a Java bean type.
     */
    @FunctionalInterface
    interface TableResolver {

        /**
         * Resolves the info about the SQL table, view, or query result set for the given Java bean type. If the bean
         * can not be mapped to the target SQL structure, it returns {@code null}.
         *
         * @param beanType the Java bean type
         * @return the info about the target SQL structure, or {@code null} if the bean can not be mapped to the target
         * SQL structure
         */
        @Nullable
        SqlTableInfo resolve(@Nonnull ObjectMeta beanType);
    }

    /**
     * Resolves the SQL column info for a Java bean property.
     */
    @FunctionalInterface
    interface ColumnResolver {

        /**
         * Resolves the SQL column info for the given property. If the property can not be mapped to a column, it
         * returns {@code null}.
         *
         * @param propertyType the Java bean property type
         * @return the SQL column info, or {@code null} if the property can not be mapped to a column
         */
        @Nullable
        SqlColumnInfo resolve(@Nonnull PropertyMeta propertyType);
    }
}
