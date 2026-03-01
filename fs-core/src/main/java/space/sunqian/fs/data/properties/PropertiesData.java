package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.string.StringKit;
import space.sunqian.fs.data.ByteData;
import space.sunqian.fs.data.CharData;
import space.sunqian.fs.data.DataMap;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.Properties;

/**
 * The interface to access properties data.
 *
 * @author sunqian
 */
public interface PropertiesData extends ByteData, CharData {

    /**
     * Loads properties from the given input stream, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param in the input stream to read from
     * @return a new {@link PropertiesData} wraps the loaded properties
     * @throws IORuntimeException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull InputStream in) throws IORuntimeException {
        return PropertiesParser.defaultParser().parse(in);
    }

    /**
     * Loads properties from the given readable byte channel, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param channel the readable byte channel to read from
     * @return a new {@link PropertiesData} wraps the loaded properties
     * @throws IORuntimeException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull ReadableByteChannel channel) throws IORuntimeException {
        return PropertiesParser.defaultParser().parse(channel);
    }

    /**
     * Loads properties from the given reader, using {@link CharsKit#defaultCharset()}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param reader the reader to read from
     * @return a new {@link PropertiesData} wraps the loaded properties
     * @throws IORuntimeException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull Reader reader) throws IORuntimeException {
        return PropertiesParser.defaultParser().parse(reader);
    }

    /**
     * Returns a new {@link PropertiesData} wraps the given {@link Properties}.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to wrap the properties.
     *
     * @param properties the {@link Properties} to wrap
     * @return a new {@link PropertiesData} wraps the given {@link Properties}
     */
    static @Nonnull PropertiesData wrap(@Nonnull Properties properties) {
        return PropertiesParser.defaultParser().wrap(properties);
    }

    /**
     * Gets the string value of the property, or {@code null} if not found.
     *
     * @param name the name of the property
     * @return the string value of the property, or {@code null} if not found
     */
    default @Nullable String getString(@Nonnull String name) {
        Object value = asMap().get(name);
        return value == null ? null : value.toString();
    }

    /**
     * Gets the string value of the property, or {@code defaultValue} if not found.
     *
     * @param name         the name of the property
     * @param defaultValue the default value
     * @return the string value of the property, or {@code defaultValue} if not found
     */
    default String getString(@Nonnull String name, String defaultValue) {
        String value = getString(name);
        return value == null ? defaultValue : value;
    }

    /**
     * Gets the int value of the property, or {@code 0} if not found.
     *
     * @param name the name of the property
     * @return the int value of the property, or {@code 0} if not found
     */
    default int getInt(@Nonnull String name) {
        return getInt(name, 0);
    }

    /**
     * Gets the int value of the property, or {@code defaultValue} if not found.
     *
     * @param name         the name of the property
     * @param defaultValue the default value
     * @return the int value of the property, or {@code defaultValue} if not found
     */
    default int getInt(@Nonnull String name, int defaultValue) {
        String value = getString(name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    /**
     * Gets the long value of the property, or {@code 0} if not found.
     *
     * @param name the name of the property
     * @return the long value of the property, or {@code 0} if not found
     */
    default long getLong(@Nonnull String name) {
        return getLong(name, 0);
    }

    /**
     * Gets the long value of the property, or {@code defaultValue} if not found.
     *
     * @param name         the name of the property
     * @param defaultValue the default value
     * @return the long value of the property, or {@code defaultValue} if not found
     */
    default long getLong(@Nonnull String name, long defaultValue) {
        String value = getString(name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    /**
     * Gets the float value of the property, or {@code 0.0f} if not found.
     *
     * @param name the name of the property
     * @return the float value of the property, or {@code 0.0f} if not found
     */
    default float getFloat(@Nonnull String name) {
        return getFloat(name, 0.0f);
    }

    /**
     * Gets the float value of the property, or {@code defaultValue} if not found.
     *
     * @param name         the name of the property
     * @param defaultValue the default value
     * @return the float value of the property, or {@code defaultValue} if not found
     */
    default float getFloat(@Nonnull String name, float defaultValue) {
        String value = getString(name);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    /**
     * Gets the double value of the property, or {@code 0.0} if not found.
     *
     * @param name the name of the property
     * @return the double value of the property, or {@code 0.0} if not found
     */
    default double getDouble(@Nonnull String name) {
        return getDouble(name, 0.0);
    }

    /**
     * Gets the double value of the property, or {@code defaultValue} if not found.
     *
     * @param name         the name of the property
     * @param defaultValue the default value
     * @return the double value of the property, or {@code defaultValue} if not found
     */
    default double getDouble(@Nonnull String name, double defaultValue) {
        String value = getString(name);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    /**
     * Returns {@code true} if the property value is enabled checked by {@link StringKit#isEnabled(CharSequence)},
     * otherwise {@code false}.
     *
     * @param name the name of the property
     * @return {@code true} if the property value is enabled checked by {@link StringKit#isEnabled(CharSequence)},
     * otherwise {@code false}.
     */
    default boolean getBoolean(@Nonnull String name) {
        String value = getString(name);
        return value != null && StringKit.isEnabled(value);
    }

    /**
     * Sets the value of the property.
     *
     * @param name  the name of the property
     * @param value the value to set
     */
    default void set(@Nonnull String name, @Nonnull Object value) {
        asMap().put(name, value);
    }

    /**
     * Removes the property.
     *
     * @param name the name of the property
     */
    default void remove(@Nonnull String name) {
        asMap().remove(name);
    }

    /**
     * Returns a properties view of the properties, any changes to the properties will be reflected in the map, and vice
     * versa.
     *
     * @return a properties view of the properties
     */
    @Nonnull
    Properties asProperties();

    /**
     * Returns a map view of the properties, any changes to the map will be reflected in the properties, and vice
     * versa.
     *
     * @return a map view of the properties
     */
    default @Nonnull Map<@Nonnull String, @Nonnull Object> asMap() {
        return Fs.as(asProperties());
    }

    /**
     * Returns a {@link DataMap} view of the properties, any changes to the map will be reflected in the properties, and
     * vice versa.
     *
     * @return a {@link DataMap} view of the properties
     */
    default @Nonnull DataMap asDataMap() {
        return DataMap.wrap(asMap());
    }

    /**
     * Writes the properties to the given output stream with the {@link CharsKit#defaultCharset()}.
     *
     * @param out the output stream to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    default void writeTo(@Nonnull OutputStream out) throws IORuntimeException {
        Writer writer = IOKit.newWriter(out, CharsKit.defaultCharset());
        writeTo(writer);
    }

    /**
     * Writes the properties to the given writable byte channel with the {@link CharsKit#defaultCharset()}.
     *
     * @param channel the writable byte channel to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    @Override
    default void writeTo(@Nonnull WritableByteChannel channel) throws IORuntimeException {
        // compatible with JDK8
        @SuppressWarnings("CharsetObjectCanBeUsed")
        Writer writer = Channels.newWriter(channel, CharsKit.defaultCharset().name());
        writeTo(writer);
    }

    @Override
    default void writeTo(@Nonnull Writer writer) throws IORuntimeException {
        Fs.uncheck(() -> asProperties().store(writer, null), IORuntimeException::new);
    }
}
