package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

/**
 * Utilities for {@code JSON}.
 *
 * @author sunqian
 */
public class JsonKit {

    /**
     * Returns a string formatted by {@link JsonFormatter#defaultFormatter()} of the given data.
     *
     * @param data the given JSON data
     * @return a string formatted by {@link JsonFormatter#defaultFormatter()} of the given data
     */
    public static @Nonnull String toJsonString(@Nullable Object data) {
        return JsonFormatter.defaultFormatter().format(data);
    }

    private JsonKit() {
    }
}
