package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.ByteDataParser;
import space.sunqian.fs.data.CharDataParser;

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;

/**
 * Represents the JSON data parser that parses JSON data a {@code JsonData} object.
 *
 * @author sunqian
 */
public interface JsonParser extends ByteDataParser<JsonData>, CharDataParser<JsonData> {

    /**
     * Returns the default {@link JsonParser}.
     *
     * @return the default {@link JsonParser}
     */
    static @Nonnull JsonParser defaultParser() {
        return JsonParserImpl.INST;
    }

    /**
     * Parses and returns the JSON data from the given byte array to a {@link JsonData} object, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param bytes the given byte array
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(byte @Nonnull [] bytes) throws JsonDataParsingException;

    /**
     * Parses and returns the JSON data from the given input stream to a {@link JsonData} object, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param input the given input stream
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(@Nonnull InputStream input) throws JsonDataParsingException;

    /**
     * Parses and returns the JSON data from the given readable byte channel to a {@link JsonData} object, using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param channel the given readable byte channel
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(@Nonnull ReadableByteChannel channel) throws JsonDataParsingException;

    /**
     * Parses and returns the JSON data from the given char array to a {@link JsonData} object.
     *
     * @param chars the given char array
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(char @Nonnull [] chars) throws JsonDataParsingException;

    /**
     * Parses and returns the JSON data from the given char sequence to a {@link JsonData} object.
     *
     * @param charSequence the given char sequence
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(@Nonnull CharSequence charSequence) throws JsonDataParsingException;

    /**
     * Parses and returns the JSON data from the given reader to a {@link JsonData} object.
     *
     * @param reader the given reader
     * @return the parsed {@link JsonData} object
     * @throws JsonDataParsingException if any error occurs during parsing
     */
    @Override
    @Nonnull
    JsonData parse(@Nonnull Reader reader) throws JsonDataParsingException;
}
