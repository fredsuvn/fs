package xyz.sunqian.common.runtime.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.base.system.JvmKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Iterator;

/**
 * Utilities kit for {@link Class}.
 *
 * @author sunqian
 */
public class ClassKit {

    /**
     * Returns the field of the specified name from the given class, or {@code null} if not found. This method first
     * uses {@link Class#getField(String)}. If not found, it will use {@link Class#getDeclaredField(String)} to try
     * again.
     *
     * @param cls  the given class
     * @param name the specified field name
     * @return the field of the specified name from the given class, or {@code null} if not found
     */
    public static @Nullable Field getField(@Nonnull Class<?> cls, @Nonnull String name) {
        return getField(cls, name, true);
    }

    /**
     * Returns the field of the specified name from the given class, or {@code null} if not found. This method first
     * uses {@link Class#getField(String)}. If not found and the {@code searchDeclared} is {@code true}, it will use
     * {@link Class#getDeclaredField(String)} to try again.
     *
     * @param cls            the given class
     * @param name           the specified field name
     * @param searchDeclared specifies whether searches declared fields
     * @return the field of the specified name from the given class, or {@code null} if not found
     */
    public static @Nullable Field getField(@Nonnull Class<?> cls, @Nonnull String name, boolean searchDeclared) {
        try {
            return cls.getField(name);
        } catch (NoSuchFieldException e) {
            if (searchDeclared) {
                try {
                    return cls.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Returns the field of the specified name from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getField(String)}. If the field is not found, then this method will use
     * {@link Class#getDeclaredField(String)} to search again. If the field is still not found, then this method will
     * traverse the hierarchy of superclasses and interfaces of the given class to search via
     * {@link Class#getDeclaredField(String)}.
     *
     * @param cls  the given class
     * @param name the specified field name
     * @return the field of the specified name from the given class, or {@code null} if not found
     */
    public static @Nullable Field searchField(@Nonnull Class<?> cls, @Nonnull String name) {
        Field field = getField(cls, name);
        if (field != null) {
            return field;
        }
        Iterator<Class<?>> supertypesAndInterfaces = toSupertypesAndInterfaces(cls);
        while (supertypesAndInterfaces.hasNext()) {
            Class<?> next = supertypesAndInterfaces.next();
            @Nullable Field nextField = searchField(next, name);
            if (nextField != null) {
                return nextField;
            }
        }
        return null;
    }

    /**
     * Returns the method of the specified name and parameter types from the given class, or {@code null} if not found.
     * This method first uses {@link Class#getMethod(String, Class[])}. If not found, it will use
     * {@link Class#getDeclaredMethod(String, Class[])} to try again.
     *
     * @param cls            the given class
     * @param name           the specified method name
     * @param parameterTypes the specified parameter types
     * @return the method of the specified name and parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Method getMethod(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        @Nonnull Class<?> @Nonnull @RetainedParam [] parameterTypes
    ) {
        return getMethod(cls, name, parameterTypes, true);
    }

    /**
     * Returns the method of the specified name and parameter types from the given class, or {@code null} if not found.
     * This method first uses {@link Class#getMethod(String, Class[])}. If not found and the {@code searchDeclared} is
     * {@code true}, it will use {@link Class#getDeclaredMethod(String, Class[])} to try again.
     *
     * @param cls            the given class
     * @param name           the specified method name
     * @param parameterTypes the specified parameter types
     * @param searchDeclared specifies whether searches declared methods
     * @return the method of the specified name and parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Method getMethod(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        @Nonnull Class<?> @Nonnull @RetainedParam [] parameterTypes,
        boolean searchDeclared
    ) {
        try {
            return cls.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (searchDeclared) {
                try {
                    return cls.getDeclaredMethod(name, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Returns the method of the specified name and parameter types from the given class, or {@code null} if not found.
     * <p>
     * This method searches via {@link Class#getMethod(String, Class[])}. If the method is not found, then this method
     * will use {@link Class#getDeclaredMethod(String, Class[])} to search again. If the method is still not found, then
     * this method will traverse the hierarchy of superclasses and interfaces of the given class to search via
     * {@link Class#getDeclaredMethod(String, Class[])}.
     *
     * @param cls  the given class
     * @param name the specified method name
     * @return the method of the specified name and parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Method searchMethod(
        @Nonnull Class<?> cls,
        @Nonnull String name,
        @Nonnull Class<?> @Nonnull @RetainedParam [] parameterTypes
    ) {
        Method method = getMethod(cls, name, parameterTypes);
        if (method != null) {
            return method;
        }
        Iterator<Class<?>> supertypesAndInterfaces = toSupertypesAndInterfaces(cls);
        while (supertypesAndInterfaces.hasNext()) {
            Class<?> next = supertypesAndInterfaces.next();
            @Nullable Method nextMethod = searchMethod(next, name, parameterTypes);
            if (nextMethod != null) {
                return nextMethod;
            }
        }
        return null;
    }

    private static @Nonnull Iterator<Class<?>> toSupertypesAndInterfaces(@Nonnull Class<?> cls) {
        return new Iterator<Class<?>>() {

            private int index = -1;
            private Class<?> @Nullable [] interfaces;
            private @Nullable Class<?> next = getNext();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Class<?> next() {
                Class<?> result = next;
                next = getNext();
                return result;
            }

            private @Nullable Class<?> getNext() {
                if (index == -1) {
                    index++;
                    Class<?> superclass = cls.getSuperclass();
                    if (superclass != null) {
                        return superclass;
                    }
                }
                if (interfaces == null) {
                    interfaces = cls.getInterfaces();
                }
                if (index < interfaces.length) {
                    return interfaces[index++];
                }
                return null;
            }
        };
    }

    /**
     * Returns the constructor of the specified parameter types from the given class, or {@code null} if not found. This
     * method first uses {@link Class#getConstructor(Class[])}. If not found, it will use
     * {@link Class#getDeclaredConstructor(Class[])} to try again.
     *
     * @param cls            the given class
     * @param parameterTypes the specified parameter types
     * @return the constructor of the specified parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Constructor<?> getConstructor(
        @Nonnull Class<?> cls,
        @Nonnull Class<?> @Nonnull @RetainedParam [] parameterTypes
    ) {
        return getConstructor(cls, parameterTypes, true);
    }

    /**
     * Returns the constructor of the specified parameter types from the given class, or {@code null} if not found. This
     * method first uses {@link Class#getConstructor(Class[])}. If not found and the {@code searchDeclared} is
     * {@code true}, it will use {@link Class#getDeclaredConstructor(Class[])} to try again.
     *
     * @param cls            the given class
     * @param parameterTypes the specified parameter types
     * @param searchDeclared specifies whether searches declared constructors
     * @return the constructor of the specified parameter types from the given class, or {@code null} if not found
     */
    public static @Nullable Constructor<?> getConstructor(
        @Nonnull Class<?> cls,
        @Nonnull Class<?> @Nonnull @RetainedParam [] parameterTypes,
        boolean searchDeclared
    ) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            if (searchDeclared) {
                try {
                    return cls.getDeclaredConstructor(parameterTypes);
                } catch (NoSuchMethodException ex) {
                    return null;
                }
            }
        }
        return null;
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
        @Nullable Class<?> cls = classForName(className, loader);
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
        return Reflect8.arrayClass(componentType);
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
            if (Jie.equals(componentType, void.class)) {
                return null;
            }
            return "[" + JvmKit.getDescriptor(componentType);
        }
        return "[L" + componentType.getName() + ";";
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
        if (Jie.equals(cls, boolean.class)) {
            return Boolean.class;
        }
        if (Jie.equals(cls, byte.class)) {
            return Byte.class;
        }
        if (Jie.equals(cls, short.class)) {
            return Short.class;
        }
        if (Jie.equals(cls, char.class)) {
            return Character.class;
        }
        if (Jie.equals(cls, int.class)) {
            return Integer.class;
        }
        if (Jie.equals(cls, long.class)) {
            return Long.class;
        }
        if (Jie.equals(cls, float.class)) {
            return Float.class;
        }
        if (Jie.equals(cls, double.class)) {
            return Double.class;
        }
        if (Jie.equals(cls, void.class)) {
            return Void.class;
        }
        throw new UnknownPrimitiveTypeException(cls);
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
     * Returns whether the given member is static.
     *
     * @param member the given member
     * @return whether the given member is static
     */
    public static boolean isStatic(@Nonnull Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * Returns whether the given member is public.
     *
     * @param member the given member
     * @return whether the given member is public
     */
    public static boolean isPublic(@Nonnull Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * Returns whether the given member is protected.
     *
     * @param member the given member
     * @return whether the given member is protected
     */
    public static boolean isProtected(@Nonnull Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    /**
     * Returns whether the given member is private.
     *
     * @param member the given member
     * @return whether the given member is private
     */
    public static boolean isPrivate(@Nonnull Member member) {
        return Modifier.isPrivate(member.getModifiers());
    }

    /**
     * Returns whether the given member is package-private (no modifies).
     *
     * @param member the given member
     * @return whether the given member is package-private (no modifies)
     */
    public static boolean isPackagePrivate(@Nonnull Member member) {
        int mod = member.getModifiers();
        return !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod);
    }

    /**
     * Returns whether the given class is static.
     *
     * @param cls the given class
     * @return whether the given class is static
     */
    public static boolean isStatic(@Nonnull Class<?> cls) {
        return Modifier.isStatic(cls.getModifiers());
    }

    /**
     * Returns whether the given class is public.
     *
     * @param cls the given class
     * @return whether the given class is public
     */
    public static boolean isPublic(@Nonnull Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    /**
     * Returns whether the given class is protected.
     *
     * @param cls the given class
     * @return whether the given class is protected
     */
    public static boolean isProtected(@Nonnull Class<?> cls) {
        return Modifier.isProtected(cls.getModifiers());
    }

    /**
     * Returns whether the given class is private.
     *
     * @param cls the given class
     * @return whether the given class is private
     */
    public static boolean isPrivate(@Nonnull Class<?> cls) {
        return Modifier.isPrivate(cls.getModifiers());
    }

    /**
     * Returns whether the given class is package-private (no modifies).
     *
     * @param cls the given class
     * @return whether the given class is package-private (no modifies)
     */
    public static boolean isPackagePrivate(@Nonnull Class<?> cls) {
        int mod = cls.getModifiers();
        return !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && !Modifier.isPrivate(mod);
    }

    /**
     * Returns whether the given member can be overridden (its owner class is no final, and itself is no final, no
     * static and no private).
     *
     * @param member the given member
     * @return whether the given member can be overridden (its owner class is no final, and itself is no final, no
     * static and no private)
     */
    public static boolean isOverridable(@Nonnull Member member) {
        int modifiers = member.getModifiers();
        if (!isOverridable(modifiers)) {
            return false;
        }
        Class<?> declaringClass = member.getDeclaringClass();
        return !Modifier.isFinal(declaringClass.getModifiers());
    }

    /**
     * Returns whether the given class can be overridden (no final, no static and no private).
     *
     * @param cls the given class
     * @return whether the given class can be overridden (no final, no static and no private)
     */
    public static boolean isOverridable(@Nonnull Class<?> cls) {
        int modifiers = cls.getModifiers();
        return isOverridable(modifiers);
    }

    private static boolean isOverridable(int mod) {
        return !Modifier.isFinal(mod) &&
            !Modifier.isStatic(mod) &&
            !Modifier.isPrivate(mod);
    }
}
