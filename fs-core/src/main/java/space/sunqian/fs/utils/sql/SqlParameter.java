package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.sql.CallableStatement;
import java.sql.JDBCType;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLType;
import java.sql.Types;

/**
 * Represents an SQL parameter.
 * <p>
 * It can represent a prepared statement parameter, which is set on a {@link PreparedStatement}. In this case,
 * {@link #mode()} returns {@code null}.
 * <p>
 * It can also represent a callable statement parameter such as a procedure or function parameter. In this case,
 * {@link #mode()} returns a non-null value.
 *
 * @author Sunqian
 */
@Immutable
public interface SqlParameter {

    /**
     * Creates and returns a new {@link SqlParameter} for {@link PreparedStatement} with the given value and SQL type.
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
     * Creates and returns a new {@link SqlParameter} for {@link PreparedStatement} with the given value and type code.
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
     * Creates and returns a new {@link SqlParameter} for {@link PreparedStatement} with the given value, type code, and
     * SQL type.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param sqlType     the SQL type of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, int sqlTypeCode, @Nonnull SQLType sqlType) {
        return SqlParameterBack.newSqlParameter(value, sqlTypeCode, sqlType);
    }

    /**
     * Creates and returns a new {@link SqlParameter} for {@link CallableStatement} with the given value, SQL type, and
     * mode.
     * <p>
     * The {@link #sqlTypeCode()} of the returned {@link SqlParameter} will be retrieved by calling
     * {@link SQLType#getVendorTypeNumber()}.
     *
     * @param value   the value of the parameter, can be {@code null}
     * @param sqlType the SQL type of the parameter
     * @param mode    the mode of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, @Nonnull SQLType sqlType, @Nonnull Mode mode) {
        return of(value, sqlType.getVendorTypeNumber(), sqlType, mode);
    }

    /**
     * Creates and returns a new {@link SqlParameter} for {@link CallableStatement} with the given value, SQL type code,
     * and mode.
     * <p>
     * The {@link #sqlType()} of the returned {@link SqlParameter} will be found by calling
     * {@link JDBCType#valueOf(int)}.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param mode        the mode of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(@Nullable Object value, int sqlTypeCode, @Nonnull Mode mode) {
        return of(value, sqlTypeCode, JDBCType.valueOf(sqlTypeCode), mode);
    }

    /**
     * Creates and returns a new {@link SqlParameter} for {@link CallableStatement} with the given value, SQL type code,
     * SQL type, and mode.
     *
     * @param value       the value of the parameter, can be {@code null}
     * @param sqlTypeCode the SQL type code of the parameter
     * @param sqlType     the SQL type of the parameter
     * @param mode        the mode of the parameter
     * @return the newly created {@link SqlParameter}
     */
    static @Nonnull SqlParameter of(
        @Nullable Object value,
        int sqlTypeCode,
        @Nonnull SQLType sqlType,
        @Nonnull Mode mode
    ) {
        return SqlParameterBack.newCallableParameter(value, sqlTypeCode, sqlType, mode);
    }

    /**
     * Returns the value of this parameter, which can be {@code null}.
     *
     * @return the value of this parameter, which can be {@code null}
     */
    @Nullable
    Object value();

    /**
     * Returns the SQL type code of this parameter, which is typically one of the constants defined in {@link Types}.
     *
     * @return the SQL type code of this parameter, which is typically one of the constants defined in {@link Types}
     */
    int sqlTypeCode();

    /**
     * Returns the SQL type of this parameter.
     *
     * @return the SQL type of this parameter
     */
    @Nonnull
    SQLType sqlType();

    /**
     * Returns the mode of this parameter if it is a callable statement parameter, otherwise {@code null}.
     *
     * @return the mode of this parameter, or {@code null} if it is not a callable statement parameter
     */
    @Nullable
    Mode mode();

    /**
     * Represents the mode of a parameter, which can be {@link #IN}, {@link #OUT}, {@link #IN_OUT} or {@link #UNKNOWN}.
     */
    enum Mode {

        /**
         * Represents the parameter is an input parameter.
         */
        IN(ParameterMetaData.parameterModeIn),
        /**
         * Represents the parameter is an output parameter.
         */
        OUT(ParameterMetaData.parameterModeOut),
        /**
         * Represents the parameter is an input/output parameter.
         */
        IN_OUT(ParameterMetaData.parameterModeInOut),
        /**
         * Represents the parameter mode is unknown.
         */
        UNKNOWN(ParameterMetaData.parameterModeUnknown),
        ;

        private final int code;

        Mode(int code) {
            this.code = code;
        }

        /**
         * Returns the code of the parameter mode, which is one of the:
         * <ul>
         *     <li>{@link ParameterMetaData#parameterModeIn}</li>
         *     <li>{@link ParameterMetaData#parameterModeOut}</li>
         *     <li>{@link ParameterMetaData#parameterModeInOut}</li>
         *     <li>{@link ParameterMetaData#parameterModeUnknown}</li>
         * </ul>
         *
         * @return the code of the parameter mode
         */
        public int code() {
            return code;
        }
    }
}
