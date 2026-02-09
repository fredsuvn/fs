package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;

/**
 * Represents the {@code null} value in {@code JSON}.
 *
 * @author sunqian
 */
public final class JsonNull {

    @SuppressWarnings("InstantiationOfUtilityClass")
    private static final @Nonnull JsonNull INSTANCE = new JsonNull();

    /**
     * Returns the singleton instance of {@link JsonNull}.
     *
     * @return the singleton instance of {@link JsonNull}
     */
    public static @Nonnull JsonNull getInstance() {
        return INSTANCE;
    }

    private JsonNull() {
    }
}
