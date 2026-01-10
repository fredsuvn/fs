package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;

/**
 * Object converter from JDBC object to java object.
 *
 * @author sunqian
 */
public interface JdbcConverter {

    /**
     * Converts the JDBC object to the specified java object.
     *
     * @param jdbcObject the JDBC object
     * @param javaType   the specified java type
     * @param <T>        the generic type of the specified java type
     * @return the converted java object
     */
    @Nonnull
    <T> T convert(@Nonnull Object jdbcObject, @Nonnull Type javaType);
}
