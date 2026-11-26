package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

/**
 * A mapper from Java beans to SQL tables, views, or query result sets.
 *
 * @author sunqian
 */
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
     */
    @Nonnull
    SqlBeanMapping toBeanMapping(@Nonnull ObjectMeta beanType);
}
