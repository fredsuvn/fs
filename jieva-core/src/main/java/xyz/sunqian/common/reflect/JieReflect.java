package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    private static final Map<Class<?>, Class<?>> CLASS_WRAPPERS = Jie.map(
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

    private static final Map<Class<?>, String> PRIMITIVE_ARRAY_CLASS_NAMES = Jie.map(
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
     * Returns the super classes as {@link Iterator} for the given class.
     *
     * @param cls the given class
     * @return the super classes as {@link Iterator} for the given class
     */
    public static @Nonnull Iterator<Class<?>> getSuperTypes(@Nonnull Class<?> cls) {
        return new Iterator<Class<?>>() {

            private @Nullable Class<?>[] superInters = null;
            private int cur = -2;
            private @Nullable Class<?> next = getNext();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Class<?> next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                Class<?> result = next;
                next = getNext();
                return result;
            }

            private @Nullable Class<?> getNext() {
                if (cur == -2) {
                    cur++;
                    Class<?> superClass = cls.getSuperclass();
                    if (superClass != null) {
                        return superClass;
                    }
                }
                if (cur == -1) {
                    superInters = cls.getInterfaces();
                    if (JieArray.isEmpty(superInters)) {
                        return null;
                    }
                    cur++;
                }
                if (cur < superInters.length) {
                    return superInters[cur++];
                }
                return null;
            }
        };
    }

    private static final class SuperTypesIterator implements Iterator<Class<?>> {

        private @Nullable SuperDeclaration declaration;

        private SuperTypesIterator(@Nonnull Class<?> cls) {
            this.declaration = getSuperDeclaration(cls);
        }

        @Override
        public boolean hasNext() {
            return declaration != null;
        }

        @Override
        public Class<?> next() {
            SuperDeclaration declaration = this.declaration;
            if (declaration == null) {
                throw new NoSuchElementException();
            }
            if (declaration.index == -1) {
                declaration.index++;
                if (declaration.superclass != null) {
                    return declaration.superclass;
                }
            }
            if (declaration.index < declaration.interfaces.length) {
                return declaration.interfaces[declaration.index++];
            }
            return null;
        }

        private @Nullable SuperDeclaration getSuperDeclaration(@Nonnull Class<?> cls) {
            if (Jie.equals(cls, Object.class)) {
                return null;
            }
            Class<?> superclass = cls.getSuperclass();
            Class<?>[] interfaces = cls.getInterfaces();
            return new SuperDeclaration(superclass, interfaces);
        }

        private static final class SuperDeclaration {

            private final @Nullable Class<?> superclass;
            private final @Nonnull Class<?>[] interfaces;
            private int index = -1;

            private SuperDeclaration(Class<?> superclass, Class<?>[] interfaces) {
                this.superclass = superclass;
                this.interfaces = interfaces;
            }
        }
    }

    /**
     * Searches and returns the field of the specified name from the given type, or returns {@code null} for searching
     * failed. This method is equivalent to ({@link #searchField(Type, String, boolean)}):
     * <pre>{@code
     * return getField(type, name, true);
     * }</pre>
     *
     * @param type the given type
     * @param name the specified field name
     * @return the field of the specified name from the given type
     */
    public static @Nullable Field getField(@Nonnull Type type, @Nonnull String name) {
        return searchField(type, name, true);
    }

    /**
     * Searches and returns the field of the specified name from the given type.
     * <p>
     * The searching in order of {@link Class#getField(String)} then {@link Class#getDeclaredField(String)}. If
     * {@code searchSuper} is true and the searching is failed in current type, this method will recursively call
     * {@link Class#getGenericSuperclass()} to search super types. If still not found, recursively call
     * {@link Class#getGenericInterfaces()}.
     * <p>
     * Returns {@code null} for searching failed.
     *
     * @param type        the given type
     * @param name        the specified field name
     * @param searchSuper whether recursively searches super types
     * @return the field of the specified name from the given type
     */
    public static @Nullable Field searchField(@Nonnull Type type, @Nonnull String name, boolean searchSuper) {
        Class<?> rawType = getRawType(type);
        if (rawType == null) {
            return null;
        }
        try {
            return rawType.getField(name);
        } catch (NoSuchFieldException e) {
            try {
                return rawType.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                if (!searchSuper) {
                    return null;
                }
                Iterator<Class<?>> superTypes = getSuperTypes(rawType);
                while (superTypes.hasNext()) {
                    Field f = searchField(superTypes.next(), name, true);
                    if (f != null) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches and returns the method of the specified name and parameter types from the given type. returns
     * {@code null} for searching failed. This method is equivalent to
     * ({@link #getMethod(Type, String, Class[], boolean)}):
     * <pre>{@code
     * return getMethod(type, name, parameterTypes, true);
     * }</pre>
     *
     * @param type           the given type
     * @param name           the specified field name
     * @param parameterTypes the specified parameter types
     * @return the method of the specified name and parameter types from the given type
     */
    public static @Nullable Method getMethod(
        @Nonnull Type type,
        @Nonnull String name,
        @Nonnull Class<?>[] parameterTypes
    ) {
        return getMethod(type, name, parameterTypes, true);
    }

    /**
     * Searches and returns the method of the specified name and parameter types from the given type.
     * <p>
     * The searching in order of {@link Class#getMethod(String, Class[])} then
     * {@link Class#getDeclaredMethod(String, Class[])}. if {@code searchSuper} is true, and if searching failed in
     * current type, this method will recursively call {@link Class#getGenericSuperclass()} to search super types. If
     * still not found, recursively call {@link Class#getGenericInterfaces()}.
     * <p>
     * Returns {@code null} for searching failed.
     *
     * @param type           the given type
     * @param name           the specified field name
     * @param parameterTypes the specified parameter types
     * @param searchSuper    whether recursively searches super types
     * @return the method of the specified name and parameter types from the given type
     */
    public static @Nullable Method getMethod(
        @Nonnull Type type,
        @Nonnull String name,
        @Nonnull Class<?>[] parameterTypes,
        boolean searchSuper
    ) {
        Class<?> rawType = getRawType(type);
        if (rawType == null) {
            return null;
        }
        try {
            return rawType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return rawType.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException ex) {
                if (!searchSuper) {
                    return null;
                }
                Iterator<Class<?>> superTypes = getSuperTypes(rawType);
                while (superTypes.hasNext()) {
                    Method m = getMethod(superTypes.next(), name, parameterTypes, true);
                    if (m != null) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches and returns the constructor of the specified parameter types from the given type. returns {@code null}
     * for searching failed. This method is equivalent to ({@link #getConstructor(Class, Class[], boolean)}):
     * <pre>{@code
     * return getConstructor(type, parameterTypes, true);
     * }</pre>
     *
     * @param type           the given type
     * @param parameterTypes the specified parameter types
     * @return the constructor of the specified parameter types from the given type
     */
    public static @Nullable Constructor<?> getConstructor(Class<?> type, Class<?>[] parameterTypes) {
        return getConstructor(type, parameterTypes, true);
    }

    /**
     * Searches and returns the constructor of the specified parameter types from the given type.
     * <p>
     * The method uses {@link Class#getConstructor(Class[])} to find the constructor. If searching failed and
     * {@code searchDeclared} is true, this method will try {@link Class#getDeclaredConstructor(Class[])}.
     * <p>
     * Returns {@code null} for searching failed.
     *
     * @param type           the given type
     * @param parameterTypes the specified parameter types
     * @param searchDeclared whether searches declared constructors
     * @return the constructor of the specified parameter types from the given type
     */
    @Nullable
    public static Constructor<?> getConstructor(Class<?> type, Class<?>[] parameterTypes, boolean searchDeclared) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            if (!searchDeclared) {
                return null;
            }
            try {
                return type.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }

    /**
     * Returns new instance for given class name.
     * <p>
     * This method first uses {@link #classForName(String, ClassLoader)} to load given class, then call
     * {@link #newInstance(Class)} to create instance.
     * <p>
     * Returns {@code null} if failed.
     *
     * @param className given class name
     * @param <T>       type of result
     * @return a new instance of given class name with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(String className) {
        return newInstance(className, null);
    }

    /**
     * Returns new instance for given class name.
     * <p>
     * This method first uses {@link #classForName(String, ClassLoader)} to load given class, then call
     * {@link #newInstance(Class)} to create instance.
     * <p>
     * Returns {@code null} if failed.
     *
     * @param className given class name
     * @param loader    given class loader, may be {@code null} if loaded by default loader
     * @param <T>       type of result
     * @return a new instance of given class name with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(String className, @Nullable ClassLoader loader) {
        Class<?> cls = classForName(className, loader);
        if (cls == null) {
            return null;
        }
        return newInstance(cls);
    }

    /**
     * Creates a new instance of given type with empty constructor, may be {@code null} if failed.
     *
     * @param type given type
     * @param <T>  type of instance
     * @return a new instance of given type with empty constructor or null
     */
    @Nullable
    public static <T> T newInstance(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            return newInstance(constructor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new instance with given constructor and arguments, may be {@code null} if failed.
     *
     * @param constructor given constructor
     * @param args        arguments
     * @param <T>         type of instance
     * @return a new instance with given constructor and arguments
     */
    @Nullable
    public static <T> T newInstance(Constructor<?> constructor, Object... args) {
        try {
            return Jie.as(constructor.newInstance(args));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns array class of given component type.
     *
     * @param componentType given component type
     * @return array class of given component type
     * @throws ReflectionException if any reflection problem occurs
     */
    public static Class<?> arrayClass(Type componentType) throws ReflectionException {
        return arrayClass(componentType, null);
    }

    /**
     * Returns array class of given component type with specified class loader.
     *
     * @param componentType given component type
     * @param classLoader   specified class loader
     * @return array class of given component type
     * @throws ReflectionException if any reflection problem occurs
     */
    public static Class<?> arrayClass(
        Type componentType, @Nullable ClassLoader classLoader
    ) throws ReflectionException {
        if (componentType instanceof Class) {
            String name = arrayClassName((Class<?>) componentType);
            return classForName(name, classLoader);
        }
        if (componentType instanceof ParameterizedType) {
            return arrayClass(((ParameterizedType) componentType).getRawType(), classLoader);
        }
        if (componentType instanceof GenericArrayType) {
            StringBuilder name = new StringBuilder();
            Type cur = componentType;
            do {
                name.append("[");
                if (cur instanceof GenericArrayType) {
                    cur = ((GenericArrayType) cur).getGenericComponentType();
                } else if (cur instanceof Class) {
                    name.append("L").append(((Class<?>) cur).getName()).append(";");
                    break;
                } else if (cur instanceof ParameterizedType) {
                    name.append("L").append(((Class<?>) ((ParameterizedType) cur).getRawType()).getName()).append(";");
                    break;
                } else {
                    throw new ReflectionException("Illegal component type: " + componentType);
                }
            } while (true);
            return classForName(name.toString(), classLoader);
        }
        throw new ReflectionException("Illegal component type: " + componentType);
    }

    /**
     * Returns array class name of which component type is specified type.
     *
     * @param componentType specified component type
     * @return array class name of which component type is specified type
     * @throws ReflectionException if any reflection problem occurs
     */
    public static String arrayClassName(Class<?> componentType) throws ReflectionException {
        if (componentType.isArray()) {
            return "[" + componentType.getName();
        }
        if (componentType.isPrimitive()) {
            String name = PRIMITIVE_ARRAY_CLASS_NAMES.get(componentType);
            if (name != null) {
                return name;
            }
            // void
            throw new ReflectionException("Array class doesn't exists: " + componentType.getName() + ".");
        }
        return "[L" + componentType.getName() + ";";
    }

    /**
     * Returns wrapper class if given class is primitive, else return itself.
     *
     * @param cls given class
     * @return wrapper class if given class is primitive, else return itself
     */
    public static Class<?> wrapper(Class<?> cls) {
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
        throw new NotPrimitiveException(cls);
    }

    /**
     * Returns whether current runtime exists the class specified by given class name.
     *
     * @param className given class name
     * @return whether current runtime exists the class specified by given class name
     */
    public static boolean classExists(String className) {
        return classExists(className, null);
    }

    /**
     * Returns whether current runtime exists the class specified by given class name and loaded by given class loader.
     *
     * @param className given class name
     * @param loader    given class loader, may be {@code null} if loaded by default loader
     * @return whether current runtime exists the class specified by given class name and loaded by given class loader
     */
    public static boolean classExists(String className, @Nullable ClassLoader loader) {
        return classForName(className, loader) != null;
    }

    /**
     * Returns the Class object associated with the class or interface with the given string name. This method calls
     * {@link Class#forName(String)} if given class loader is {@code null}, or
     * {@link Class#forName(String, boolean, ClassLoader)} if it is not {@code null}.
     *
     * @param name   name of class or interface
     * @param loader given class loader, may be {@code null} if loaded by default loader
     * @return the Class object associated with the class or interface with the given string name
     */
    @Nullable
    public static Class<?> classForName(String name, @Nullable ClassLoader loader) {
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
    public static boolean isAssignable(Type assigned, Type assignee) {
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
     * @param type    given type
     * @param rawType specified raw type
     * @return a list to describe the generalized representation of the given type from the specified raw type
     */
    public static List<Type> getActualTypeArguments(Type type, Class<?> rawType) {
        boolean supportedType = false;
        if (type instanceof Class<?>) {
            supportedType = true;
            Class<?> subType = (Class<?>) type;
            if (!rawType.isAssignableFrom(subType)) {
                return Collections.emptyList();
            }
        }
        if (type instanceof ParameterizedType) {
            supportedType = true;
            ParameterizedType subType = (ParameterizedType) type;
            Class<?> subRawType = (Class<?>) subType.getRawType();
            if (!rawType.isAssignableFrom(subRawType)) {
                return Collections.emptyList();
            }
        }
        if (!supportedType) {
            return Collections.emptyList();
        }
        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
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
