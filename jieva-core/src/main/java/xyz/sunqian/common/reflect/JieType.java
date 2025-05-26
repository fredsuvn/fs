package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieStream;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides implementations and utilities for {@link Type}.
 *
 * @author sunqian
 */
public class JieType {

    /**
     * Returns a {@link ParameterizedType} with the specified raw type and actual type arguments.
     *
     * @param rawType             the raw type
     * @param actualTypeArguments the actual type arguments
     * @return a {@link ParameterizedType} with the specified raw type and actual type arguments
     */
    public static @Nonnull ParameterizedType parameterized(
        @Nonnull Class<?> rawType,
        @Nonnull Type @Nonnull @RetainedParam [] actualTypeArguments
    ) {
        return parameterized(rawType, actualTypeArguments, null);
    }

    /**
     * Returns a {@link ParameterizedType} with the specified raw type, actual type arguments and owner type. The owner
     * type may be {@code null}, and if it is {@code null}, the owner type will be the result of
     * {@link Class#getDeclaringClass()}.
     *
     * @param rawType             the raw type
     * @param actualTypeArguments the actual type arguments
     * @param ownerType           the owner type
     * @return a {@link ParameterizedType} with the specified raw type, actual type arguments and owner type
     */
    public static @Nonnull ParameterizedType parameterized(
        @Nonnull Class<?> rawType,
        @Nonnull Type @Nonnull @RetainedParam [] actualTypeArguments,
        @Nullable Type ownerType
    ) {
        return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    /**
     * Returns a {@link WildcardType} with the specified upper bound ({@code ? extends}).
     *
     * @param upperBound the upper bound
     * @return a {@link WildcardType} with the specified upper bound ({@code ? extends})
     */
    public static @Nonnull WildcardType upperBound(@Nonnull Type upperBound) {
        return new WildcardTypeImpl(Jie.array(upperBound), WildcardTypeImpl.EMPTY_BOUNDS);
    }

    /**
     * Returns a {@link WildcardType} with the specified lower bound ({@code ? super}).
     *
     * @param lowerBounds the lower bound
     * @return a {@link WildcardType} with the specified lower bound ({@code ? super})
     */
    public static @Nonnull WildcardType lowerBound(@Nonnull Type lowerBounds) {
        return new WildcardTypeImpl(WildcardTypeImpl.OBJECT_BOUND, Jie.array(lowerBounds));
    }

    /**
     * Returns a singleton {@link WildcardType} represents {@code ?}.
     *
     * @return a singleton {@link WildcardType} represents {@code ?}
     */
    public static @Nonnull WildcardType questionMark() {
        return WildcardTypeImpl.QUESTION_MARK;
    }

    /**
     * Returns a {@link WildcardType} with the specified upper bounds and lower bounds.
     *
     * @param upperBounds the upper bounds
     * @param lowerBounds the lower bounds
     * @return a {@link WildcardType} with the specified upper bounds and lower bounds
     */
    public static @Nonnull WildcardType wildcard(
        @Nonnull Type @Nonnull @RetainedParam [] upperBounds,
        @Nonnull Type @Nonnull @RetainedParam [] lowerBounds
    ) {
        return new WildcardTypeImpl(upperBounds, lowerBounds);
    }

    /**
     * Returns a {@link GenericArrayType} with the specified component type.
     *
     * @param componentType the component type
     * @return a {@link GenericArrayType} with the specified component type
     */
    public static @Nonnull GenericArrayType array(@Nonnull Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    /**
     * Returns a new instance of {@link Type}. Note the type of the instance is <b>NOT</b> the {@link Class},
     * {@link ParameterizedType}, {@link WildcardType}, {@link TypeVariable} or {@link GenericArrayType}.
     *
     * @return a new instance of {@link Type}
     */
    public static @Nonnull Type other() {
        return new OtherType();
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {

        private final @Nonnull Class<?> rawType;
        private final @Nonnull Type @Nonnull [] actualTypeArguments;
        private final @Nullable Type ownerType;

        private ParameterizedTypeImpl(
            @Nonnull Class<?> rawType,
            @Nonnull Type @Nonnull @RetainedParam [] actualTypeArguments,
            @Nullable Type ownerType
        ) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = ownerType != null ? ownerType : rawType.getDeclaringClass();
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
                return Jie.equals(ownerType, that.ownerType) &&
                    Jie.equals(rawType, that.rawType) &&
                    Arrays.equals(actualTypeArguments, that.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType that = (ParameterizedType) o;
                return Jie.equals(ownerType, that.getOwnerType()) &&
                    Jie.equals(rawType, that.getRawType()) &&
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
                sb.append(rawType.getSimpleName());
            } else {
                // test.B
                sb.append(rawType.getTypeName());
            }
            // <...>
            sb.append("<");
            sb.append(JieStream.stream(actualTypeArguments)
                .map(Type::getTypeName)
                .collect(Collectors.joining(", ")));
            sb.append(">");
            return sb.toString();
        }
    }

    private static class WildcardTypeImpl implements WildcardType {

        private static final @Nonnull Type @Nonnull [] EMPTY_BOUNDS = {};
        private static final @Nonnull Type @Nonnull [] OBJECT_BOUND = {Object.class};
        private static final WildcardType QUESTION_MARK = new WildcardTypeImpl(
            Jie.array(Object.class), EMPTY_BOUNDS
        );

        private final @Nonnull Type @Nonnull [] upperBounds;
        private final @Nonnull Type @Nonnull [] lowerBounds;

        private WildcardTypeImpl(
            @Nonnull Type @Nonnull @RetainedParam [] upperBounds,
            @Nonnull Type @Nonnull @RetainedParam [] lowerBounds
        ) {
            this.upperBounds = upperBounds;
            this.lowerBounds = lowerBounds;
        }

        @Override
        public @Nonnull Type @Nonnull [] getUpperBounds() {
            return upperBounds.clone();
        }

        @Override
        public @Nonnull Type @Nonnull [] getLowerBounds() {
            return lowerBounds.length == 0 ? lowerBounds : lowerBounds.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof WildcardTypeImpl) {
                WildcardTypeImpl that = (WildcardTypeImpl) o;
                return Arrays.equals(lowerBounds, that.lowerBounds) &&
                    Arrays.equals(upperBounds, that.upperBounds);
            }
            if (o instanceof WildcardType) {
                WildcardType that = (WildcardType) o;
                return Arrays.equals(lowerBounds, that.getLowerBounds()) &&
                    Arrays.equals(upperBounds, that.getUpperBounds());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
        }

        @Override
        public String toString() {
            if (lowerBounds.length > 0) {
                // ? super
                return "? super " + lowerBounds[0].getTypeName();
            }
            if (upperBounds.length > 0) {
                if (Jie.equals(upperBounds[0], Object.class)) {
                    return "?";
                }
                // ? extends
                return "? extends " + upperBounds[0].getTypeName();
            }
            // unknown
            return "??";
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {

        private final @Nonnull Type componentType;

        private GenericArrayTypeImpl(@Nonnull Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public @Nonnull Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GenericArrayType) {
                GenericArrayType other = (GenericArrayType) o;
                return Objects.equals(componentType, other.getGenericComponentType());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(componentType);
        }

        @Override
        public String toString() {
            return componentType.getTypeName() + "[]";
        }
    }

    private static final class OtherType implements Type {

        @Override
        public @Nonnull String getTypeName() {
            return getClass().getName();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return getTypeName();
        }
    }
}
