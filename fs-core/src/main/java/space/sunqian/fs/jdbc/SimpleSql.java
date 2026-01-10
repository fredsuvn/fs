package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;

import java.sql.ResultSet;

public interface SimpleSql {

    /**
     * @return the result set of the query for this SQL statement
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    ResultSet query() throws JdbcException;
}
