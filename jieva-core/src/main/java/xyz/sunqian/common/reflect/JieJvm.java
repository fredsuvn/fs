package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Static utility class for {@code JVM}.
 *
 * @author sunqian
 */
public class JieJvm {

    /**
     * Returns the internal name of the given class. The internal name of a class is its fully qualified name, as
     * returned by {@link Class#getName()}, where '.' are replaced by '/'.
     *
     * @param cls the given class
     * @return the internal name of the given class.
     */
    public static @Nonnull String getInternalName(@Nonnull Class<?> cls) {
        return cls.getName().replace('.', '/');
    }

    /**
     * Returns the descriptor of the given type. This method supports {@link Class}, {@link ParameterizedType},
     * {@link GenericArrayType} and {@link TypeVariable}.
     *
     * @param type the given type
     * @return the descriptor of the given type
     * @throws JvmException if any problem occurs
     */
    public static @Nonnull String getDescriptor(@Nonnull Type type) throws JvmException {
        if (JieReflect.isTypeVariable(type)) {
            TypeVariable<?> tv = (TypeVariable<?>) type;
            Type bound = JieReflect.getFirstBound(tv);
            return getDescriptor(bound);
        }
        StringBuilder appender = new StringBuilder();
        if (JieReflect.isClass(type)) {
            appendDescriptor((Class<?>) type, appender);
        } else if (JieReflect.isParameterized(type)) {
            Class<?> rawClass = getRawClass((ParameterizedType) type);
            appendDescriptor(rawClass, appender);
        } else if (JieReflect.isGenericArray(type)) {
            appendDescriptor(getRawClass((GenericArrayType) type), appender);
        } else {
            throw new JvmException("Unknown type: " + type + ".");
        }
        return appender.toString();
    }

    /**
     * Returns the descriptor of the given method.
     *
     * @param method the given method
     * @return the descriptor of the given method
     */
    public static @Nonnull String getDescriptor(@Nonnull Method method) {
        Class<?> returnType = method.getReturnType();
        Class<?>[] parameters = method.getParameterTypes();
        if (Objects.equals(returnType, void.class) && parameters.length == 0) {
            return "()V";
        }
        StringBuilder appender = new StringBuilder();
        appender.append("(");
        for (Class<?> parameter : parameters) {
            appendDescriptor(parameter, appender);
        }
        appender.append(")");
        appendDescriptor(returnType, appender);
        return appender.toString();
    }

    /**
     * Returns the descriptor of the given constructor.
     *
     * @param constructor the given constructor
     * @return the descriptor of the given constructor
     */
    public static @Nonnull String getDescriptor(@Nonnull Constructor<?> constructor) {
        Class<?>[] parameters = constructor.getParameterTypes();
        if (parameters.length == 0) {
            return "()V";
        }
        StringBuilder appender = new StringBuilder();
        appender.append("(");
        for (Class<?> parameter : parameters) {
            appendDescriptor(parameter, appender);
        }
        appender.append(")V");
        return appender.toString();
    }

    private static void appendDescriptor(@Nonnull Class<?> cls, @Nonnull StringBuilder appender) {
        Class<?> curCls = cls;
        while (curCls.isArray()) {
            appender.append('[');
            curCls = curCls.getComponentType();
        }
        if (curCls.isPrimitive()) {
            appender.append(getPrimitiveDescriptor(curCls));
        } else {
            appender.append('L').append(getInternalName(curCls)).append(';');
        }
    }

    private static char getPrimitiveDescriptor(@Nonnull Class<?> cls) {
        if (Objects.equals(cls, boolean.class)) {
            return 'Z';
        }
        if (Objects.equals(cls, byte.class)) {
            return 'B';
        }
        if (Objects.equals(cls, short.class)) {
            return 'S';
        }
        if (Objects.equals(cls, char.class)) {
            return 'C';
        }
        if (Objects.equals(cls, int.class)) {
            return 'I';
        }
        if (Objects.equals(cls, long.class)) {
            return 'J';
        }
        if (Objects.equals(cls, float.class)) {
            return 'F';
        }
        if (Objects.equals(cls, double.class)) {
            return 'D';
        }
        if (Objects.equals(cls, void.class)) {
            return 'V';
        }
        throw new UnknownPrimitiveTypeException(cls);
    }

