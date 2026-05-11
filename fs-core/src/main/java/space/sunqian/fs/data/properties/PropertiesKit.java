package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.DataParsingException;

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;

/**
 * Utilities for {@code Properties}.
 *
 * @author sunqian
 */
public class PropertiesKit {

    /**
     * Loads properties from the given byte array, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param bytes the byte array to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(byte @Nonnull [] bytes) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(bytes);
    }

    /**
     * Loads properties from the given input stream, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param in the input stream to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull InputStream in) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(in);
    }

    /**
     * Loads properties from the given readable byte channel, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param channel the readable byte channel to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull ReadableByteChannel channel) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(channel);
    }

    /**
     * Loads properties from the given char array, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param chars the char array to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(char @Nonnull [] chars) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(chars);
    }

    /**
     * Loads properties from the given char sequence, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param charSequence the char sequence to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull CharSequence charSequence) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(charSequence);
    }

    /**
     * Loads properties from the given reader, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param reader the reader to read from
     * @return the parsed {@link PropertiesData} object
     * @throws DataParsingException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull Reader reader) throws DataParsingException {
        return PropertiesParser.defaultParser().parse(reader);
    }

    private PropertiesKit() {
    }
}
