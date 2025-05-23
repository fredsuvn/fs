package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.cache.SimpleCache;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieCollect;
import xyz.sunqian.common.collect.JieMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
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
 * Static utility class for reflection.
 *
 * @author sunqian
 */
public class JieReflect {

    private static final @Nonnull Map<Class<?>, Class<?>> CLASS_WRAPPERS = Jie.map(
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        short.class, Short.class,
        char.class, Character.class,
        int.class, Integer.class,
        long.class, Long.class,
        float.class, Float.class,
        double.class, Double.class,
        void.class, Void.class
    );

    private static final @Nonnull Map<Class<?>, String> PRIMITIVE_ARRAY_CLASS_NAMES = Jie.map(
        boolean.class, "[Z",
        byte.class, "[B",
        short.class, "[S",
        char.class, "[C",
        int.class, "[I",
        long.class, "[J",
        float.class, "[F",
        double.class, "[D"
    );

    /**
     * Returns the last name of the given class. The last name is sub-string after last dot. For example: {@code String}
     * is last name of {@code java.lang.String}.
     *
     * @param cls the given class
     * @return the last name of given class
     */
    public static @Nonnull String getLastName(@Nonnull Class<?> cls) {
        String name = cls.getName();
        int index = JieString.lastIndexOf(name, ".");
        return name.substring(index + 1);
    }