    /**
     * Returns whether the given type need a signature for JVM.
     * <p>
     * The {@code declaration} specifies where the signature is used for. When for the declaration, {@code true} should
     * be passed here. Otherwise, such as signature of a {@link Field}'s type, {@code false}.
     *
     * @param type        the given type
     * @param declaration whether the type is declaring
     * @return whether the given type need a signature for JVM
     */
    public static boolean needSignature(@Nonnull Type type, boolean declaration) {
        if (!declaration) {
            return !JieReflect.isClass(type);
        }
        if (!JieReflect.isClass(type)) {
            return true;
        }
        Class<?> cls = (Class<?>) type;
        TypeVariable<?>[] tv = cls.getTypeParameters();
        if (tv.length > 0) {
            return true;
        }
        @Nullable Type superclass = cls.getGenericSuperclass();
        if (superclass != null && !JieReflect.isClass(superclass)) {
            return true;
        }
        Type[] interfaces = cls.getGenericInterfaces();
        for (Type anInterface : interfaces) {
            if (!JieReflect.isClass(anInterface)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given method need a signature for JVM.
     *
     * @param method the given method
     * @return whether the given method need a signature for JVM
     */
    public static boolean needSignature(@Nonnull Method method) {
        Type returnType = method.getGenericReturnType();
        if (needSignature(returnType, false)) {
            return true;
        }
        return needSignature((Executable) method);
    }

    /**
     * Returns whether the given constructor need a signature for JVM.
     *
     * @param constructor the given constructor
     * @return whether the given constructor need a signature for JVM
     */
    public static boolean needSignature(@Nonnull Constructor<?> constructor) {
        return needSignature((Executable) constructor);
    }

    private static boolean needSignature(@Nonnull Executable executable) {
        Type[] parameters = executable.getGenericParameterTypes();
        for (Type parameter : parameters) {
            if (needSignature(parameter, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the signature of the given type.
     * <p>
     * The {@code declaration} specifies where the signature is used for. When for the declaration, {@code true} should
     * be passed here. Otherwise, such as signature of a {@link Field}'s type, {@code false}.
     *
     * @param type the given type
     * @return the signature of the given type
     */
    public static @Nullable String getSignature(@Nonnull Type type, boolean declaration) {
        if (!needSignature(type, declaration)) {
            return null;
        }
        StringBuilder appender = new StringBuilder();
        if (JieReflect.isClass(type)) {
            Class<?> cls = (Class<?>) type;
            appendSignature(cls.getTypeParameters(), appender);
            @Nullable Type superclass = cls.getGenericSuperclass();
            if (superclass == null) {
                appender.append("Ljava/lang/Object;");
            } else {
                appendSignature(superclass, appender);
            }
            Type[] interfaces = cls.getGenericInterfaces();
            for (Type anInterface : interfaces) {
                appendSignature(anInterface, appender);
            }
        } else {
            appendSignature(type, appender);
        }
        return appender.toString();
    }

    /**
     * Returns the given type's signature used for the declaration. This method is equivalent to
     * ({@link #getSignature(Type, boolean)}): {@code getSignature(type, true)}.
     *
     * @param type the given type
     * @return the given type's signature used for the declaration
     */
    public static @Nullable String getSignature(@Nonnull Type type) {
        return getSignature(type, true);
    }

    /**
     * Returns the signature of the given field.
     *
     * @param field the given field
     * @return the signature of the given field
     */
    public static @Nullable String getSignature(@Nonnull Field field) {
        return getSignature(field.getGenericType(), false);
    }

    /**
     * Returns the signature of the given method.
     *
     * @param method the given method
     * @return the signature of the given method
     */
    public static @Nullable String getSignature(@Nonnull Method method) {
        if (!needSignature(method)) {
            return null;
        }
        StringBuilder appender = new StringBuilder();
        appendSignature(method, appender);
        Type returnType = method.getGenericReturnType();
        appendSignature(returnType, appender);
        return appender.toString();
    }

    /**
     * Returns the signature of the given constructor.
     *
     * @param constructor the given constructor
     * @return the signature of the given constructor
     */
    public static @Nullable String getSignature(@Nonnull Constructor<?> constructor) {
        if (!needSignature(constructor)) {
            return null;
        }
        StringBuilder appender = new StringBuilder();
        appendSignature(constructor, appender);
        appender.append('V');
        return appender.toString();
    }

    private static void appendSignature(
        @Nonnull Executable executable,
        @Nonnull StringBuilder appender
    ) {
        appendSignature(executable.getTypeParameters(), appender);
        appender.append('(');
        for (Type parameter : executable.getGenericParameterTypes()) {
            appendSignature(parameter, appender);
        }
        appender.append(')');
    }

    private static void appendSignature(
        @Nonnull TypeVariable<?> @Nonnull [] typeVariables,
        @Nonnull StringBuilder appender
    ) {
        if (typeVariables.length > 0) {
            appender.append('<');
            for (TypeVariable<?> tv : typeVariables) {
                appendTypeVariableDeclaringSignature(tv, appender);
            }
            appender.append('>');
        }
    }

    private static void appendTypeVariableDeclaringSignature(
        @Nonnull TypeVariable<?> type,
        @Nonnull StringBuilder appender
    ) {
        appender.append(type.getName());
        Type[] bounds = type.getBounds();
        for (int i = 0; i < bounds.length; i++) {
            Type bound = bounds[i];
            appender.append(':');
            if (JieReflect.isTypeVariable(bound)) {
                appender.append('T');
                appender.append(((TypeVariable<?>) bound).getName());
                appender.append(';');
                continue;
            }
            if (i == 0) {
                if (JieReflect.isParameterized(bound)) {
                    Class<?> rawClass = getRawClass((ParameterizedType) bound);
                    if (rawClass.isInterface()) {
                        appender.append(':');
                    }
                }
                if (JieReflect.isClass(bound)) {
                    Class<?> boundClass = Jie.as(bound);
                    if (boundClass.isInterface()) {
                        appender.append(':');
                    }
                }
            }
            appendSignature(bound, appender);
        }
    }

    private static void appendSignature(@Nonnull Type type, @Nonnull StringBuilder appender) {
        if (JieReflect.isClass(type)) {
            appendSignature((Class<?>) type, appender);
            return;
        }
        if (JieReflect.isParameterized(type)) {
            appendSignature((ParameterizedType) type, appender);
            return;
        }
        if (JieReflect.isWildcard(type)) {
            appendSignature((WildcardType) type, appender);
            return;
        }
        if (JieReflect.isTypeVariable(type)) {
            appendSignature((TypeVariable<?>) type, appender);
            return;
        }
        if (JieReflect.isGenericArray(type)) {
            appendSignature((GenericArrayType) type, appender);
            return;
        }
        throw new JvmException("Unknown type: " + type + ".");
    }

    private static void appendSignature(@Nonnull Class<?> type, @Nonnull StringBuilder appender) {
        appender.append(getDescriptor(type));
    }

    private static void appendSignature(@Nonnull ParameterizedType type, @Nonnull StringBuilder appender) {
        Type owner = type.getOwnerType();
        Class<?> rawClass = getRawClass(type);
        if (owner != null) {
            appendSignature(owner, appender);
            // it must end with a ';'
            int semicolonIndex = appender.length() - 1;
            if (JieReflect.isClass(owner)) {
                appender.setCharAt(semicolonIndex, '$');
            } else {
                appender.setCharAt(semicolonIndex, '.');
            }
            appender.append(rawClass.getSimpleName());
        } else {
            // no primitive
            appender.append('L');
            appender.append(getInternalName(rawClass));
        }
        appender.append('<');
        for (Type actualTypeArgument : type.getActualTypeArguments()) {
            appendSignature(actualTypeArgument, appender);
        }
        appender.append(">;");
    }

    private static void appendSignature(@Nonnull WildcardType type, @Nonnull StringBuilder appender) {
        @Nullable Type lower = JieReflect.getLowerBound(type);
        Type[] bounds;
        if (lower != null) {
            // ? super
            appender.append('-');
            bounds = type.getLowerBounds();
        } else {
            // '?' and '? extends' are equivalent
            appender.append('+');
            bounds = type.getUpperBounds();
        }
        for (Type bound : bounds) {
            appendSignature(bound, appender);
        }
    }

    private static void appendSignature(@Nonnull TypeVariable<?> type, @Nonnull StringBuilder appender) {
        appender.append('T').append(type.getTypeName()).append(';');
    }

    private static void appendSignature(@Nonnull GenericArrayType type, @Nonnull StringBuilder appender) {
        @Nonnull Type curType = type;
        while (JieReflect.isArray(curType)) {
            appender.append('[');
            // never null
            curType = Objects.requireNonNull(JieReflect.getComponentType(curType));
        }
        appendSignature(curType, appender);
    }

    private static @Nonnull Class<?> getRawClass(@Nonnull ParameterizedType type) throws JvmException {
        Type rawType = type.getRawType();
        if (JieReflect.isClass(rawType)) {
            return (Class<?>) rawType;
        }
        throw new JvmException("Unknown raw type: " + rawType + ".");
    }

    private static @Nonnull Class<?> getRawClass(@Nonnull GenericArrayType type) throws JvmException {
        @Nullable Class<?> arrayClass = JieReflect.toRuntimeClass(type);
        if (arrayClass != null) {
            return arrayClass;
        }
        throw new JvmException("Unknown array type: " + type + ".");
    }

    /**
     * Loads and returns a class from the specified byte data.
     *
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if any problem occurs
     */
    public static @Nonnull Class<?> loadClass(@Nonnull byte @Nonnull [] bytes) throws JvmException {
        return ClassLoaderHolder.INSTANCE.load(bytes);
    }

    /**
     * Loads and returns a class from the specified byte data.
     *
     * @param bytes the specified byte data
     * @return the {@link Class} instance loaded from the specified byte data
     * @throws JvmException if any problem occurs
     */
    public static @Nonnull Class<?> loadClass(@Nonnull ByteBuffer bytes) throws JvmException {
        return ClassLoaderHolder.INSTANCE.load(bytes);
    }

    private static final class ClassLoaderHolder {
        private static final BytesClassLoader INSTANCE = new BytesClassLoader();
    }
}
