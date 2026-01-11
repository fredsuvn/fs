package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;

/**
 * Maker to create SQL statements.
 *
 * @author sunqian
 */
public interface SqlMaker {

    /**
     * Appends the given SQL string to the current SQL statement.
     *
     * @param sql the SQL string to append
     * @return this SQL statement
     */
    @Nonnull
    SqlMaker sql(@Nonnull String sql);

    /**
     * Appends the given SQL string to the current SQL statement.
     *
     * @param sql    the SQL string to append
     * @param params the parameters in the SQL string
     * @return this SQL statement
     */
    @Nonnull
    SqlMaker sql(@Nonnull String sql, @Nonnull Object @Nonnull ... params);

    /**
     * Appends the given SQL string to the current SQL statement if the condition is true.
     *
     * @param condition the condition to check
     * @param sql       the SQL string to append
     * @return this SQL statement
     */
    @Nonnull
    SqlMaker condition(boolean condition, @Nonnull String sql);

    /**
     * Appends the given SQL string to the current SQL statement if the condition is true.
     *
     * @param condition the condition to check
     * @param sql       the SQL string to append
     * @param params    the parameters in the SQL string
     * @return this SQL statement
     */
    @Nonnull
    SqlMaker condition(boolean condition, @Nonnull String sql, @Nonnull Object @Nonnull ... params);

    /**
     * Creates a prepared SQL statement with the appended SQL string and parameters.
     *
     * @return the prepared SQL statement
     */
    @Nonnull
    PreparedSql makePrepared();
}