    /**
     * Returns the raw type of the given type. the given type must be a {@link Class} or {@link ParameterizedType}.
     * Returns {@code null} if the given type neither be {@link Class} nor {@link ParameterizedType}.
     *
     * @param type the given type
     * @return the raw type of given type or {@code null}
     */
    public static @Nullable Class<?> getRawType(@Nonnull Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
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
     * Returns the field of the specified name from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getField(String)}. If not found then {@link Class#getDeclaredField(String)}
     * will be used next. If still not found, this method will recursively call itself with superclass (from
     * {@link Class#getSuperclass()}) and interfaces (from {@link Class#getInterfaces()}) until found.
     *
     * @param cls  the given class
     * @param name the specified field name
     * @return the field of the specified name from the given class, or {@code null} if not found
     */
    public static @Nullable Field getField(@Nonnull Class<?> cls, @Nonnull String name) {
        return getField(cls, name, true, true);
    }

    /**
     * Returns the field of the specified name from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getField(String)}. If not found and the {@code searchDeclared} is true,
     * then {@link Class#getDeclaredField(String)} will be used next. If still not found and the {@code searchSuper} is
     * true, this method will recursively call itself with superclass (from {@link Class#getSuperclass()}) and
     * interfaces (from {@link Class#getInterfaces()}) until found.
     *
     * @param cls            the given class
     * @param name           the specified field name
     * @param searchDeclared specifies whether searches declared fields
     * @param searchSuper    specifies whether searches superclasses and interfaces recursively
     * @return the field of the specified name from the given class, or {@code null} if not found
     */
    public static @Nullable Field getField(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        boolean searchDeclared,
        boolean searchSuper
    ) {
        try {
            return cls.getField(name);
        } catch (NoSuchFieldException e) {
            if (searchDeclared) {
                try {
                    return cls.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    if (!searchSuper) {
                        return null;
                    }
                    // Searches super class:
                    Class<?> superclass = cls.getSuperclass();
                    if (superclass != null) {
                        Field result = getField(superclass, name, true, true);
                        if (result != null) {
                            return result;
                        }
                    }
                    // Searches interfaces:
                    Class<?>[] interfaces = cls.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        Field result = getField(anInterface, name, true, true);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Returns the method of the specified name and parameter types from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getMethod(String, Class[])}. If not found, then
     * {@link Class#getDeclaredMethod(String, Class[])} will be used next. If still not found, this method will
     * recursively call itself with superclass (from {@link Class#getSuperclass()}) and interfaces (from
     * {@link Class#getInterfaces()}) until found.
     *
     * @param cls            the given class
     * @param name           the specified method name
     * @param parameterTypes the specified parameter types
     * @return the method of the specified name and parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Method getMethod(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        @Nonnull @RetainedParam Class<?>[] parameterTypes
    ) {
        return getMethod(cls, name, parameterTypes, true, true);
    }

    /**
     * Returns the method of the specified name and parameter types from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getMethod(String, Class[])}. If not found and the {@code searchDeclared} is
     * true, then {@link Class#getDeclaredMethod(String, Class[])} will be used next. If still not found and the
     * {@code searchSuper} is true, this method will recursively call itself with superclass (from
     * {@link Class#getSuperclass()}) and interfaces (from {@link Class#getInterfaces()}) until found.
     *
     * @param cls            the given class
     * @param name           the specified method name
     * @param parameterTypes the specified parameter types
     * @param searchDeclared specifies whether searches declared methods
     * @param searchSuper    specifies whether searches superclasses and interfaces recursively
     * @return the method of the specified name and parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Method getMethod(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        @Nonnull @RetainedParam Class<?>[] parameterTypes,
        boolean searchDeclared,
        boolean searchSuper
    ) {
        try {
            return cls.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (searchDeclared) {
                try {
                    return cls.getDeclaredMethod(name, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    if (!searchSuper) {
                        return null;
                    }
                    // Searches super class:
                    Class<?> superclass = cls.getSuperclass();
                    if (superclass != null) {
                        Method result = getMethod(superclass, name, parameterTypes, true, true);
                        if (result != null) {
                            return result;
                        }
                    }
                    // Searches interfaces:
                    Class<?>[] interfaces = cls.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        Method result = getMethod(anInterface, name, parameterTypes, true, true);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Returns the constructor of the given class with the specified parameter types, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getConstructor(Class[])}. If not found, then
     * {@link Class#getDeclaredConstructor(Class[])} will be used next.
     *
     * @param cls            the given class
     * @param parameterTypes the specified parameter types
     * @return the constructor of the given class with the specified parameter types, or {@code null} if not found
     */
    public static @Nullable Constructor<?> getConstructor(
        @Nonnull Class<?> cls,
        @Nonnull @RetainedParam Class<?>[] parameterTypes
    ) {
        return getConstructor(cls, parameterTypes, true);
    }

    /**
     * Returns the constructor of the given class with the specified parameter types, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getConstructor(Class[])}. If not found and the {@code searchDeclared} is
     * true, then {@link Class#getDeclaredConstructor(Class[])} will be used next.
     *
     * @param cls            the given class
     * @param parameterTypes the specified parameter types
     * @param searchDeclared specifies whether searches declared constructors
     * @return the constructor of the given class with the specified parameter types, or {@code null} if not found
     */
    public static @Nullable Constructor<?> getConstructor(
        @Nonnull Class<?> cls,
        @Nonnull @RetainedParam Class<?>[] parameterTypes,
        boolean searchDeclared
    ) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            if (!searchDeclared) {
                return null;
            }
            try {
                return cls.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }

    /**
     * Returns a new instance for the given class name with the empty constructor, may be {@code null} if fails.
     * <p>
     * This method first uses {@link #classForName(String, ClassLoader)} to get the class of the given class name, then
     * call {@link #newInstance(Class)} to create a new instance.
     *
     * @param className the given class name
     * @param <T>       the instance's type
     * @return a new instance for the given class name with the empty constructor, may be {@code null} if fails
     */
    public static <T> @Nullable T newInstance(@Nonnull String className) {
        return newInstance(className, null);
    }

    /**
     * Returns a new instance for the given class name with the empty constructor, may be {@code null} if fails.
     * <p>
     * This method first uses {@link #classForName(String, ClassLoader)} to get the class of the given class name, then
     * call {@link #newInstance(Class)} to create a new instance.
     *
     * @param className the given class name
     * @param loader    the given class loader, may be {@code null} if loaded by the default loader
     * @param <T>       the instance's type
     * @return a new instance for the given class name with the empty constructor, may be {@code null} if fails
     */
    public static <T> @Nullable T newInstance(@Nonnull String className, @Nullable ClassLoader loader) {
        Class<?> cls = classForName(className, loader);
        if (cls == null) {
            return null;
        }
        return newInstance(cls);
    }

    /**
     * Returns a new instance for the given class with the empty constructor, may be {@code null} if fails.
     *
     * @param <T> the instance's type
     * @return a new instance for the given class with the empty constructor, may be {@code null} if fails
     */
    public static <T> @Nullable T newInstance(@Nonnull Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            return newInstance(constructor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new instance with the given constructor and arguments, may be {@code null} if fails.
     *
     * @param constructor the given constructor
     * @param args        the given arguments
     * @param <T>         the instance's type
     * @return a new instance with the given constructor and arguments, may be {@code null} if fails
     */
    public static <T> @Nullable T newInstance(@Nonnull Constructor<?> constructor, Object @Nonnull ... args) {
        try {
            return Jie.as(constructor.newInstance(args));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the array class whose component type is the specified type, may be {@code null} if fails. Note
     * {@link TypeVariable} and {@link WildcardType} are unsupported.
     *
     * @param componentType the specified component type
     * @return the array class whose component type is the specified type, may be {@code null} if fails
     */
    public static @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
        Class<?> componentClass = toRuntimeClass(componentType);
        if (componentClass == null) {
            return null;
        }
        String name = arrayClassName(componentClass);
        if (name == null) {
            return null;
        }
        return classForName(name, componentClass.getClassLoader());
    }

    /**
     * Returns the array class name whose component type is the specified type, may be {@code null} if fails.
     *
     * @param componentType the specified component type
     * @return the array class name whose component type is the specified type, may be {@code null} if fails
     */
    public static @Nullable String arrayClassName(@Nonnull Class<?> componentType) {
        if (componentType.isArray()) {
            return "[" + componentType.getName();
        }
        if (componentType.isPrimitive()) {
            // No void[]
            return PRIMITIVE_ARRAY_CLASS_NAMES.get(componentType);
        }
        return "[L" + componentType.getName() + ";";
    }

    /**
     * Returns the runtime class of the given type, may be {@code null} if fails. Note {@link TypeVariable} and
     * {@link WildcardType} are unsupported.
     *
     * @param type the given type
     * @return the runtime class of the given type, may be {@code null} if fails
     */
    public static @Nullable Class<?> toRuntimeClass(@Nonnull Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return toRuntimeClass(rawType);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Type componentType = arrayType.getGenericComponentType();
            Class<?> componentClass = toRuntimeClass(componentType);
            if (componentClass == null) {
                return null;
            }
            return arrayClass(componentClass);
        }
        return null;
    }

    /**
     * Returns the wrapper class if the given class is primitive, else return the given class itself.
     *
     * @param cls the given class
     * @return the wrapper class if the given class is primitive, else return the given class itself
     */
    public static @Nonnull Class<?> wrapperClass(@Nonnull Class<?> cls) {
        if (!cls.isPrimitive()) {
            return cls;
        }
        return wrapperPrimitive(cls);
    }

    private static Class<?> wrapperPrimitive(Class<?> cls) {
        Class<?> wrapper = CLASS_WRAPPERS.get(cls);
        if (wrapper != null) {
            return wrapper;
        }
        throw new UnreachablePointException("Unknown primitive type: " + cls + ".");
    }

    /**
     * Returns whether the current runtime exists the class specified by the given class name and loaded by the default
     * class loader.
     *
     * @param className the given class name
     * @return whether the current runtime exists the class specified by the given class name and loaded by the default
     * class loader
     */
    public static boolean classExists(@Nonnull String className) {
        return classExists(className, null);
    }

    /**
     * Returns whether the current runtime exists the class specified by the given class name and loaded by the given
     * class loader.
     *
     * @param className the given class name
     * @param loader    the given class loader, may be {@code null} if loaded by the default loader
     * @return whether the current runtime exists the class specified by the given class name and loaded by the given
     * class loader
     */
    public static boolean classExists(@Nonnull String className, @Nullable ClassLoader loader) {
        return classForName(className, loader) != null;
    }

    /**
     * Returns the {@link Class} object whose name is the given name. This method calls {@link Class#forName(String)} if
     * the given class loader is {@code null}, or {@link Class#forName(String, boolean, ClassLoader)} if not.
     *
     * @param name   the given name of the class or interface
     * @param loader the given class loader, may be {@code null}
     * @return the {@link Class} object whose name is the given name
     */
    public static @Nullable Class<?> classForName(@Nonnull String name, @Nullable ClassLoader loader) {
        try {
            return loader == null ? Class.forName(name) : Class.forName(name, true, loader);
        } catch (ClassNotFoundException e) {
            return null;
        }
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

    /**
     * Return a list to describe the generalized representation of the given type from the specified raw type, each
     * element of the list is actual type argument type of the raw type on given type. For example, for the types:
     * <pre>
     *     private static interface Z&lt;B, T, U, R&gt; {}
     *     private static class ZS implements Z&lt;String, Integer, Long, Boolean&gt; {}
     * </pre>
     * The result of this method:
     * <pre>
     *     getActualTypeArguments(ZS.class, Z.class);
     * </pre>
     * will be:
     * <pre>
     *     [String.class, Integer.class, Long.class, Boolean.class]
     * </pre>
     * Typically, the given type is same with or subtype of the specified raw type. And this method returns an empty
     * list if failed to get actual type arguments.
     *
     * @param type     given type
     * @param baseType specified raw type
     * @return a list to describe the generalized representation of the given type from the specified raw type
     */
    public static @Nonnull List<Type> resolveActualTypeArguments(
        @Nonnull Type type, @Nonnull Class<?> baseType
    ) throws ReflectionException {
        Class<?> cls = toRuntimeClass(type);
        if (cls == null) {
            throw new ReflectionException("Unsupported type: " + type + ".");
        }
        if (!baseType.isAssignableFrom(cls)) {
            throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
        }

        // boolean supportedType = false;
        // if (type instanceof Class<?>) {
        //     supportedType = true;
        //     Class<?> subType = (Class<?>) type;
        //     if (!baseType.isAssignableFrom(subType)) {
        //         throw new ReflectionException(
        //             type.getTypeName() + " is not subtype of " + baseType.getTypeName() + "."
        //         );
        //     }
        // }
        // if (type instanceof ParameterizedType) {
        //     supportedType = true;
        //     ParameterizedType subType = (ParameterizedType) type;
        //     Class<?> subRawType = (Class<?>) subType.getRawType();
        //     if (!baseType.isAssignableFrom(subRawType)) {
        //         return Collections.emptyList();
        //     }
        // }
        // if (!supportedType) {
        //     return Collections.emptyList();
        // }

        TypeVariable<?>[] typeParameters = baseType.getTypeParameters();
        if (JieArray.isEmpty(typeParameters)) {
            return Collections.emptyList();
        }
        Map<TypeVariable<?>, Type> typeArguments = getTypeParameterMapping(type);
        Set<Type> stack = new HashSet<>();
        return Arrays.stream(typeParameters)
            .map(it -> {
                Type nestedValue = JieMap.resolveChain(typeArguments, it, stack);
                stack.clear();
                return nestedValue == null ? it : nestedValue;
            }).collect(Collectors.toList());
    }

    /**
     * Returns a type parameters mapping for given type, the key of mapping is type parameter, and the value is actual
     * type argument or inherited type parameter. For example, for these types:
     * <pre>
     *     private static class X extends Y&lt;Integer, Long&gt;{}
     *     private static class Y&lt;K, V&gt; implements Z&lt;Float, Double, V&gt; {}
     *     private static interface Z&lt;T, U, R&gt;{}
     * </pre>
     * The result of this method
     * <pre>
     *     getTypeParameterMapping(x.class)
     * </pre>
     * will be:
     * <pre>
     *     T -&gt; Float
     *     U -&gt; Double
     *     R -&gt; V
     *     K -&gt; Integer
     *     V -&gt; Long
     * </pre>
     * It is recommended using {@link JieCollect#getRecursive(Map, Object, Set)} to get actual type of type variable in
     * the result.
     *
     * @param type given type
     * @return a mapping of type parameters for given type
     */
    @Immutable
    public static Map<TypeVariable<?>, Type> getTypeParameterMapping(Type type) {
        return TypeParameterCache.get(type, it -> {
            Map<TypeVariable<?>, Type> result = new HashMap<>();
            getTypeParameterMapping(type, result);
            return Collections.unmodifiableMap(result);
        });
    }

    private static void getTypeParameterMapping(Type type, @OutParam Map<TypeVariable<?>, Type> mapping) {
        if (type instanceof Class) {
            Class<?> cur = (Class<?>) type;
            while (true) {
                Type superType = cur.getGenericSuperclass();
                if (superType != null) {
                    mappingTypeVariables(superType, mapping);
                }
                mappingGenericInterfacesTypeArgs(cur, mapping);
                cur = cur.getSuperclass();
                if (cur == null) {
                    return;
                }
            }
        }
        if (type instanceof ParameterizedType) {
            mappingTypeVariables(type, mapping);
            getTypeParameterMapping(((ParameterizedType) type).getRawType(), mapping);
        }
    }

    private static void mappingGenericInterfacesTypeArgs(Class<?> cls, @OutParam Map<TypeVariable<?>, Type> mapping) {
        Type[] genericInterfaces = cls.getGenericInterfaces();
        if (JieArray.isEmpty(genericInterfaces)) {
            return;
        }
        for (Type genericInterface : genericInterfaces) {
            mappingTypeVariables(genericInterface, mapping);
            Class<?> interfaceClass = getRawType(genericInterface);
            // Never null for interfaceClass
            mappingGenericInterfacesTypeArgs(interfaceClass, mapping);
        }
    }

    private static void mappingTypeVariables(Type type, @OutParam Map<TypeVariable<?>, Type> mapping) {
        if (!(type instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
        TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<?> typeVariable = typeParameters[i];
            Type actualTypeArgument = actualTypeArguments[i];
            mapping.put(typeVariable, actualTypeArgument);
        }
    }

    /**
     * Replaces the types in given {@code type} (including itself) which equals to {@code matcher} with
     * {@code replacement}. This method is equivalent to ({@link #replaceType(Type, Type, Type, boolean)}):
     * <pre>
     *     return replaceType(type, matcher, replacement, true);
     * </pre>
     *
     * @param type        type to be replaced
     * @param matcher     matcher type
     * @param replacement replacement type
     * @return type after replacing
     */
    public static Type replaceType(Type type, Type matcher, Type replacement) {
        return replaceType(type, matcher, replacement, true);
    }

    /**
     * Replaces the types in given {@code type} (including itself) which equals to {@code matcher} with
     * {@code replacement}. This method supports:
     * <ul>
     *     <li>
     *         ParameterizedType: rawType, ownerType, actualTypeArguments;
     *     </li>
     *     <li>
     *         WildcardType: upperBounds, lowerBounds;
     *     </li>
     *     <li>
     *         GenericArrayType: componentType;
     *     </li>
     * </ul>
     * If the {@code deep} parameter is true, this method will recursively resolve unmatched types to replace.
     * <p>
     * If no type is matched or the type is not supported for replacing, return given type itself.
     *
     * @param type        type to be replaced
     * @param matcher     matcher type
     * @param replacement replacement type
     * @param deep        whether to recursively replace unmatched types
     * @return type after replacing
     */
    public static Type replaceType(Type type, Type matcher, Type replacement, boolean deep) {
        if (Objects.equals(type, matcher)) {
            return replacement;
        }
        boolean matched = false;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Objects.equals(rawType, matcher)) {
                matched = true;
                rawType = (Class<?>) replacement;
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                if (Objects.equals(ownerType, matcher)) {
                    matched = true;
                    ownerType = replacement;
                } else if (deep) {
                    Type newOwnerType = replaceType(ownerType, matcher, replacement, true);
                    if (!Objects.equals(ownerType, newOwnerType)) {
                        matched = true;
                        ownerType = newOwnerType;
                    }
                }
            }
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                if (Objects.equals(actualTypeArgument, matcher)) {
                    matched = true;
                    actualTypeArguments[i] = replacement;
                } else if (deep) {
                    Type newActualTypeArgument = replaceType(actualTypeArgument, matcher, replacement, true);
                    if (!Objects.equals(actualTypeArgument, newActualTypeArgument)) {
                        matched = true;
                        actualTypeArguments[i] = newActualTypeArgument;
                    }
                }
            }
            if (matched) {
                return JieType.parameterized(rawType, actualTypeArguments, ownerType);
            } else {
                return type;
            }
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            for (int i = 0; i < upperBounds.length; i++) {
                Type bound = upperBounds[i];
                if (Objects.equals(bound, matcher)) {
                    matched = true;
                    upperBounds[i] = replacement;
                } else if (deep) {
                    Type newBound = replaceType(bound, matcher, replacement, true);
                    if (!Objects.equals(bound, newBound)) {
                        matched = true;
                        upperBounds[i] = newBound;
                    }
                }
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            for (int i = 0; i < lowerBounds.length; i++) {
                Type bound = lowerBounds[i];
                if (Objects.equals(bound, matcher)) {
                    matched = true;
                    lowerBounds[i] = replacement;
                } else if (deep) {
                    Type newBound = replaceType(bound, matcher, replacement, true);
                    if (!Objects.equals(bound, newBound)) {
                        matched = true;
                        lowerBounds[i] = newBound;
                    }
                }
            }
            if (matched) {
                return JieType.wildcard(upperBounds, lowerBounds);
            } else {
                return type;
            }
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            if (Objects.equals(componentType, matcher)) {
                matched = true;
                componentType = replacement;
            } else if (deep) {
                Type newComponentType = replaceType(componentType, matcher, replacement, true);
                if (!Objects.equals(componentType, newComponentType)) {
                    matched = true;
                    componentType = newComponentType;
                }
            }
            if (matched) {
                return JieType.array(componentType);
            } else {
                return type;
            }
        }
        return type;
    }

    private static final class TypeParameterCache {

        private static final @Nonnull SimpleCache<Type, Map<TypeVariable<?>, Type>> cache = SimpleCache.ofSoft();

        private static @Nonnull Map<TypeVariable<?>, Type> get(
            @Nonnull Type type,
            @Nonnull Function<@Nonnull Type, @Nonnull Map<@Nonnull TypeVariable<?>, @Nonnull Type>> producer
        ) {
            return Objects.requireNonNull(cache.get(type, producer));
        }
    }
}
