package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.collect.JieArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * This is a static utilities class provides utilities for {@code JVM}.
 *
 * @author fredsuvn
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
     * Returns the descriptor of the given class.
     *
     * @param cls the given class
     * @return the descriptor of the given class
     */
    public static @Nonnull String getDescriptor(@Nonnull Class<?> cls) {
        StringBuilder appender = new StringBuilder();
        appendDescriptor(cls, appender);
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
     * Returns the signature of the given type.
     *
     * @param type the given type
     * @return the signature of the given type
     */
    public static @Nonnull String getSignature(@Nonnull Type type) {
        if (type instanceof Class<?>) {

        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder();
            sb.append("L");
            sb.append(getInternalName((Class<?>) pt.getRawType()));
            sb.append("<");
            for (Type actualTypeArgument : pt.getActualTypeArguments()) {
                sb.append(getSignature(actualTypeArgument));
            }
            sb.append(">;");
            return sb.toString();
        }
        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            Type lower = JieReflect.getLowerBound(wt);
            if (lower != null) {
                return "-" + getSignature(lower);
            }
            return "+" + getSignature(JieReflect.getUpperBound(wt));
        }
        if (type instanceof TypeVariable<?>) {
            TypeVariable<?> tv = (TypeVariable<?>) type;
            return "T" + tv.getTypeName() + ";";
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType at = (GenericArrayType) type;
            return "[" + getSignature(at.getGenericComponentType());
        }
        throw new IllegalArgumentException("Unknown type: " + type.getTypeName());
    }

    // private static String getSignature(Class<?> type) {
    //     TypeVariable<?>[] tv = type.getTypeParameters();
    //
    // }

    /**
     * Returns JVM signature of the given method.
     *
     * @param method given method
     * @return JVM signature of the given method
     */
    public static String getSignature(Method method) {
        Type returnType = method.getGenericReturnType();
        if (Objects.equals(returnType, void.class) && method.getParameterCount() == 0) {
            return "()V";
        }
        Type[] params = method.getGenericParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type param : params) {
            sb.append(getSignature(param));
        }
        sb.append(")");
        sb.append(getSignature(returnType));
        return sb.toString();
    }

    /**
     * Returns JVM signature of the given constructor.
     *
     * @param constructor given constructor
     * @return JVM signature of the given constructor
     */
    public static String getSignature(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return "()V";
        }
        Type[] params = constructor.getGenericParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type param : params) {
            sb.append(getSignature(param));
        }
        sb.append(")V");
        return sb.toString();
    }

    /**
     * Returns JVM signature for declaration of a class or interface.
     *
     * @param cls a class or interface to declare
     * @return JVM signature for declaration of a class or interface
     */
    public static String declareSignature(Class<?> cls) {
        StringBuilder sb = new StringBuilder();
        TypeVariable<?>[] tvs = cls.getTypeParameters();
        if (JieArray.isNotEmpty(tvs)) {
            sb.append("<");
            for (TypeVariable<?> tv : tvs) {
                sb.append(tv.getTypeName());
                Type[] bounds = tv.getBounds();
                for (int i = 0; i < bounds.length; i++) {
                    Type bound = bounds[i];
                    sb.append(declareSignature(i, bound));
                }
            }
            sb.append(">");
        }
        Type superClass = cls.getGenericSuperclass();
        sb.append(getSignature(superClass));
        Type[] interfaces = cls.getGenericInterfaces();
        if (JieArray.isNotEmpty(interfaces)) {
            for (Type anInterface : interfaces) {
                sb.append(getSignature(anInterface));
            }
        }
        return sb.toString();
    }

    private static String declareSignature(int index, Type bound) {
        if (index > 0) {
            return ":" + getSignature(bound);
        }
        if (bound instanceof Class<?>) {
            if (((Class<?>) bound).isInterface()) {
                return "::" + getSignature(bound);
            }
        }
        if (bound instanceof ParameterizedType) {
            Class<?> rawType = (Class<?>) ((ParameterizedType) bound).getRawType();
            if (rawType.isInterface()) {
                return "::" + getSignature(bound);
            }
        }
        return ":" + getSignature(bound);
    }

    /**
     * Loads given bytecode to {@link Class}.
     *
     * @param bytecode given bytecode
     * @return class loaded from given bytecode
     * @throws JvmException if any loading problem occurs
     */
    public static Class<?> loadBytecode(byte[] bytecode) throws JvmException {
        return loadBytecode(ByteBuffer.wrap(bytecode));
    }

    /**
     * Loads given bytecode to {@link Class}.
     *
     * @param bytecode given bytecode
     * @return class loaded from given bytecode
     * @throws JvmException if any loading problem occurs
     */
    public static Class<?> loadBytecode(ByteBuffer bytecode) throws JvmException {
        return JieClassLoader.SINGLETON.load(bytecode);
    }

    private static final class JieClassLoader extends ClassLoader {

        private static final JieClassLoader SINGLETON = new JieClassLoader();

        private JieClassLoader() {
        }

        public Class<?> load(ByteBuffer buffer) throws JvmException {
            try {
                return defineClass(null, buffer, null);
            } catch (ClassFormatError | Exception e) {
                throw new JvmException(e);
            }
        }
    }
}
