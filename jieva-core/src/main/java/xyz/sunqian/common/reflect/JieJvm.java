package xyz.sunqian.common.reflect;

import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.collect.JieArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * This is a static utilities class provides utilities for {@code JVM}.
 *
 * @author fredsuvn
 */
public class JieJvm {

    private static final Map<Class<?>, String> DESCRIPTORS = Jie.map(
        boolean.class, "Z",
        byte.class, "B",
        short.class, "S",
        char.class, "C",
        int.class, "I",
        long.class, "J",
        float.class, "F",
        double.class, "D",
        void.class, "V"
    );

    private static final Map<Class<?>, String> INTERNAL_NAMES = Jie.map(
        Object.class, "java/lang/Object",
        String.class, "java/lang/String",
        Boolean.class, "java/lang/Boolean",
        Byte.class, "java/lang/Byte",
        Short.class, "java/lang/Short",
        Character.class, "java/lang/Character",
        Integer.class, "java/lang/Integer",
        Long.class, "java/lang/Long",
        Float.class, "java/lang/Float",
        Double.class, "java/lang/Double",
        Void.class, "java/lang/Void"
    );

    /**
     * Returns the internal name of the given class.
     * <p>
     * For object types, the internal name is its fully qualified name (as returned by Class. getName(), where '.' are
     * replaced by '/'); For primitive type, this method returns its descriptor ({@link #getDescriptor(Class)}).
     *
     * @param cls given class
     * @return internal name of the given class
     */
    public static String getInternalName(Class<?> cls) {
        String result = INTERNAL_NAMES.get(cls);
        return result != null ? result : JieString.replace(cls.getName(), ".", "/");
    }

    /**
     * Returns JVM descriptor of the given class.
     *
     * @param cls given class
     * @return JVM descriptor of the given class
     */
    public static String getDescriptor(Class<?> cls) {
        if (cls.isArray()) {
            return "[" + getDescriptor(cls.getComponentType());
        }
        if (!cls.isPrimitive()) {
            return "L" + getInternalName(cls) + ";";
        }
        return getPrimitiveDescriptor(cls);
    }

    private static String getPrimitiveDescriptor(Class<?> cls) {
        String result = DESCRIPTORS.get(cls);
        if (result != null) {
            return result;
        }
        throw new NotPrimitiveException(cls);
    }

    /**
     * Returns JVM descriptor of the given method.
     *
     * @param method given method
     * @return JVM descriptor of the given method
     */
    public static String getDescriptor(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Objects.equals(returnType, void.class) && method.getParameterCount() == 0) {
            return "()V";
        }
        Class<?>[] params = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Class<?> param : params) {
            sb.append(getDescriptor(param));
        }
        sb.append(")");
        sb.append(getDescriptor(returnType));
        return sb.toString();
    }

    /**
     * Returns JVM descriptor of the given constructor.
     *
     * @param constructor given constructor
     * @return JVM descriptor of the given constructor
     */
    public static String getDescriptor(Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {
            return "()V";
        }
        Class<?>[] params = constructor.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Class<?> param : params) {
            sb.append(getDescriptor(param));
        }
        sb.append(")V");
        return sb.toString();
    }

    /**
     * Returns JVM signature of the given type.
     *
     * @param type given type
     * @return JVM signature of the given type
     */
    public static String getSignature(Type type) {
        if (type instanceof Class<?>) {
            return getDescriptor((Class<?>) type);
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
