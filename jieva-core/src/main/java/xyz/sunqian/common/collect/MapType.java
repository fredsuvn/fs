package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This class is used to hold the key type and value type for a {@link Map}.
 *
 * @author sunqian
 */
public final class MapType {

    /**
     * Returns a new {@link MapType} instance with the specified key type and value type.
     *
     * @param keyType   the specified key type
     * @param valueType the specified value type
     * @return a new {@link MapType} instance with the specified key type and value type
     */
    public static @Nonnull MapType of(@Nonnull Type keyType, @Nonnull Type valueType) {
        return new MapType(keyType, valueType);
    }

    private final @Nonnull Type keyType;
    private final @Nonnull Type valueType;

    private MapType(@Nonnull Type keyType, @Nonnull Type valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * Returns the key type.
     *
     * @return the key type
     */
    public @Nonnull Type keyType() {
        return keyType;
    }

    /**
     * Returns the value type.
     *
     * @return the value type
     */
    public @Nonnull Type valueType() {
        return valueType;
    }
}
