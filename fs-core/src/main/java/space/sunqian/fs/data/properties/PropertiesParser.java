package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.ByteDataParser;
import space.sunqian.fs.data.CharDataParser;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

/**
 * Represents the properties data parser that parses properties data a {@code PropertiesData} object.
 *
 * @author sunqian
 */
public interface PropertiesParser extends ByteDataParser<PropertiesData>, CharDataParser<PropertiesData> {

    /**
     * Returns the default {@link PropertiesParser}.
     *
     * @return the default {@link PropertiesParser}
     */
    static @Nonnull PropertiesParser defaultParser() {
        return PropertiesParserImpl.INST;
    }

    /**
     * Wraps the given {@link Properties} object into a {@link PropertiesData} object.
     *
     * @param properties the given {@link Properties} object
     * @return the wrapped {@link PropertiesData} object
     */
    default @Nonnull PropertiesData wrap(@Nonnull Properties properties) {
        return new PropertiesData() {

            @Override
            public @Nonnull Properties asProperties() {
                return properties;
            }

            @Override
            public String toString() {
                return properties.toString();
            }
        };
    }

    /**
     * Parses and returns the properties data from the given input stream to a {@link PropertiesData} object, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param input the given input stream
     * @return the parsed {@link PropertiesData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    @Override
    @Nonnull
    PropertiesData parse(@Nonnull InputStream input) throws IORuntimeException;

    /**
     * Parses and returns the properties data from the given readable byte channel to a {@link PropertiesData} object,
     * using {@link CharsKit#defaultCharset()}.
     *
     * @param channel the given readable byte channel
     * @return the parsed {@link PropertiesData} object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    @Override
    @Nonnull
    PropertiesData parse(@Nonnull ReadableByteChannel channel) throws IORuntimeException;
}
