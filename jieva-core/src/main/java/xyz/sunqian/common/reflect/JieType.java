package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieStream;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Static utility class for {@link Type}.
 *
 * @author sunqian
 */
public class JieType {

    /**
     * Returns {@code true} if the given type is a {@link Class}, {@code false} otherwise.
     *
     * @param type the given type
     * @return {@code true} if the given type is a {@link Class}, {@code false} otherwise
     */
    public static boolean isClass(@Nonnull Type type) {
        return type instanceof Class<?>;
    }

    /**
     * Returns {@code true} if the given type is a {@link ParameterizedType}, {@code false} otherwise.
     *
     * @param type the given type
     * @return {@code true} if the given type is a {@link ParameterizedType}, {@code false} otherwise
     */
    public static boolean isParameterized(@Nonnull Type type) {
        return type instanceof ParameterizedType;
    }

    /**
     * Returns {@code true} if the given type is a {@link WildcardType}, {@code false} otherwise.
     *
     * @param type the given type
     * @return {@code true} if the given type is a {@link WildcardType}, {@code false} otherwise
     */
    public static boolean isWildcard(@Nonnull Type type) {
        return type instanceof WildcardType;
    }

    /**
     * Returns {@code true} if the given type is a {@link TypeVariable}, {@code false} otherwise.
     *
     * @param type the given type
     * @return {@code true} if the given type is a {@link TypeVariable}, {@code false} otherwise
     */
    public static boolean isTypeVariable(@Nonnull Type type) {
        return type instanceof TypeVariable<?>;
    }

    /**
     * Returns {@code true} if the given type is a {@link GenericArrayType}, {@code false} otherwise.
     *
     * @param type the given type
     * @return {@code true} if the given type is a {@link GenericArrayType}, {@code false} otherwise
     */
    public static boolean isGenericArray(@Nonnull Type type) {
        return type instanceof GenericArrayType;
    }

    /**
     * Returns whether the given type is an array type (array {@link Class} or {@link GenericArrayType}).
     *
     * @param type the given type
     * @return whether the given type is an array type (array {@link Class} or {@link GenericArrayType})
     */
    public static boolean isArray(@Nonnull Type type) {
        if (isClass(type)) {
            return ((Class<?>) type).isArray();
        }
        return isGenericArray(type);
    }

    /**
     * Returns the last name of the given type. The last name is sub-string after last dot(.) For example: the last name
     * of {@code java.lang.String} is {@code String}.
     *
     * @param type the given type
     * @return the last name of given type
     */
    public static @Nonnull String getLastName(@Nonnull Type type) {
        String className = type.getTypeName();
        return getLastName(className);
    }

    private static @Nonnull String getLastName(@Nonnull String typeName) {
        int index = JieString.lastIndexOf(typeName, ".");
        return typeName.substring(index + 1);
    }

