package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.bytes.BytesBuilder;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.ByteDataFormatter;
import space.sunqian.fs.data.CharDataFormatter;
import space.sunqian.fs.data.DataFormattingException;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Represents JSON data formatter that formats a given JSON data. By default, it uses {@link CharsKit#defaultCharset()}
 * to formats the data in bytes.
 * <p>
 * Note the JsonFormatter does not support {@link JsonData}, using {@link JsonData}'s own methods (such as
 * {@link JsonData#writeTo(Appendable)}) or converting the  {@link JsonData} (such as {@link JsonData#asMap()}) to
 * format.
 *
 * @author sunqian
 */
public interface JsonFormatter extends ByteDataFormatter<Object>, CharDataFormatter<Object> {

    /**
     * Returns the default {@link JsonFormatter}, which uses {@link ObjectSchemaParser#defaultCachedParser()} and
     * {@link ObjectConverter#defaultConverter()} to pars and convert objects, and doesn't ignore the properties of
     * which values are {@code null}, or {@code null} values of map, when formatting.
     * <p>
     * The returned formatter will format {@code byte[]}/{@link ByteBuffer} as Base64 string, and it is the only way to
     * format binary data.
     *
     * @return the default {@link JsonFormatter}
     */
    static @Nonnull JsonFormatter defaultFormatter() {
        return JsonFormatterBack.defaultFormatter();
    }

    /**
     * Returns the default {@link JsonFormatter}, which uses {@link ObjectSchemaParser#defaultCachedParser()} and
     * {@link ObjectConverter#defaultConverter()} to pars and convert objects, and does ignore the properties of which
     * values are {@code null}, or {@code null} values of map, when formatting.
     * <p>
     * The returned formatter will format {@code byte[]}/{@link ByteBuffer} as Base64 string, and it is the only way to
     * format binary data.
     *
     * @return the default {@link JsonFormatter}
     */
    static @Nonnull JsonFormatter newFormatter(boolean ignoreNullValue) {
        return JsonFormatterBack.newFormatter(ignoreNullValue);
    }

    /**
     * Returns a new {@link JsonFormatter} with the specified {@link ObjectSchemaParser} and {@link ObjectConverter} to
     * parse and convert objects.
     * <p>
     * The returned formatter will format {@code byte[]}/{@link ByteBuffer} as Base64 string, and it is the only way to
     * format binary data.
     *
     * @param objectParser    the specified {@link ObjectSchemaParser}
     * @param objectConverter the specified {@link ObjectConverter}
     * @param ignoreNullValue whether to ignore the properties of which values are {@code null}, or {@code null} values
     *                        of map, when formatting
     * @return a new {@link JsonFormatter} with the specified {@link ObjectSchemaParser} and {@link ObjectConverter} to
     * parse and convert objects
     */
    static @Nonnull JsonFormatter newFormatter(
        @Nonnull ObjectSchemaParser objectParser,
        @Nonnull ObjectConverter objectConverter,
        boolean ignoreNullValue
    ) {
        return JsonFormatterBack.newFormatter(objectParser, objectConverter, ignoreNullValue);
    }

    /**
     * Formates and writes the given data as JSON string to the given output stream, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param data the given data to be formatted
     * @param out  the output stream to write to
     * @throws DataFormattingException if any error occurs during formatting
     */
    @Override
    default void formatTo(@Nullable Object data, @Nonnull OutputStream out) throws DataFormattingException {
        formatTo(data, IOKit.newWriter(out));
    }

    /**
     * Formates and writes the given data as JSON string to the given writable byte channel, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param data    the given data to be formatted
     * @param channel the output channel to write to
     * @throws DataFormattingException if any error occurs during formatting
     */
    @SuppressWarnings("DataFlowIssue")
    @Override
    default void formatTo(@Nullable Object data, @Nonnull WritableByteChannel channel) throws DataFormattingException {
        ByteDataFormatter.super.formatTo(data, channel);
    }

    /**
     * Formates and writes the given data as JSON string to the given appender.
     *
     * @param data     the given data to be formatted
     * @param appender the output appender to write to
     * @throws DataFormattingException if any error occurs during formatting
     */
    @Override
    void formatTo(@Nullable Object data, @Nonnull Appendable appender) throws DataFormattingException;

    /**
     * Formates and writes the given data as a JSON string.
     *
     * @param data the given data to be formatted
     * @return the JSON string
     * @throws DataFormattingException if any error occurs during formatting
     */
    @Nonnull
    default String format(@Nullable Object data) throws DataFormattingException {
        StringBuilder sb = new StringBuilder();
        formatTo(data, sb);
        return sb.toString();
    }

    /**
     * Formates and writes the given data as JSON string to a byte array, using {@link CharsKit#defaultCharset()}.
     *
     * @param data the given data to be formatted
     * @return the byte array formatted from the given data
     * @throws DataFormattingException if any error occurs during formatting
     */
    default byte @Nonnull [] formatBytes(@Nullable Object data) throws DataFormattingException {
        BytesBuilder out = new BytesBuilder();
        formatTo(data, out);
        return out.toByteArray();
    }
}
