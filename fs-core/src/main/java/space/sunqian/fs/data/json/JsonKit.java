package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

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
        return JsonFormatter.defaultFormatter().toString(data);
    }

    /**
     * Returns a byte array formatted by {@link JsonFormatter#defaultFormatter()} of the given data.
     *
     * @param data the given JSON data
     * @return a byte array formatted by {@link JsonFormatter#defaultFormatter()} of the given data
     */
    public static byte @Nonnull [] toJsonBytes(@Nullable Object data) {
        return JsonFormatter.defaultFormatter().toByteArray(data);
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

    /**
     * Formats the given JSON data to the given output stream by {@link JsonFormatter#defaultFormatter()}, using
     * {@link CharsKit#defaultCharset()} to encode.
     *
     * @param data the given JSON data to be formatted
     * @param out  the output stream to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void toJsonBytes(@Nullable Object data, @Nonnull OutputStream out) throws IORuntimeException {
        JsonFormatter.defaultFormatter().formatTo(data, out);
    }

    /**
     * Formats the given JSON data to the given writable byte channel by {@link JsonFormatter#defaultFormatter()}, using
     * {@link CharsKit#defaultCharset()} to encode.
     *
     * @param data    the given JSON data to be formatted
     * @param channel the writable byte channel to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void toJsonBytes(@Nullable Object data, @Nonnull WritableByteChannel channel) throws IORuntimeException {
        JsonFormatter.defaultFormatter().formatTo(data, channel);
    }

    private JsonKit() {
    }
}
