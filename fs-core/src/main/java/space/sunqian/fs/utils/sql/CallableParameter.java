package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.sql.CallableStatement;
import java.sql.JDBCType;
import java.sql.SQLType;

/**
 * Represents a parameter in a SQL callable statement, such as a procedure or function. It can be used to set a SQL
 * callable parameter on a {@link CallableStatement} object.
 *
 * @author Sunqian
 */
@Immutable
public interface CallableParameter extends SqlParameter {

    /**
     * Creates and returns a new {@link CallableParameter} with the given value, SQL type, and mode.
     * <p>
     * The {@link #sqlTypeCode()} of the returned {@link CallableParameter} will be retrieved by calling
     * {@link SQLType#getVendorTypeNumber()}.
     *
     * @param value   the value of the parameter, can be {@code null}
     * @param sqlType the SQL type of the parameter
     * @param mode    the mode of the parameter
     * @return the newly created {@link CallableParameter}
     */
    static @Nonnull CallableParameter of(@Nullable Object value, @Nonnull SQLType sqlType, @Nonnull Mode mode) {
        return of(value, sqlType.getVendorTypeNumber(), sqlType, mode);
    }

    /**
     * Creates and returns a new {@link CallableParameter} with the given value, SQL type code, and mode.
     * <p>
     * The {@link #sqlType()} of the returned {@link CallableParameter} will be found by calling
     * {@link JDBCType#valueOf(int)}.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param mode        the mode of the parameter
     * @return the newly created {@link CallableParameter}
     */
    static @Nonnull CallableParameter of(@Nullable Object value, int sqlTypeCode, @Nonnull Mode mode) {
        return of(value, sqlTypeCode, JDBCType.valueOf(sqlTypeCode), mode);
    }

    /**
     * Creates and returns a new {@link CallableParameter} with the given value, SQL type code, SQL type, and mode.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param sqlType     the SQL type of the parameter
     * @param mode        the mode of the parameter
     * @return the newly created {@link CallableParameter}
     */
    static @Nonnull CallableParameter of(
        @Nullable Object value,
        int sqlTypeCode,
        @Nonnull SQLType sqlType,
        @Nonnull Mode mode
    ) {
        return ParameterBack.newCallableParameter(value, sqlTypeCode, sqlType, mode);
    }

    /**
     * Returns the mode of the parameter, which can be {@link Mode#IN}, {@link Mode#OUT}, or {@link Mode#IN_OUT}.
     *
     * @return the mode of the parameter
     */
    @Nonnull
    Mode mode();

    /**
     * Represents the mode of the parameter, which can be {@link #IN}, {@link #OUT}, or {@link #IN_OUT}.
     */
    enum Mode {
        /**
         * Represents the parameter is an input parameter.
         */
        IN,
        /**
         * Represents the parameter is an output parameter.
         */
        OUT,
        /**
         * Represents the parameter is an input/output parameter.
         */
        IN_OUT
    }
}
