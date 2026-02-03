package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This interface extends the {@link Map} interface with signature {@code Map<String, Object>}, provides methods to get
 * and convert values to common types, such as {@link #getString(String)}, {@link #getInt(String)}, etc. The original
 * methods of {@link Map} are also supported and without any conversion.
 *
 * @author sunqian
 */
public interface DataMap extends Map<String, Object> {

    /**
     * Returns a new {@link DataMap} based on the {@link LinkedHashMap} with the
     * {@link ObjectConverter#defaultConverter()} to use for conversion values and empty options.
     *
     * @return the wrapped {@link DataMap}
     */
    static @Nonnull DataMap newMap() {
        return wrap(new LinkedHashMap<>());
    }

    /**
     * Wraps the given {@link Map} to a {@link DataMap} with the {@link ObjectConverter#defaultConverter()} to use for
     * conversion values and empty options.
     *
     * @param map the {@link Map} to wrap
     * @return the wrapped {@link DataMap}
     */
    static @Nonnull DataMap wrap(@Nonnull Map<String, Object> map) {
        return wrap(map, ObjectConverter.defaultConverter(), Option.emptyOptions());
    }

    /**
     * Wraps the given {@link Map} to a {@link DataMap} with the given {@link ObjectConverter} and options.
     *
     * @param map       the {@link Map} to wrap
     * @param converter the {@link ObjectConverter} to use for conversion values
     * @param options   the options to use for conversion
     * @return the wrapped {@link DataMap}
     */
    static @Nonnull DataMap wrap(
        @Nonnull Map<String, Object> map,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull [] options
    ) {
        return new DataMapImpl(map, converter, options);
    }

    /**
     * Gets the value for the specified key, then converts it to the type of the default value. If the key is not found,
     * returns {@code defaultValue}.
     * <p>
     * Note that this method uses {@link Object#getClass()} to get the type of the default value, if the default value
     * is {@code null}, the type to convert the value to will be {@link Object}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted value of the specified key, or {@code defaultValue} if the key is not found
     */
    default <T> T get(@Nonnull String key, T defaultValue) throws DataException {
        return get(key, defaultValue == null ? Object.class : defaultValue.getClass(), defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the specified type. If the key is not found, returns
     * {@code defaultValue}.
     * <p>
     * Note the specified type must be the type of the default value.
     *
     * @param key          the specified key
     * @param type         the type to convert the value to
     * @param defaultValue the default value
     * @return the converted value of the specified key, or {@code defaultValue} if the key is not found
     */
    <T> T get(@Nonnull String key, @Nonnull Type type, T defaultValue) throws DataException;

    /**
     * Gets the value for the specified key, then converts it to the String type. If the key is not found, returns
     * {@code null}.
     *
     * @param key the specified key
     * @return the converted string value of the specified key, or {@code null} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable String getString(@Nonnull String key) throws DataException {
        return getString(key, null);
    }

    /**
     * Gets the value for the specified key, then converts it to the String type. If the key is not found, returns
     * {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted string value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default String getString(@Nonnull String key, String defaultValue) throws DataException {
        return get(key, String.class, defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the int type. If the key is not found, returns
     * {@code 0}.
     *
     * @param key the specified key
     * @return the converted int value of the specified key, or {@code 0} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default int getInt(@Nonnull String key) throws DataException {
        return getInt(key, 0);
    }

    /**
     * Gets the value for the specified key, then converts it to the int type. If the key is not found, returns
     * {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted int value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default int getInt(@Nonnull String key, int defaultValue) throws DataException {
        return get(key, int.class, defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the long type. If the key is not found, returns
     * {@code 0}.
     *
     * @param key the specified key
     * @return the converted long value of the specified key, or {@code 0} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default long getLong(@Nonnull String key) throws DataException {
        return getLong(key, 0L);
    }

    /**
     * Gets the value for the specified key, then converts it to the long type. If the key is not found, returns
     * {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted long value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default long getLong(@Nonnull String key, long defaultValue) throws DataException {
        return get(key, long.class, defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the float type. If the key is not found, returns
     * {@code 0.0f}.
     *
     * @param key the specified key
     * @return the converted float value of the specified key, or {@code 0.0f} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default float getFloat(@Nonnull String key) throws DataException {
        return getFloat(key, 0.0f);
    }

    /**
     * Gets the value for the specified key, then converts it to the float type. If the key is not found, returns
     * {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted float value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default float getFloat(@Nonnull String key, float defaultValue) throws DataException {
        return get(key, float.class, defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the double type. If the key is not found, returns
     * {@code 0.0}.
     *
     * @param key the specified key
     * @return the converted double value of the specified key, or {@code 0.0} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default double getDouble(@Nonnull String key) throws DataException {
        return getDouble(key, 0.0);
    }

    /**
     * Gets the value for the specified key, then converts it to the double type. If the key is not found, returns
     * {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted double value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default double getDouble(@Nonnull String key, double defaultValue) throws DataException {
        return get(key, double.class, defaultValue);
    }

    /**
     * Gets the value for the specified key, then converts it to the {@link BigDecimal} type. If the key is not found,
     * returns {@code null}.
     *
     * @param key the specified key
     * @return the converted BigDecimal value of the specified key, or {@code null} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable BigDecimal getBigDecimal(@Nonnull String key) throws DataException {
        return getBigDecimal(key, null);
    }

    /**
     * Gets the value for the specified key, then converts it to the {@link BigDecimal} type. If the key is not found,
     * returns {@code defaultValue}.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the converted BigDecimal value of the specified key, or {@code defaultValue} if the key is not found
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable BigDecimal getBigDecimal(
        @Nonnull String key, @Nullable BigDecimal defaultValue
    ) throws DataException {
        return get(key, BigDecimal.class, defaultValue);
    }

    /**
     * Returns {@code true} if the given object is an instance of {@link DataMap} their contents are equal,
     * {@code false} otherwise.
     *
     * @param o object to be compared for equality with this
     * @return {@code true} if the given object is an instance of {@link DataMap} their contents are equal,
     * {@code false} otherwise.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns {@code true} if the content of this {@link DataMap } are equal to the content of the given map,
     * {@code false} otherwise.
     *
     * @param o the given map to be compared with this
     * @return {@code true} if the content of this {@link DataMap } are equal to the content of the given map,
     * {@code false} otherwise.
     */
    boolean contentEquals(@Nullable Map<String, Object> o);
}
