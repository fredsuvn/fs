package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieMap;
import xyz.sunqian.common.collect.JieStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
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
        String className = cls.getName();
        return getLastName(className);
    }

    private static @Nonnull String getLastName(@Nonnull String className) {
        int index = JieString.lastIndexOf(className, ".");
        return className.substring(index + 1);
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
        @Nonnull @RetainedParam Class<?> @Nonnull [] parameterTypes
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
        @Nonnull @RetainedParam Class<?> @Nonnull [] parameterTypes,
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
        @Nonnull @RetainedParam Class<?> @Nonnull [] parameterTypes
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
        @Nonnull @RetainedParam Class<?> @Nonnull [] parameterTypes,
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
        return JdkBack.arrayClass(componentType);
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
     * Returns whether the given type is an array type.
     *
     * @param type the given type
     * @return whether the given type is an array type
     */
    public static boolean isArray(@Nonnull Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).isArray();
        }
        return type instanceof GenericArrayType;
    }

    /**
     * Returns the component type of the given type if it is an array, {@code null} if it is not.
     *
     * @param type the given type
     * @return the component type of the given type if it is an array, {@code null} if it is not
     */
    public static @Nullable Type getComponentType(@Nonnull Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getComponentType();
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
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
            Type componentType = getComponentType(type);
            if (componentType == null) {
                throw new ReflectionException("Unsupported resolving between " + type + " and " + baseType);
            }
            return resolveActualTypeArguments(componentType, baseType.getComponentType());
        }
        Class<?> cls = toRuntimeClass(type);
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
                return actualType == null ? typeVariable : actualType;
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
        if (type instanceof Class) {
            Class<?> cur = (Class<?>) type;
            while (cur != null) {
                Type superclass = cur.getGenericSuperclass();
                if (superclass != null) {
                    mapTypeVariables(superclass, mapping);
                }
                Type[] interfaces = cur.getGenericInterfaces();
                mapTypeVariables(interfaces, mapping);
                cur = cur.getSuperclass();
            }
        }
        if (type instanceof ParameterizedType) {
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
            Class<?> rawClass = Objects.requireNonNull(getRawType(anInterface));
            mapTypeVariables(rawClass.getGenericInterfaces(), mapping);
        }
    }

    private static void mapTypeVariables(
        @Nonnull Type type,
        @Nonnull @OutParam Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping
    ) {
        if (!(type instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
        TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<?> typeParameter = typeParameters[i];
            Type typeArgument = typeArguments[i];
            mapping.put(typeParameter, typeArgument);
        }
    }

    /**
     * Resolves the given type and tries to replace the given type's contained types, which equals to the specified
     * matching type, with the specified replacement. Returns the type after the replacing, may be the given type itself
     * if no type can be replaced. For example, a type: {@code Map<String, Integer>}, the result of
     * {@code replaceType(type, Integer.class, Long.class)} will be: {@code Map<String, Long>}. Note the given type
     * itself can also be replaced.
     * <p>
     * This method supports resolving {@link Class}, {@link ParameterizedType}, {@link WildcardType} and
     * {@link GenericArrayType}.
     *
     * @param type        the given type to be resolved
     * @param matching    the specified matching type
     * @param replacement the specified replacement
     * @return the type after the replacing
     */
    public static @Nonnull Type replaceType(
        @Nonnull Type type,
        @Nonnull Type matching,
        @Nonnull Type replacement
    ) throws ReflectionException {
        if (Jie.equals(type, matching)) {
            return replacement;
        }
        if (type instanceof ParameterizedType) {
            return replaceType((ParameterizedType) type, matching, replacement);
        }
        if (type instanceof WildcardType) {
            return replaceType((WildcardType) type, matching, replacement);
        }
        if (type instanceof GenericArrayType) {
            return replaceType((GenericArrayType) type, matching, replacement);
        }
        return type;
    }

    private static @Nonnull Type replaceType(
        @Nonnull ParameterizedType type,
        @Nonnull Type matching,
        @Nonnull Type replacement
    ) throws ReflectionException {
        boolean matched = false;
        Type rawType = type.getRawType();
        Type newRawType = replaceType(rawType, matching, replacement);
        if (!Jie.equals(rawType, newRawType)) {
            matched = true;
        }
        Type ownerType = type.getOwnerType();
        Type newOwnerType = null;
        if (ownerType != null) {
            newOwnerType = replaceType(ownerType, matching, replacement);
            if (!Jie.equals(ownerType, newOwnerType)) {
                matched = true;
            }
        }
        Type[] actualTypeArguments = type.getActualTypeArguments();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            Type newActualTypeArgument = replaceType(actualTypeArgument, matching, replacement);
            if (!Jie.equals(actualTypeArgument, newActualTypeArgument)) {
                matched = true;
                actualTypeArguments[i] = newActualTypeArgument;
            }
        }
        if (matched) {
            return JieType.parameterized(newRawType, actualTypeArguments, newOwnerType);
        } else {
            return type;
        }
    }

    private static @Nonnull Type replaceType(
        @Nonnull WildcardType type,
        @Nonnull Type matching,
        @Nonnull Type replacement
    ) throws ReflectionException {
        boolean matched = false;
        Type[] upperBounds = type.getUpperBounds();
        for (int i = 0; i < upperBounds.length; i++) {
            Type upperBound = upperBounds[i];
            Type newUpperBound = replaceType(upperBound, matching, replacement);
            if (!Jie.equals(upperBound, newUpperBound)) {
                matched = true;
                upperBounds[i] = newUpperBound;
            }
        }
        Type[] lowerBounds = type.getLowerBounds();
        for (int i = 0; i < lowerBounds.length; i++) {
            Type lowerBound = lowerBounds[i];
            Type newLowerBound = replaceType(lowerBound, matching, replacement);
            if (!Jie.equals(lowerBound, newLowerBound)) {
                matched = true;
                lowerBounds[i] = newLowerBound;
            }
        }
        if (matched) {
            return JieType.wildcard(upperBounds, lowerBounds);
        } else {
            return type;
        }
    }

    private static @Nonnull Type replaceType(
        @Nonnull GenericArrayType type,
        @Nonnull Type matching,
        @Nonnull Type replacement
    ) throws ReflectionException {
        boolean matched = false;
        Type componentType = type.getGenericComponentType();
        Type newComponentType = replaceType(componentType, matching, replacement);
        if (!Jie.equals(componentType, newComponentType)) {
            matched = true;
        }
        if (matched) {
            return JieType.array(newComponentType);
        } else {
            return type;
        }
    }
}
