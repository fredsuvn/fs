package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;

/**
 * Represents the {@code null} value in {@code JSON}.
 *
 * @author sunqian
 */
public final class JsonNull {

    /**
     * The singleton instance of {@link JsonNull}.
     */
    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final @Nonnull JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
    }
}
