package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * This abstract class is used for obtaining runtime {@link Type} instance. The usage as following:
 * <pre>{@code
 * // To obtain a class type: String.class
 * Type classType = new TypeRef<String>(){}.type();
 * // To obtain a parameterized type: List<String>
 * ParameterizedType paramType = new TypeRef<List<String>>(){}.asParameterized();
 * }</pre>
 *
 * @param <T> the runtime type to be obtained
 * @author sunqian
 */
public abstract class TypeRef<T> {

    private final @Nonnull Type type;

    /**
     * Constructs a new instance of {@link TypeRef}.
     */
    protected TypeRef() {
        this.type = resolveActualTypeArgument();
    }

    private @Nonnull Type resolveActualTypeArgument() {
        Type genericSuper = getClass().getGenericSuperclass();
        if (TypeKit.isParameterized(genericSuper)) {
            ParameterizedType parameterizedSuper = (ParameterizedType) genericSuper;
            if (Objects.equals(parameterizedSuper.getRawType(), TypeRef.class)) {
                return parameterizedSuper.getActualTypeArguments()[0];
            }
        }
        List<Type> typeArgs = TypeKit.resolveActualTypeArguments(genericSuper, TypeRef.class);
        return typeArgs.get(0);
    }

    /**
     * Returns the actual runtime type to be obtained
     *
     * @return the actual runtime type to be obtained
     */
    public @Nonnull Type type() {
        return type;
    }

    /**
     * Returns the actual runtime type to be obtained as {@link ParameterizedType}.
     *
     * @return the actual runtime type to be obtained as {@link ParameterizedType}
     */
    public @Nonnull ParameterizedType asParameterized() {
        return (ParameterizedType) type;
    }
}
