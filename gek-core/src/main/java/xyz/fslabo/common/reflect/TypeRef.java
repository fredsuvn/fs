package xyz.fslabo.common.reflect;

import xyz.fslabo.common.collect.JieColl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * This abstract class is a utility class used for obtaining runtime {@link Type} instance:
 * <pre>
 *     TypeRef&lt;String&gt; classRef = new TypeRef&lt;String&gt;(){};
 *     Type classType = classRef.getType();
 *     Objects.equals(classType, String.class);//true
 *     TypeRef&lt;List&lt;String&gt;&gt; parameterizedRef = new TypeRef&lt;List&lt;String&gt;&gt;(){};
 *     Type parameterizedType = parameterizedRef.getType();
 *     System.out.println(parameterizedType.getTypeName());//List&lt;String&gt;
 * </pre>
 *
 * @author fredsuvn
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
        List<Type> typeArgs = JieReflect.getActualTypeArguments(genericSuper, TypeRef.class);
        if (JieColl.isEmpty(typeArgs)) {
            throw new IllegalStateException("Not subtype of TypeRef: " + getClass().getName() + "!");
        }
        return typeArgs.get(0);
    }

    /**
     * Returns actual type of this reference.
     *
     * @return actual type of this reference
     */
    public Type getType() {
        return type;
    }
}
