package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.sql.SQLType;
import java.util.Objects;

final class ParameterBack {

    static @Nonnull SqlParameter newParameter(@Nullable Object value, int sqlTypeCode, @Nonnull SQLType sqlType) {
        return new SqlParameterImpl(value, sqlTypeCode, sqlType);
    }

    static @Nonnull CallableParameter newCallableParameter(
        @Nullable Object value,
        int sqlTypeCode,
        @Nonnull SQLType sqlType,
        @Nonnull CallableParameter.Mode mode
    ) {
        return new CallableParameterImpl(value, sqlTypeCode, sqlType, mode);
    }

    private static class BaseParameterImpl implements SqlParameter {

        protected final @Nullable Object value;
        protected final int sqlTypeCode;
        protected final @Nonnull SQLType sqlType;

        protected BaseParameterImpl(@Nullable Object value, int sqlTypeCode, @Nonnull SQLType sqlType) {
            this.value = value;
            this.sqlTypeCode = sqlTypeCode;
            this.sqlType = sqlType;
        }

        @Override
        public @Nullable Object value() {
            return value;
        }

        @Override
        public int sqlTypeCode() {
            return sqlTypeCode;
        }

        @Override
        public @Nonnull SQLType sqlType() {
            return sqlType;
        }
    }

    private static final class SqlParameterImpl extends BaseParameterImpl {

        private SqlParameterImpl(@Nullable Object value, int sqlTypeCode, @Nonnull SQLType sqlType) {
            super(value, sqlTypeCode, sqlType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, sqlTypeCode, sqlType);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SqlParameterImpl)) {
                return false;
            }
            @SuppressWarnings("PatternVariableCanBeUsed")
            SqlParameterImpl that = (SqlParameterImpl) obj;
            return Objects.equals(value, that.value())
                && sqlTypeCode == that.sqlTypeCode()
                && Objects.equals(sqlType, that.sqlType());
        }

        @Override
        public String toString() {
            return "SqlParameter[" + value + ", " + sqlTypeCode + ", " + sqlType.getName() + "]";
        }
    }

    private static final class CallableParameterImpl extends BaseParameterImpl implements CallableParameter {

        private final @Nonnull Mode mode;

        private CallableParameterImpl(
            @Nullable Object value,
            int sqlTypeCode,
            @Nonnull SQLType sqlType,
            @Nonnull Mode mode
        ) {
            super(value, sqlTypeCode, sqlType);
            this.mode = mode;
        }

        @Override
        public @Nonnull Mode mode() {
            return mode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, sqlTypeCode, sqlType, mode);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CallableParameterImpl)) {
                return false;
            }
            @SuppressWarnings("PatternVariableCanBeUsed")
            CallableParameterImpl that = (CallableParameterImpl) obj;
            return Objects.equals(value, that.value())
                && sqlTypeCode == that.sqlTypeCode()
                && Objects.equals(sqlType, that.sqlType())
                && mode == (that.mode());
        }

        @Override
        public String toString() {
            return "CallableParameter[" + value + ", " + sqlTypeCode + ", " + sqlType.getName() + ", " + mode + "]";
        }
    }

    private ParameterBack() {
    }
}
