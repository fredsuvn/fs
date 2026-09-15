package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLType;
import java.sql.Types;

/**
 * Represents a parameter in a SQL statement. It can be used to set a SQL parameter on a {@link PreparedStatement}
 * object.
 *
 * @author Sunqian
 */
@Immutable
public interface SqlParameter {

    /**
     * Creates and returns a new {@link SqlParameter} with the given value and SQL type.
     * <p>
     * The {@link #sqlTypeCode()} of the returned {@link SqlParameter} will be retrieved by calling
     * {@link SQLType#getVendorTypeNumber()}.
     *
     * @param value   the value of the parameter, can be {@code null}
     * @param sqlType the SQL type of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, @Nonnull SQLType sqlType) {
        return of(value, sqlType.getVendorTypeNumber(), sqlType);
    }

    /**
     * Creates and returns a new {@link SqlParameter} with the given value and type code.
     * <p>
     * The {@link #sqlType()} of the returned {@link SqlParameter} will be found by calling
     * {@link JDBCType#valueOf(int)}.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, int sqlTypeCode) {
        return of(value, sqlTypeCode, JDBCType.valueOf(sqlTypeCode));
    }

    /**
     * Creates and returns a new {@link SqlParameter} with the given value, type code, and SQL type.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param sqlType     the SQL type of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, int sqlTypeCode, @Nonnull SQLType sqlType) {
        return ParameterBack.newParameter(value, sqlTypeCode, sqlType);
    }

    /**
     * Returns the value of the parameter, which can be {@code null}.
     *
     * @return the value of the parameter, which can be {@code null}
     */
    @Nullable
    Object value();

    /**
     * Returns the SQL type code of the parameter, which is typically one of the constants defined in {@link Types}.
     *
     * @return the SQL type code of the parameter, which is typically one of the constants defined in {@link Types}
     */
    int sqlTypeCode();

    /**
     * Returns the SQL type of the parameter.
     *
     * @return the SQL type of the parameter
     */
    @Nonnull
    SQLType sqlType();
}