    /**
     * Returns the raw class of the given type. The given type must be a {@link Class} or {@link ParameterizedType}.
     * This method returns the given type itself if it is a {@link Class}, or {@link ParameterizedType#getRawType()} if
     * it is a {@link ParameterizedType}. Returns {@code null} if the given type neither be {@link Class} nor
     * {@link ParameterizedType}, or the raw type is not a {@link Class}.
     *
     * @param type the given type
     * @return the raw class of given type, or {@code null} if the given type neither be {@link Class} nor
     * {@link ParameterizedType}, or the raw type is not a {@link Class}
     */
    public static @Nullable Class<?> getRawClass(@Nonnull Type type) {
        if (isClass(type)) {
            return (Class<?>) type;
        }
        if (isParameterized(type)) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return isClass(rawType) ? (Class<?>) rawType : null;
        }
        return null;
    }

    /**
     * Returns the first upper bound type of the given wildcard type ({@code ? extends}). Note that if no upper bound is
     * explicitly declared, returns {@code Object.class}.
     *
     * @param type the given wildcard type
     * @return the first upper bound type of the given wildcard type
     */
    public static @Nonnull Type getUpperBound(@Nonnull WildcardType type) {
        Type[] upperBounds = type.getUpperBounds();
        if (JieArray.isNotEmpty(upperBounds)) {
            return upperBounds[0];
        }
        return Object.class;
    }

    /**
     * Returns the first lower bound type of the given wildcard type ({@code ? super}). If given type has no lower
     * bound, returns {@code null}.
     *
     * @param type the given wildcard type
     * @return the first lower bound type of the given wildcard type or {@code null}
     */
    public static @Nullable Type getLowerBound(@Nonnull WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        if (JieArray.isNotEmpty(lowerBounds)) {
            return lowerBounds[0];
        }
        return null;
    }

    /**
     * Returns the first bound type of the given type variable ({@code T extends}). Note that if no upper bound is
     * explicitly declared, returns {@code Object.class}.
     *
     * @param type the given type variable
     * @return the first upper bound type of the given type variable
     */
    public static @Nonnull Type getFirstBound(@Nonnull TypeVariable<?> type) {
        Type[] bounds = type.getBounds();
        if (JieArray.isNotEmpty(bounds)) {
            return bounds[0];
        }
        return Object.class;
    }

    /**
     * Returns the component type of the given type if it is an array, {@code null} if it is not.
     *
     * @param type the given type
     * @return the component type of the given type if it is an array, {@code null} if it is not
     */
    public static @Nullable Type getComponentType(@Nonnull Type type) {
        if (isClass(type)) {
            return ((Class<?>) type).getComponentType();
        }
        if (isGenericArray(type)) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
    }

    /**
     * Returns the runtime class of the given type, may be {@code null} if fails. This method supports {@link Class},
     * {@link ParameterizedType}, {@link GenericArrayType} and {@link TypeVariable}.
     *
     * @param type the given type
     * @return the runtime class of the given type, may be {@code null} if fails
     */
    public static @Nullable Class<?> toRuntimeClass(@Nonnull Type type) {
        if (isClass(type)) {
            return (Class<?>) type;
        }
        if (isParameterized(type)) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return toRuntimeClass(rawType);
        }
        if (isGenericArray(type)) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type componentType = arrayType.getGenericComponentType();
            @Nullable Class<?> componentClass = toRuntimeClass(componentType);
            if (componentClass == null) {
                return null;
            }
            return JieClass.arrayClass(componentClass);
        }
        if (isTypeVariable(type)) {
            return toRuntimeClass(getFirstBound((TypeVariable<?>) type));
        }
        return null;
    }

    /**
     * Returns whether a type can be assigned by another type. This method is {@link Type} version of
     * {@link Class#isAssignableFrom(Class)}, supporting {@link Class}, {@link ParameterizedType}, {@link WildcardType},
     * {@link TypeVariable} and {@link GenericArrayType}.
     *
     * @param assigned the type to be assigned
     * @param assignee the assignee type
     * @return whether a type can be assigned by another type
     */
    public static boolean isAssignable(@Nonnull Type assigned, @Nonnull Type assignee) {
        return TypePattern.defaultPattern().isAssignable(assigned, assignee);
    }

    private static boolean isAssignable(@Nonnull Class<?> assigned, @Nonnull Type assignee) {
        if (Jie.equals(assigned, Object.class)) {
            return true;
        }
        @Nullable Class<?> rawCLass = getRawClass(assignee);
        if (rawCLass != null) {
            return assigned.isAssignableFrom(rawCLass);
        }
        // if (isTypeVariable(assignee)) {
        // }
        // if (isParameterized(assignee)) {
        //     ParameterizedType parameterizedType = (ParameterizedType) assignee;
        //     Class<?> rawCLass = getRawClass(parameterizedType);
        // }
        // List<?> l = null;
        // List o = l;

        return TypePattern.defaultPattern().isAssignable(assigned, assignee);
    }

    /**
     * Returns a new {@link ParameterizedType} with the specified raw type and actual type arguments.
     *
     * @param rawType             the raw type
     * @param actualTypeArguments the actual type arguments
     * @return a new {@link ParameterizedType} with the specified raw type and actual type arguments
     */
    public static @Nonnull ParameterizedType parameterizedType(
        @Nonnull Class<?> rawType,
        @Nonnull Type @Nonnull @RetainedParam [] actualTypeArguments
    ) {
        return parameterizedType(rawType, actualTypeArguments, null);
    }

    /**
     * Returns a new {@link ParameterizedType} with the specified raw type, actual type arguments and owner type. The
     * owner type may be {@code null}, and if it is {@code null}, the owner type will be the result of
     * {@link Class#getDeclaringClass()}.
     *
     * @param rawType             the raw type
     * @param actualTypeArguments the actual type arguments
     * @param ownerType           the owner type
     * @return a new {@link ParameterizedType} with the specified raw type, actual type arguments and owner type
     */
    public static @Nonnull ParameterizedType parameterizedType(
        @Nonnull Class<?> rawType,
        @Nonnull Type @Nonnull @RetainedParam [] actualTypeArguments,
        @Nullable Type ownerType
    ) {
        return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    /**
     * Returns a new {@link WildcardType} with the specified upper bound ({@code ? extends}).
     *
     * @param upperBound the upper bound
     * @return a new {@link WildcardType} with the specified upper bound ({@code ? extends})
     */
    public static @Nonnull WildcardType upperWildcard(@Nonnull Type upperBound) {
        return new WildcardTypeImpl(Jie.array(upperBound), WildcardTypeImpl.EMPTY_BOUNDS);
    }

    /**
     * Returns a new {@link WildcardType} with the specified lower bound ({@code ? super}).
     *
     * @param lowerBounds the lower bound
     * @return a new {@link WildcardType} with the specified lower bound ({@code ? super})
     */
    public static @Nonnull WildcardType lowerWildcard(@Nonnull Type lowerBounds) {
        return new WildcardTypeImpl(WildcardTypeImpl.OBJECT_BOUND, Jie.array(lowerBounds));
    }

    /**
     * Returns a singleton {@link WildcardType} represents {@code ?}.
     *
     * @return a singleton {@link WildcardType} represents {@code ?}
     */
    public static @Nonnull WildcardType wildcardChar() {
        return WildcardTypeImpl.QUESTION_MARK;
    }

    /**
     * Returns a new {@link WildcardType} with the specified upper bounds and lower bounds.
     *
     * @param upperBounds the upper bounds
     * @param lowerBounds the lower bounds
     * @return a new {@link WildcardType} with the specified upper bounds and lower bounds
     */
    public static @Nonnull WildcardType wildcardType(
        @Nonnull Type @Nonnull @RetainedParam [] upperBounds,
        @Nonnull Type @Nonnull @RetainedParam [] lowerBounds
    ) {
        return new WildcardTypeImpl(upperBounds, lowerBounds);
    }

    /**
     * Returns a new {@link GenericArrayType} with the specified component type.
     *
     * @param componentType the component type
     * @return a new {@link GenericArrayType} with the specified component type
     */
    public static @Nonnull GenericArrayType arrayType(@Nonnull Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    /**
     * Returns a new instance of {@link Type}. Note the type of the instance is <b>NOT</b> the {@link Class},
     * {@link ParameterizedType}, {@link WildcardType}, {@link TypeVariable} or {@link GenericArrayType}.
     *
     * @return a new instance of {@link Type}
     */
    public static @Nonnull Type otherType() {
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
