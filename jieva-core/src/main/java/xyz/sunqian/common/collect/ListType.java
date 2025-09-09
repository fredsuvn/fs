package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class is used to hold the component type for array, iterable, list and other collection types.
 *
 * @author sunqian
 */
public final class ListType {

    /**
     * Returns a new {@link ListType} instance with the specified component type.
     *
     * @param componentType the specified component type
     * @return a new {@link ListType} instance with the specified component type
     */
    public static @Nonnull ListType of(@Nonnull Type componentType) {
        return new ListType(componentType);
    }

    /**
     * Returns a new {@link MapType} instance with the specified parameterized list type.
     *
     * @param listType the specified parameterized map type
     * @return a new {@link MapType} instance with the specified parameterized list type
     */
    public static @Nonnull ListType of(@Nonnull ParameterizedType listType) {
        Type[] actualTypeArguments = listType.getActualTypeArguments();
        return new ListType(actualTypeArguments[0]);
    }

    private final @Nonnull Type componentType;

    private ListType(@Nonnull Type componentType) {
        this.componentType = componentType;
    }

    /**
     * Returns the component type.
     *
     * @return the component type
     */
    public @Nonnull Type componentType() {
        return componentType;
    }
}
