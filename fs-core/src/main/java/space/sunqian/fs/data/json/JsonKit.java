package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IORuntimeException;

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

    /**
     * Formats the given JSON data to the given appender by {@link JsonFormatter#defaultFormatter()}.
     *
     * @param data     the given JSON data to be formatted
     * @param appender the appender to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void toJsonString(@Nullable Object data, @Nonnull Appendable appender) throws IORuntimeException {
        JsonFormatter.defaultFormatter().formatTo(data, appender);
    }

    private JsonKit() {
    }
}
