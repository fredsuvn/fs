package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;

import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * JSON formatter, which is used to format JSON data.
 *
 * @author sunqian
 */
public interface JsonFormatter {

    /**
     * Returns the default {@link JsonFormatter}.
     *
     * @return the default JSON formatter
     */
    static @Nonnull JsonFormatter defaultFormatter() {
        return JsonBack.defaultFormatter();
    }

    /**
     * Formats the given JSON data to the specified output stream, with the specified property mapper.
     *
     * @param jsonData       the JSON data to be formatted, can be any possible object
     * @param output         the output stream to which the formatted JSON data will be written
     * @param charset        the charset to be used for formatting
     * @param propertyMapper the property mapper to be used for formatting the JSON data
     * @throws JsonDataException if the given JSON data is not a valid JSON
     */
    void format(
        @Nonnull Object jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset,
        @Nonnull JsonPropertyMapper propertyMapper
    ) throws JsonDataException;
}
