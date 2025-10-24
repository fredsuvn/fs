package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.OutParam;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.base.Kit;
import space.sunqian.common.collect.ArrayKit;
import space.sunqian.common.collect.MapKit;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for {@link Type}.
 *
 * @author sunqian
 */
public class TypeKit {

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
        int index = typeName.lastIndexOf('.');
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
        if (ArrayKit.isNotEmpty(upperBounds)) {
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
        if (ArrayKit.isNotEmpty(lowerBounds)) {
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
        if (ArrayKit.isNotEmpty(bounds)) {
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
            return ClassKit.arrayClass(componentClass);
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
        return AssignBack.isAssignable(assigned, assignee);
    }

    /**
     * Resolves and returns the actual type arguments of the given type, based on the type parameters of the specified
     * base type, in order of those type parameters.
     * <p>
     * For example, here is a base type: {@code interface Base<A, B, C>}, and a subtype to be resolved:
     * {@code class Sub implements Base<String, Integer, Long>}. The result of the
     * {@code resolveActualTypeArguments(subtype, base)} will be the list of:
     * {@code [String.class, Integer.class, Long.class]}.
     * <p>
     * The given type to be resolved must be a {@link Class}, {@link ParameterizedType} or array. If it is a
     * {@link Class}, it must be a sub or same type of the base type; if it is a {@link ParameterizedType}, its raw type
     * must be a sub or same type of the base type; if it is an array, the base type must also be an array, and this
     * method calls itself with their component types.
     * <p>
     * Note this method does not guarantee that all type parameters can be resolved, and unresolved type parameters will
     * be directly returned to the list at the corresponding index.
     *
     * @param type     the given type to be resolved
     * @param baseType the specified base type
     * @return the actual type arguments of the given type, based on the type parameters of the specified base type, in
     * order of those type parameters
     * @throws ReflectionException if the given type cannot be resolved
     */
    public static @Nonnull List<@Nonnull Type> resolveActualTypeArguments(
        @Nonnull Type type, @Nonnull Class<?> baseType
    ) throws ReflectionException {
        if (baseType.isArray()) {
            Type componentType = TypeKit.getComponentType(type);
            if (componentType == null) {
                throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
            }
            return resolveActualTypeArguments(componentType, baseType.getComponentType());
        }
        @Nullable Class<?> cls = TypeKit.toRuntimeClass(type);
        if (cls == null) {
            throw new ReflectionException("Unsupported type: " + type + ".");
        }
        if (!baseType.isAssignableFrom(cls)) {
            throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
        }
        // Resolves:
        TypeVariable<?>[] typeParameters = baseType.getTypeParameters();
        if (ArrayKit.isEmpty(typeParameters)) {
            return Collections.emptyList();
        }
        Map<TypeVariable<?>, Type> typeArguments = typeParametersMapping(type);
        Set<Type> stack = new HashSet<>();
        return Kit.stream(typeParameters)
            .map(typeVariable -> {
                Type actualType = MapKit.resolveChain(typeArguments, typeVariable, stack);
                stack.clear();
                return Kit.nonnull(actualType, typeVariable);
            })
            .collect(Collectors.toList());
    }

    /**
     * Returns a map contains the mapping of type parameters for the given type, the key is type parameter, and the
     * value is the actual type argument or inherited type parameter. For example, these types:
     * <pre>{@code
     *     class X extends Y<Integer, Long>
     *     class Y<K, V> implements Z<Float, Double, V>
     *     interface Z<T, U, R>
     * }</pre>
     * <p>
     * The result of {@code resolveTypeParameterMapping(X.class)} will be:
     * <pre>{@code
     *     T -> Float
     *     U -> Double
     *     R -> V
     *     K -> Integer
     *     V -> Long
     * }</pre>
     *
     * @param type the given type
     * @return a map contains the mapping of type parameters for the given type
     */
    public static @Nonnull Map<@Nonnull TypeVariable<?>, @Nullable Type> typeParametersMapping(
        @Nonnull Type type
    ) {
        Map<TypeVariable<?>, Type> result = new HashMap<>();
        typeParametersMapping(type, result);
        return result;
    }

    private static void typeParametersMapping(
        @Nonnull Type type,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (TypeKit.isClass(type)) {
            Class<?> cur = (Class<?>) type;
            while (cur != null) {
                @Nullable Type superclass = cur.getGenericSuperclass();
                if (superclass != null) {
                    mapTypeVariables(superclass, mapping);
                }
                Type[] interfaces = cur.getGenericInterfaces();
                mapTypeVariables(interfaces, mapping);
                cur = cur.getSuperclass();
            }
        }
        if (TypeKit.isParameterized(type)) {
            mapTypeVariables(type, mapping);
            typeParametersMapping(((ParameterizedType) type).getRawType(), mapping);
        }
    }

    private static void mapTypeVariables(
        @Nonnull Type @Nonnull [] interfaces,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (ArrayKit.isEmpty(interfaces)) {
            return;
        }
        for (Type anInterface : interfaces) {
            mapTypeVariables(anInterface, mapping);
            // never null
            Class<?> rawClass = TypeKit.getRawClass(anInterface);
            if (rawClass == null) {
                // unreachable
                continue;
            }
            mapTypeVariables(rawClass.getGenericInterfaces(), mapping);
        }
    }

    private static void mapTypeVariables(
        @Nonnull Type type,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (!TypeKit.isParameterized(type)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        // never null
        Class<?> rawClass = TypeKit.getRawClass(parameterizedType);
        if (rawClass == null) {
            // unreachable
            return;
        }
        TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<?> typeParameter = typeParameters[i];
            Type typeArgument = typeArguments[i];
            mapping.put(typeParameter, typeArgument);
        }
    }

    /**
     * Resolves the given type and replaces the resolved {@link Class} types, which equal to the specified matching
     * type, with the specified replacement. Returns the fully replaced type (or the original type itself if no
     * replacement were made).
     * <p>
     * For example, for a type: {@code Map<String, Integer>}, the result of
     * {@code replaceType(type, Integer.class, Long.class)} is: {@code Map<String, Long>}. Note the given type itself
     * can also be replaced.
     * <p>
     * This method supports resolving {@link Class}, {@link ParameterizedType}, {@link WildcardType} and
     * {@link GenericArrayType}.
     *
     * @param type        the given type to be resolved
     * @param matching    the specified matching type
     * @param replacement the specified replacement
     * @return the type after the replacing
     * @throws ReflectionException if an error occurs during the replacing
     */
    public static @Nonnull Type replaceType(
        @Nonnull Type type,
        @Nonnull Class<?> matching,
        @Nonnull Type replacement
    ) throws ReflectionException {
        return replaceType(type, t -> Kit.equals(t, matching) ? replacement : t);
    }

    /**
     * Resolves the given type, passes resolved {@link Class} types to the given mapper, and replaces them with the
     * mapper's results which are not equal to the original passed {@link Class} types (via
     * {@link Objects#equals(Object, Object)}). Returns the fully replaced type (or the original type itself if no
     * replacement were made).
     * <p>
     * For example, for a type: {@code Map<String, Integer>}, the result of
     * {@code replaceType(type, t -> Objects.equals(t, Integer.class) ? Long.class : t)} is: {@code Map<String, Long>}.
     * Note the given type itself can also be replaced if it is a {@link Class} and the mapper's result is not equals to
     * it.
     * <p>
     * This method supports resolving {@link Class}, {@link ParameterizedType}, {@link WildcardType} and
     * {@link GenericArrayType}.
     *
     * @param type   the given type to be resolved
     * @param mapper the given mapper
     * @return the type after the replacing
     * @throws ReflectionException if an error occurs during the replacing
     */
    public static @Nonnull Type replaceType(
        @Nonnull Type type,
        @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Type> mapper
    ) throws ReflectionException {
        if (TypeKit.isClass(type)) {
            Type newType = mapper.apply((Class<?>) type);
            if (!Kit.equals(type, newType)) {
                return newType;
            }
        }
        if (TypeKit.isParameterized(type)) {
            return replaceType((ParameterizedType) type, mapper);
        }
        if (TypeKit.isWildcard(type)) {
            return replaceType((WildcardType) type, mapper);
        }
        if (TypeKit.isGenericArray(type)) {
            return replaceType((GenericArrayType) type, mapper);
        }
        return type;
    }

    private static @Nonnull Type replaceType(
        @Nonnull ParameterizedType type,
        @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Type> mapper
    ) throws ReflectionException {
        boolean matched = false;
        Type rawType = type.getRawType();
        Type newRawType = replaceType(rawType, mapper);
        if (!TypeKit.isClass(newRawType)) {
            throw new ReflectionException("Unsupported raw type: " + newRawType + ".");
        }
        if (!Kit.equals(rawType, newRawType)) {
            matched = true;
        }
        @Nullable Type ownerType = type.getOwnerType();
        Type newOwnerType = null;
        if (ownerType != null) {
            newOwnerType = replaceType(ownerType, mapper);
            if (!Kit.equals(ownerType, newOwnerType)) {
                matched = true;
            }
        }
        Type[] actualTypeArguments = type.getActualTypeArguments();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            Type newActualTypeArgument = replaceType(actualTypeArgument, mapper);
            if (!Kit.equals(actualTypeArgument, newActualTypeArgument)) {
                matched = true;
                actualTypeArguments[i] = newActualTypeArgument;
            }
        }
        if (matched) {
            return TypeKit.parameterizedType((Class<?>) newRawType, actualTypeArguments, newOwnerType);
        } else {
            return type;
        }
    }

    private static @Nonnull Type replaceType(
        @Nonnull WildcardType type,
        @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Type> mapper
    ) throws ReflectionException {
        boolean matched = false;
        Type[] upperBounds = type.getUpperBounds();
        for (int i = 0; i < upperBounds.length; i++) {
            Type upperBound = upperBounds[i];
            Type newUpperBound = replaceType(upperBound, mapper);
            if (!Kit.equals(upperBound, newUpperBound)) {
                matched = true;
                upperBounds[i] = newUpperBound;
            }
        }
        Type[] lowerBounds = type.getLowerBounds();
        for (int i = 0; i < lowerBounds.length; i++) {
            Type lowerBound = lowerBounds[i];
            Type newLowerBound = replaceType(lowerBound, mapper);
            if (!Kit.equals(lowerBound, newLowerBound)) {
                matched = true;
                lowerBounds[i] = newLowerBound;
            }
        }
        if (matched) {
            return TypeKit.wildcardType(upperBounds, lowerBounds);
        } else {
            return type;
        }
    }

    private static @Nonnull Type replaceType(
        @Nonnull GenericArrayType type,
        @Nonnull Function<? super @Nonnull Class<?>, ? extends @Nonnull Type> mapper
    ) throws ReflectionException {
        boolean matched = false;
        Type componentType = type.getGenericComponentType();
        Type newComponentType = replaceType(componentType, mapper);
        if (!Kit.equals(componentType, newComponentType)) {
            matched = true;
        }
        if (matched) {
            return TypeKit.arrayType(newComponentType);
        } else {
            return type;
        }
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
        return new WildcardTypeImpl(Kit.array(upperBound), WildcardTypeImpl.EMPTY_BOUNDS);
    }

    /**
     * Returns a new {@link WildcardType} with the specified lower bound ({@code ? super}).
     *
     * @param lowerBounds the lower bound
     * @return a new {@link WildcardType} with the specified lower bound ({@code ? super})
     */
    public static @Nonnull WildcardType lowerWildcard(@Nonnull Type lowerBounds) {
        return new WildcardTypeImpl(WildcardTypeImpl.OBJECT_BOUND, Kit.array(lowerBounds));
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
                return Kit.equals(ownerType, that.ownerType) &&
                    Kit.equals(rawType, that.rawType) &&
                    Arrays.equals(actualTypeArguments, that.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType that = (ParameterizedType) o;
                return Kit.equals(ownerType, that.getOwnerType()) &&
                    Kit.equals(rawType, that.getRawType()) &&
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
            sb.append(Kit.stream(actualTypeArguments)
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
            Kit.array(Object.class), EMPTY_BOUNDS
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
                if (Kit.equals(upperBounds[0], Object.class)) {
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
