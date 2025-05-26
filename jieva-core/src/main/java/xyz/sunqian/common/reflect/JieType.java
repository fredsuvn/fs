package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.JieCollect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class provides implementations and utilities for {@link Type}.
 *
 * @author fredsuvn
 */
public class JieType {

    /**
     * Returns a {@link ParameterizedType} with given raw type and actual type arguments.
     * <p>
     * Note that the array {@code actualTypeArgs} will be referenced directly in the return value, so any modifications
     * to this array will affect the return value.
     *
     * @param rawType        given raw type
     * @param actualTypeArgs actual type arguments
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(Type rawType, Type[] actualTypeArgs) {
        return parameterized(rawType, actualTypeArgs, null);
    }

    /**
     * Returns a {@link ParameterizedType} with given raw type and actual type arguments.
     *
     * @param rawType        given raw type
     * @param actualTypeArgs actual type arguments
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(Type rawType, Iterable<Type> actualTypeArgs) {
        return parameterized(rawType, JieCollect.toArray(actualTypeArgs, Type.class));
    }

    /**
     * Returns a {@link ParameterizedType} with given raw type, owner type and actual type arguments.
     * <p>
     * Note that the array {@code actualTypeArgs} will be referenced directly in the return value, so any modifications
     * to this array will affect the return value.
     *
     * @param rawType        given raw type
     * @param actualTypeArgs actual type arguments
     * @param ownerType      given owner type
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(Type rawType, Type[] actualTypeArgs, @Nullable Type ownerType) {
        return TypeBack.parameterized(rawType, actualTypeArgs, ownerType);
    }

    /**
     * Returns a {@link ParameterizedType} with given raw type, owner type and actual type arguments.
     *
     * @param rawType        given raw type
     * @param actualTypeArgs actual type arguments
     * @param ownerType      given owner type
     * @return a {@link ParameterizedType}
     */
    public static ParameterizedType parameterized(
        Type rawType, Iterable<Type> actualTypeArgs, @Nullable Type ownerType) {
        return parameterized(rawType, JieCollect.toArray(actualTypeArgs, Type.class), ownerType);
    }

    /**
     * Returns a {@link WildcardType} with given upper bound, it is equivalent to the declaration of:
     * <pre>
     *     ? extends T
     * </pre>
     *
     * @param upperBound given upper bound
     * @return a {@link WildcardType}
     */
    public static WildcardType upperBound(Type upperBound) {
        return new WildcardTypeImpl(true, upperBound);
    }

    /**
     * Returns a {@link WildcardType} with given lower bound, it is equivalent to the declaration of:
     * <pre>
     *     ? super T
     * </pre>
     *
     * @param lowerBound given lower bound
     * @return a {@link WildcardType}
     */
    public static WildcardType lowerBound(Type lowerBound) {
        return new WildcardTypeImpl(false, lowerBound);
    }

    /**
     * Returns a singleton {@link WildcardType} represents {@code ?}.
     *
     * @return a singleton {@link WildcardType} represents {@code ?}
     */
    public static WildcardType questionMark() {
        return WildcardTypeImpl.QUESTION_MARK;
    }

    /**
     * Returns a {@link WildcardType} with given upper bounds and lower bounds.
     * <p>
     * Note that the array {@code upperBounds} and {@code lowerBounds} will be referenced directly in the return value,
     * so any modifications to the arrays will affect the return value.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     * @return a {@link WildcardType}
     */
    public static WildcardType wildcard(Type[] upperBounds, Type[] lowerBounds) {
        return new WildcardTypeImpl(upperBounds, lowerBounds);
    }

    /**
     * Returns a {@link GenericArrayType} with given component type.
     *
     * @param componentType given component type
     * @return a {@link GenericArrayType}
     */
    public static GenericArrayType array(Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    /**
     * Returns a new instance of {@link Type}. The instance is <b>NOT</b> type of {@link Class},
     * {@link ParameterizedType}, {@link WildcardType}, {@link TypeVariable} or {@link GenericArrayType}.
     *
     * @return a new instance of {@link Type}
     */
    public static Type other() {
        return new JievaType();
    }

    private static class WildcardTypeImpl implements WildcardType {

        static final Type[] OBJECT_ARRAY = {Object.class};
        static final Type[] EMPTY_ARRAY = {};

        static final WildcardTypeImpl QUESTION_MARK = new WildcardTypeImpl(true, null);

        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        private WildcardTypeImpl(boolean isUpperBounds, @Nullable Type bound) {
            if (isUpperBounds) {
                // ? extends String
                this.upperBounds = bound == null ? OBJECT_ARRAY : new Type[]{bound};
                this.lowerBounds = EMPTY_ARRAY;
            } else {
                // ? super String
                this.upperBounds = OBJECT_ARRAY;
                this.lowerBounds = bound == null ? OBJECT_ARRAY : new Type[]{bound};
            }
        }

        private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            this.upperBounds = upperBounds;
            this.lowerBounds = lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds.clone();
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds.clone();
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
                WildcardTypeImpl other = (WildcardTypeImpl) o;
                return Arrays.equals(upperBounds, other.upperBounds)
                    && Arrays.equals(lowerBounds, other.lowerBounds);
            }
            if (o instanceof WildcardType) {
                WildcardType other = (WildcardType) o;
                return Arrays.equals(upperBounds, other.getUpperBounds())
                    && Arrays.equals(lowerBounds, other.getLowerBounds());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
        }

        @Override
        public String toString() {
            StringBuilder builder;
            Type[] bounds;
            if (lowerBounds.length == 0) {
                if (upperBounds.length == 0 || Objects.equals(Object.class, upperBounds[0])) {
                    return "?";
                }
                bounds = upperBounds;
                builder = new StringBuilder("? extends ");
            } else {
                bounds = lowerBounds;
                builder = new StringBuilder("? super ");
            }
            for (int i = 0; i < bounds.length; ++i) {
                if (i > 0) {
                    builder.append(" & ");
                }
                builder.append(bounds[i].getTypeName());
            }
            return builder.toString();
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {

        private final Type componentType;

        private GenericArrayTypeImpl(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
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
            Type type = this.getGenericComponentType();
            StringBuilder builder = new StringBuilder();
            if (type instanceof Class) {
                builder.append(((Class<?>) type).getName());
            } else {
                builder.append(type);
            }
            builder.append("[]");
            return builder.toString();
        }
    }

    private static final class JievaType implements Type {

        @Override
        public String getTypeName() {
            return "Hello, Jieva!";
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
