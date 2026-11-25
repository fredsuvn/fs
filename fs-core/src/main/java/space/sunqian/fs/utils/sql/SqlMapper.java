package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

/**
 * Maps between Java types and SQL structures, such as mapping Java beans to SQL tables, views, or query result sets.
 *
 * @author sunqian
 */
public interface SqlMapper {

    /**
     * Returns the default {@link SqlMapper}.
     * <p>
     * The default {@link SqlMapper} uses the following rules:
     * <ul>
     *     <li>
     *         The table/view name is {@link SqlTable#value()} if the bean is marked with {@link SqlTable} and the
     *         {@link SqlTable#value()} is non-empty; otherwise, it is derived from the bean type name by
     *         {@link #defaultToTableName()}.
     *     </li>
     *     <li>
     *         A bean property is mapped to a {@link SqlColumnMapping} only if it is marked with {@link SqlColumn}. If
     *         {@link SqlColumn#value()} is empty, the column name is derived from the property name by
     *         {@link #defaultToColumnName()}.
     *     </li>
     *     <li>
     *         The default {@link SqlMapper} is singleton and caches mappings in a {@link SimpleCache} registered via
     *         {@link Fs#registerGlobalCache(SimpleCache)}.
     *     </li>
     * </ul>
     *
     * @return the default {@link SqlMapper}
     */
    static @Nonnull SqlMapper defaultMapper() {
        return SqlMapperBack.defaultMapper();
    }

    /**
     * Returns the default name mapper for column name.
     * <p>
     * It maps from lower camel case to upper underscore case, e.g. {@code userName} to {@code USER_NAME}.
     *
     * @return the default name mapper for column name
     */
    static @Nonnull NameMapper defaultToColumnName() {
        return SqlMapperBack.defaultToColumnName();
    }

    /**
     * Returns the default name mapper for property name.
     * <p>
     * It maps from upper or lower underscore case to lower camel case, e.g. {@code USER_NAME}/{@code user_name} to
     * {@code userName}.
     *
     * @return the default name mapper for property name
     */
    static @Nonnull NameMapper defaultToPropertyName() {
        return SqlMapperBack.defaultToPropertyName();
    }

    /**
     * Returns the default name mapper for table name.
     * <p>
     * It maps from the simple upper camel case name (without package name) to upper underscore case, e.g.
     * {@code a.b.c.TableName} to {@code TABLE_NAME}.
     *
     * @return the default name mapper for table name
     */
    static @Nonnull NameMapper defaultToTableName() {
        return SqlMapperBack.defaultToTableName();
    }

    /**
     * Creates a SQL bean mapping for the given Java bean metadata.
     * <p>
     * The mapping can be used both to read query results into Java beans and to generate SQL statements such as
     * {@code INSERT}, {@code UPDATE}, and {@code DELETE}.
     *
     * @param beanMeta the metadata of a Java bean
     * @return the SQL bean mapping for the Java bean metadata
     */
    @Nonnull
    SqlBeanMapping toBeanMapping(@Nonnull ObjectMeta beanMeta);
}
