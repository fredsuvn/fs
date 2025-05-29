package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieMap;
import xyz.sunqian.common.collect.JieStream;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
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
 * Static utility class for reflection.
 *
 * @author sunqian
 */
public class JieReflect {

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
    public static @Nonnull List<Type> resolveActualTypeArguments(
        @Nonnull Type type, @Nonnull Class<?> baseType
    ) throws ReflectionException {
        if (baseType.isArray()) {
            Type componentType = JieType.getComponentType(type);
            if (componentType == null) {
                throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
            }
            return resolveActualTypeArguments(componentType, baseType.getComponentType());
        }
        @Nullable Class<?> cls = JieType.toRuntimeClass(type);
        if (cls == null) {
            throw new ReflectionException("Unsupported type: " + type + ".");
        }
        if (!baseType.isAssignableFrom(cls)) {
            throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
        }
        // Resolves:
        TypeVariable<?>[] typeParameters = baseType.getTypeParameters();
        if (JieArray.isEmpty(typeParameters)) {
            return Collections.emptyList();
        }
        Map<TypeVariable<?>, Type> typeArguments = mapTypeParameters(type);
        Set<Type> stack = new HashSet<>();
        return JieStream.stream(typeParameters)
            .map(typeVariable -> {
                Type actualType = JieMap.resolveChain(typeArguments, typeVariable, stack);
                stack.clear();
                return Jie.nonnull(actualType, typeVariable);
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
    public static @Nonnull Map<@Nonnull TypeVariable<?>, @Nullable Type> mapTypeParameters(
        @Nonnull Type type
    ) {
        Map<TypeVariable<?>, Type> result = new HashMap<>();
        mapTypeParameters(type, result);
        return result;
    }

    private static void mapTypeParameters(
        @Nonnull Type type,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (JieType.isClass(type)) {
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
        if (JieType.isParameterized(type)) {
            mapTypeVariables(type, mapping);
            mapTypeParameters(((ParameterizedType) type).getRawType(), mapping);
        }
    }

    private static void mapTypeVariables(
        @Nonnull Type @Nonnull [] interfaces,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (JieArray.isEmpty(interfaces)) {
            return;
        }
        for (Type anInterface : interfaces) {
            mapTypeVariables(anInterface, mapping);
            // never null
            Class<?> rawClass = JieType.getRawClass(anInterface);
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
        if (!JieType.isParameterized(type)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        // never null
        Class<?> rawClass = JieType.getRawClass(parameterizedType);
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
        return replaceType(type, t -> Jie.equals(t, matching) ? replacement : t);
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
        if (JieType.isClass(type)) {
            Type newType = mapper.apply((Class<?>) type);
            if (!Jie.equals(type, newType)) {
                return newType;
            }
        }
        if (JieType.isParameterized(type)) {
            return replaceType((ParameterizedType) type, mapper);
        }
        if (JieType.isWildcard(type)) {
            return replaceType((WildcardType) type, mapper);
        }
        if (JieType.isGenericArray(type)) {
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
        if (!JieType.isClass(newRawType)) {
            throw new ReflectionException("Unsupported raw type: " + newRawType + ".");
        }
        if (!Jie.equals(rawType, newRawType)) {
            matched = true;
        }
        @Nullable Type ownerType = type.getOwnerType();
        Type newOwnerType = null;
        if (ownerType != null) {
            newOwnerType = replaceType(ownerType, mapper);
            if (!Jie.equals(ownerType, newOwnerType)) {
                matched = true;
            }
        }
        Type[] actualTypeArguments = type.getActualTypeArguments();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            Type newActualTypeArgument = replaceType(actualTypeArgument, mapper);
            if (!Jie.equals(actualTypeArgument, newActualTypeArgument)) {
                matched = true;
                actualTypeArguments[i] = newActualTypeArgument;
            }
        }
        if (matched) {
            return JieType.newParameterizedType((Class<?>) newRawType, actualTypeArguments, newOwnerType);
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
            if (!Jie.equals(upperBound, newUpperBound)) {
                matched = true;
                upperBounds[i] = newUpperBound;
            }
        }
        Type[] lowerBounds = type.getLowerBounds();
        for (int i = 0; i < lowerBounds.length; i++) {
            Type lowerBound = lowerBounds[i];
            Type newLowerBound = replaceType(lowerBound, mapper);
            if (!Jie.equals(lowerBound, newLowerBound)) {
                matched = true;
                lowerBounds[i] = newLowerBound;
            }
        }
        if (matched) {
            return JieType.newWildcardType(upperBounds, lowerBounds);
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
        if (!Jie.equals(componentType, newComponentType)) {
            matched = true;
        }
        if (matched) {
            return JieType.newArrayType(newComponentType);
        } else {
            return type;
        }
    }
}
