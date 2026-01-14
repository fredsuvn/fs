package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;

/**
 * A fluent interface for building SQL statements programmatically.
 * <p>
 * This builder provides a type-safe way to construct SQL statements with support for conditional SQL fragments and
 * parameter binding.
 *
 * @author sunqian
 * @see PreparedSql
 */
public interface SqlBuilder {

    /**
     * Appends a raw SQL string to the current statement.
     *
     * @param sql the SQL string to append
     * @return this builder
     */
    @Nonnull
    SqlBuilder append(@Nonnull String sql);

    /**
     * Appends a parameterized SQL string to the current statement.
     * <p>
     * The SQL string should contain question marks (?) as parameter placeholders, and each parameter will be bound to
     * the corresponding placeholder in order.
     *
     * @param sql    the SQL string with parameter placeholders
     * @param params the parameter values to bind
     * @return this builder
     */
    @Nonnull
    SqlBuilder append(@Nonnull String sql, @Nonnull Object @Nonnull ... params);

    /**
     * Conditionally appends a raw SQL string to the current statement.
     * <p>
     * The SQL fragment will only be added if the specified condition evaluates to {@code true}.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the SQL string to append if condition is true
     * @return this builder
     */
    @Nonnull
    SqlBuilder appendIf(boolean condition, @Nonnull String sql);

    /**
     * Conditionally appends a parameterized SQL string to the current statement.
     * <p>
     * The SQL fragment and its parameters will only be added if the specified condition evaluates to {@code true}. And,
     * the SQL string contains question marks (?) as parameter placeholders, and each parameter will be bound to the
     * corresponding placeholder in order.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the SQL string with parameter placeholders
     * @param params    the parameter values to bind if condition is true
     * @return this builder
     */
    @Nonnull
    SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nonnull Object @Nonnull ... params);

    /**
     * Builds and returns the final prepared SQL statement.
     *
     * @return the prepared SQL statement ready for execution
     */
    @Nonnull
    PreparedSql build();
}