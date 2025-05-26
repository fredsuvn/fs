package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.JieArray;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public class TypeBack {

    static @Nonnull ParameterizedType parameterized(
        @Nonnull Type rawType,
        @Nonnull Type @Nonnull [] actualTypeArguments,
        @Nullable Type ownerType
    ) {
        return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {

        private final @Nonnull Type rawType;
        private final @Nonnull Type @Nonnull [] actualTypeArguments;
        private final @Nullable Type ownerType;

        private ParameterizedTypeImpl(
            @Nonnull Type rawType,
            @Nonnull Type @Nonnull [] actualTypeArguments,
            @Nullable Type ownerType
        ) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = (ownerType != null) ? ownerType :
                (rawType instanceof Class<?> ? ((Class<?>) rawType).getDeclaringClass() : null);
        }

        @Override
        public @Nonnull Type @Nonnull [] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public @Nonnull Type getRawType() {
            return rawType;
        }

        @Override
        public @Nullable Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof ParameterizedTypeImpl) {
                ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
                return Objects.equals(ownerType, that.ownerType) &&
                    Objects.equals(rawType, that.rawType) &&
                    Arrays.equals(actualTypeArguments, that.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType that = (ParameterizedType) o;
                return Objects.equals(ownerType, that.getOwnerType()) &&
                    Objects.equals(rawType, that.getRawType()) &&
                    Arrays.equals(actualTypeArguments, that.getActualTypeArguments());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(actualTypeArguments) ^
                Objects.hashCode(ownerType) ^
                Objects.hashCode(rawType);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (ownerType != null) {
                // test.A<T>
                sb.append(ownerType.getTypeName());
                // test.A<T>$
                sb.append("$");
                // test.A<T>$B
                sb.append(JieReflect.getLastName(rawType));
            } else {
                // test.B
                sb.append(rawType.getTypeName());
            }
            if (JieArray.isNotEmpty(actualTypeArguments)) {
                sb.append("<");
                boolean first = true;
                for (Type t : actualTypeArguments) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(t.getTypeName());
                    first = false;
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }
}
