package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.OutputableData;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

/**
 * The interface to access properties data.
 *
 * @author sunqian
 */
public interface PropertiesData extends OutputableData {

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
     * Loads properties from the given input stream, using the specified charset.
     * <p>
     * This method uses {@link PropertiesParser#defaultParser()} to parse the properties.
     *
     * @param in      the input stream to read from
     * @param charset the charset to use
     * @return a new {@link PropertiesData} wraps the loaded properties
     * @throws IORuntimeException if an I/O error occurs while loading the properties
     */
    static @Nonnull PropertiesData load(@Nonnull InputStream in, @Nonnull Charset charset) throws IORuntimeException {
        return PropertiesParser.defaultParser().parse(in, charset);
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
    default @Nonnull String getString(@Nonnull String name, @Nonnull String defaultValue) {
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
     * Returns {@code true} if the property value is one of "true", "1", "yes", "y", or "enabled" (case-insensitive),
     * otherwise {@code false}.
     *
     * @param name the name of the property
     * @return {@code true} if the property value is one of "true", "1", "yes", "y", or "enabled" (case-insensitive),
     * otherwise {@code false}.
     */
    default boolean getBoolean(String name) {
        String value = getString(name);
        return value != null && (
            value.equalsIgnoreCase("true")
                || value.equals("1")
                || value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("y")
                || value.equalsIgnoreCase("enabled")
        );
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

    @Override
    default void writeTo(@Nonnull OutputStream out, @Nonnull Charset charset) throws IORuntimeException {
        Properties properties = asProperties();
        Writer writer = IOKit.newWriter(out, charset);
        Fs.uncheck(() -> properties.store(writer, null), IORuntimeException::new);
    }
}
