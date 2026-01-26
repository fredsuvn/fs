package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

/**
 * This interface is used for building SQL in method chaining. For example:
 * <pre>{@code
 * List<User> users = SqlBuilder.newBuilder()
 *     .append("SELECT * FROM users WHERE 1=1)
 *     .append(" AND gender = ", gender)
 *     .append(" AND id in (", idList).append(")")
 *     .build()
 *     .connection(connection)
 *     .query(User.class);
 * }</pre>
 *
 * @author sunqian
 * @see PreparedSql
 */
public interface SqlBuilder {

    /**
     * Returns a new instance of {@link SqlBuilder}.
     *
     * @return a new instance of {@link SqlBuilder}
     */
    static @Nonnull SqlBuilder newBuilder() {
        return SqlBack.newBuilder();
    }

    /**
     * Appends a raw string to the current SQL.
     *
     * @param sql the given raw string to append
     * @return this builder
     */
    @Nonnull
    SqlBuilder append(@Nonnull String sql);

    /**
     * Appends a parameterized string to the current SQL.
     * <p>
     * This method first appends the given raw string, and then appends the question mark (?) for parameter binding. If
     * the given parameter is not an instance of {@link Iterable}, this method appends one question mark (?), otherwise,
     * this method appends multiple question marks joined by commas (?,?,?...), and the number of question marks is
     * equal to the size of the {@link Iterable} parameter.
     *
     * @param sql   the given raw string to append
     * @param param the given parameter value to bind
     * @return this builder
     */
    @Nonnull
    SqlBuilder append(@Nonnull String sql, @Nullable Object param);

    /**
     * Conditionally appends a raw string to the current SQL.
     * <p>
     * The given raw string will only be added if the specified condition evaluates to {@code true}.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the given raw string to append if condition is {@code true}
     * @return this builder
     */
    default @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql) {
        if (condition) {
            append(sql);
        }
        return this;
    }

    /**
     * Conditionally appends a parameterized string to the current SQL.
     * <p>
     * The given parameterized string will only be added if the specified condition evaluates to {@code true}. If it is,
     * this method first appends the given raw string, and then appends the question mark (?) for parameter binding. If
     * the given parameter is not an instance of {@link Iterable}, this method appends one question mark (?), otherwise,
     * this method appends multiple question marks joined by commas (?,?,?...), and the number of question marks is
     * equal to the size of the {@link Iterable} parameter.
     *
     * @param condition the condition that determines whether to append the SQL
     * @param sql       the given raw string to append if condition is {@code true}
     * @param param     the given parameter value to bind if condition is {@code true}
     * @return this builder
     */
    default @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nullable Object param) {
        if (condition) {
            append(sql, param);
        }
        return this;
    }

    /**
     * Builds and returns the final prepared SQL.
     *
     * @return the prepared SQL ready for execution
     */
    @Nonnull
    PreparedSql build();
}