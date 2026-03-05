package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.ByteDataFormatter;
import space.sunqian.fs.data.CharDataFormatter;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Represents JSON data formatter that formats a given JSON data. By default, it uses {@link CharsKit#defaultCharset()}
 * to formats the data in bytes.
 *
 * @author sunqian
 */
public interface JsonFormatter extends ByteDataFormatter<Object>, CharDataFormatter<Object> {

    /**
     * Returns the default formatter of JSON data, which uses {@link ObjectSchemaParser#defaultCachedParser()} and
     * {@link ObjectConverter#defaultConverter()} to pars and convert objects, and doesn't ignore the properties of
     * which values are {@code null} , or {@code null} values of map, when formatting.
     * <p>
     * The returned formatter will format {@code byte[]}/{@link ByteBuffer} as Base64 string, and it is the only way to
     * format binary data.
     *
     * @return the default formatter of JSON data
     */
    static @Nonnull JsonFormatter defaultFormatter() {
        return JsonFormatterBack.defaultFormatter();
    }

    /**
     * Returns a new JSON formatter with the specified {@link ObjectSchemaParser} and {@link ObjectConverter} to parse
     * and convert objects.
     * <p>
     * The returned formatter will format {@code byte[]}/{@link ByteBuffer} as Base64 string, and it is the only way to
     * format binary data.
     *
     * @param objectParser    the specified {@link ObjectSchemaParser}
     * @param objectConverter the specified {@link ObjectConverter}
     * @param ignoreNullValue whether to ignore the properties of which values are {@code null}, or {@code null} values
     *                        of map, when formatting
     * @return a new JSON formatter with the specified {@link ObjectSchemaParser} and {@link ObjectConverter} to parse
     * and convert objects
     */
    static @Nonnull JsonFormatter newFormatter(
        @Nonnull ObjectSchemaParser objectParser,
        @Nonnull ObjectConverter objectConverter,
        boolean ignoreNullValue
    ) {
        return JsonFormatterBack.newFormatter(objectParser, objectConverter, ignoreNullValue);
    }

    /**
     * Formats the given JSON data to the given appender.
     *
     * @param data     the given JSON data to be formatted
     * @param appender the appender to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    void formatTo(@Nullable Object data, @Nonnull Appendable appender) throws IORuntimeException;

    /**
     * Formats the given JSON data to a string.
     *
     * @param data the given data to be formatted
     * @return the formatting string
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    @Nonnull
    default String toString(@Nullable Object data) throws IORuntimeException {
        return CharDataFormatter.super.toString(Fs.asNonnull(data));
    }

    /**
     * Formats the given JSON data to the given output stream.
     *
     * @param data the given data to be formatted
     * @param out  the output stream to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    void formatTo(@Nullable Object data, @Nonnull OutputStream out) throws IORuntimeException;

    /**
     * Formats the given JSON data to the given writable byte channel.
     *
     * @param data    the given data to be formatted
     * @param channel the writable byte channel to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    default void formatTo(@Nullable Object data, @Nonnull WritableByteChannel channel) throws IORuntimeException {
        ByteDataFormatter.super.formatTo(Fs.asNonnull(data), channel);
    }

    /**
     * Formats the given JSON data to a byte array.
     *
     * @param data the given data to be formatted
     * @return the formatting byte array
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    default byte @Nonnull [] toByteArray(@Nullable Object data) throws IORuntimeException {
        return ByteDataFormatter.super.toByteArray(Fs.asNonnull(data));
    }
}
