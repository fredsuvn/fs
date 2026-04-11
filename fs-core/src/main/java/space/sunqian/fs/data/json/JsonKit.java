package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
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
        return JsonFormatter.defaultFormatter().format(data);
    }

    /**
     * Returns a byte array formatted by {@link JsonFormatter#defaultFormatter()} of the given data.
     *
     * @param data the given JSON data
     * @return a byte array formatted by {@link JsonFormatter#defaultFormatter()} of the given data
     */
    public static byte @Nonnull [] toJsonBytes(@Nullable Object data) {
        return JsonFormatter.defaultFormatter().formatBytes(data);
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

    /**
     * Parses and returns the JSON data from the given input stream to a {@link JsonData} object by
     * {@link JsonParser#defaultParser()}, using {@link CharsKit#defaultCharset()}.
     *
     * @param input the given input stream
     * @return the parsed {@link JsonData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    public static @Nonnull JsonData parse(@Nonnull InputStream input) throws IORuntimeException {
        return JsonParser.defaultParser().parse(input);
    }

    /**
     * Parses and returns the JSON data from the given readable byte channel to a {@link JsonData} object by
     * {@link JsonParser#defaultParser()}, using {@link CharsKit#defaultCharset()}.
     *
     * @param channel the given readable byte channel
     * @return the parsed {@link JsonData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    public static @Nonnull
    JsonData parse(@Nonnull ReadableByteChannel channel) throws IORuntimeException {
        return JsonParser.defaultParser().parse(channel);
    }

    /**
     * Parses and returns the JSON data from the given reader to a {@link JsonData} object by
     * {@link JsonParser#defaultParser()}.
     *
     * @param reader the given reader
     * @return the parsed {@link JsonData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    public static @Nonnull JsonData parse(@Nonnull Reader reader) throws IORuntimeException {
        return JsonParser.defaultParser().parse(reader);
    }

    /**
     * Parses and returns the JSON data from the given JSON string to a {@link JsonData} object by
     * {@link JsonParser#defaultParser()}.
     *
     * @param str the given JSON string
     * @return the parsed {@link JsonData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    public static @Nonnull JsonData parse(@Nonnull String str) throws IORuntimeException {
        return JsonParser.defaultParser().parse(str);
    }

    private JsonKit() {
    }
}
