package xyz.sunqian.common.reflect;

import xyz.sunqian.common.collect.JieCollect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * This abstract class is used for obtaining runtime {@link Type} instance:
 * <pre>{@code
 *     // To obtain a class type: String.class
 *     Type classType = new TypeRef<String>(){}.type(); // String.class
 *
 *     // To obtain a parameterized type: List<String>
 *     ParameterizedType paramType = new TypeRef<List<String>>(){}.asParameterized();// List<String>
 * }</pre>
 *
 * @author sunqian
 */
public abstract class TypeRef<T> {

    private final Type type;

    protected TypeRef() {
        this.type = reflectToActualType();
    }

    private Type reflectToActualType() {
        Type genericSuper = getClass().getGenericSuperclass();
        if (genericSuper instanceof ParameterizedType) {
            ParameterizedType parameterizedSuper = (ParameterizedType) genericSuper;
            if (Objects.equals(parameterizedSuper.getRawType(), TypeRef.class)) {
                return parameterizedSuper.getActualTypeArguments()[0];
            }
        }
        List<Type> typeArgs = JieReflect.resolveActualTypeArguments(genericSuper, TypeRef.class);
        return get0(typeArgs);
    }

    private Type get0(List<Type> typeArgs) {
        if (JieCollect.isEmpty(typeArgs)) {
            throw new ReflectionException("Failed to get actual type of current TypeRef: " + getClass() + ".");
        }
        return typeArgs.get(0);
    }

    /**
     * Returns actual type of this reference.
     *
     * @return actual type of this reference
     */
    public Type type() {
        return type;
    }

    /**
     * Returns actual type of this reference as {@link ParameterizedType}.
     *
     * @return actual type of this reference as {@link ParameterizedType}
     */
    public ParameterizedType asParameterized() {
        return (ParameterizedType) type;
    }
}
