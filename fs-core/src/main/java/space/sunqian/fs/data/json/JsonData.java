package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.data.OutputableData;
import space.sunqian.fs.io.IOKit;

import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface JsonData extends OutputableData {

    /**
     * Gets the object value for the specified key, or {@code null} if the key is not found.
     * <p>
     * Note if the key exists but the value is {@code null}, {@link JsonNull} will be returned.
     *
     * @param key the specified key
     * @return the object value of the specified key, or {@code null} if the key is not found
     */
    default @Nullable Object getObject(@Nonnull String key) {
        return getObject(key, null);
    }

    /**
     * Gets the object value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     * <p>
     * Note if the key exists but the value is {@code null}, {@link JsonNull} will be returned.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the object value of the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null
     */
    default Object getObject(@Nonnull String key, Object defaultValue) {
        Object value = asJsonObject().get(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the string value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the string value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON string
     */
    default @Nullable String getString(@Nonnull String key) throws JsonDataException {
        return getString(key, null);
    }

    /**
     * Gets the string value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the string value of the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null
     * @throws JsonDataException if the value is not a JSON string
     */
    default String getString(@Nonnull String key, String defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof String) {
            return (String) value;
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON string.");
    }

    /**
     * Gets the int value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the int value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON number
     */
    default int getInt(@Nonnull String key) throws JsonDataException {
        return getInt(key, 0);
    }

    /**
     * Gets the int value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the int value of the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null
     * @throws JsonDataException if the value is not a JSON number
     */
    default int getInt(@Nonnull String key, int defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
    }

    /**
     * Gets the long value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the long value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON number
     */
    default long getLong(@Nonnull String key) throws JsonDataException {
        return getLong(key, 0L);
    }

    /**
     * Gets the long value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the long value of the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null
     * @throws JsonDataException if the value is not a JSON number
     **/
    default long getLong(@Nonnull String key, long defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
    }

    /**
     * Gets the float value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the float value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON number
     */
    default float getFloat(@Nonnull String key) throws JsonDataException {
        return getFloat(key, 0.0f);
    }

    /**
     * Gets the float value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the float value of the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null
     * @throws JsonDataException if the value is not a JSON number
     **/
    default float getFloat(@Nonnull String key, float defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
    }

    /**
     * Gets the double value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the double value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON number
     */
    default double getDouble(@Nonnull String key) throws JsonDataException {
        return getDouble(key, 0.0);
    }

    /**
     * Gets the double value for the specified key, or {@code defaultValue} if the key is not found or the value is JSON
     * null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the double value of the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null
     * @throws JsonDataException if the value is not a JSON number
     **/
    default double getDouble(@Nonnull String key, double defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
    }

    /**
     * Gets the decimal value for the specified key, or {@code null} if the key is not found or the value is JSON null.
     *
     * @param key the specified key
     * @return the decimal value of the specified key, or {@code null} if the key is not found or the value is JSON null
     * @throws JsonDataException if the value is not a JSON decimal number
     */
    default BigDecimal getDecimal(@Nonnull String key) throws JsonDataException {
        return getDecimal(key, null);
    }

    /**
     * Gets the decimal value for the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null.
     *
     * @param key          the specified key
     * @param defaultValue the default value
     * @return the decimal value of the specified key, or {@code defaultValue} if the key is not found or the value is
     * JSON null
     * @throws JsonDataException if the value is not a JSON number
     */
    default BigDecimal getDecimal(@Nonnull String key, BigDecimal defaultValue) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof java.math.BigDecimal) {
            return (java.math.BigDecimal) value;
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
    }

    /**
     * Returns whether the specified key exists in the JSON data. Note the keys of which value are JSON null are also
     * considered as existing.
     *
     * @param key the specified key
     * @return {@code true} if the specified key exists, {@code false} otherwise
     */
    default boolean contains(@Nonnull String key) {
        Object value = getObject(key);
        return value != null;
    }

    /**
     * Gets the JSON object value for the specified key, or {@code null} if the key is not found or the value is JSON
     * null.
     *
     * @param key the specified key
     * @return the JSON object value of the specified key, or {@code null} if the key is not found or the value is JSON
     * null
     * @throws JsonDataException if the value is not a JSON object
     */
    default @Nullable Map<@Nonnull String, @Nonnull Object> getJsonObject(
        @Nonnull String key
    ) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return null;
        }
        if (value instanceof Map<?, ?>) {
            return Fs.as(value);
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON object.");
    }

    /**
     * Gets the JSON array value for the specified key, or {@code null} if the key is not found or the value is JSON
     * null.
     *
     * @param key the specified key
     * @return the JSON array value of the specified key, or {@code null} if the key is not found or the value is JSON
     * null
     * @throws JsonDataException if the value is not a JSON array
     */
    default @Nullable List<@Nonnull Object> getJsonArray(
        @Nonnull String key
    ) throws JsonDataException {
        Object value = getObject(key);
        if (JsonBack.isNullValue(value)) {
            return null;
        }
        if (value instanceof List<?>) {
            return Fs.as(value);
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON array.");
    }

    /**
     * Returns a {@link Map} view of the current JSON data, any changes to the JSON data will be reflected in the map,
     * and vice versa.
     *
     * @return a {@link Map} view of the current JSON data
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    @Nonnull
    Map<@Nonnull String, @Nonnull Object> asJsonObject() throws JsonDataException;

    /**
     * Returns a {@link List} view of the current JSON data, any changes to the JSON data will be reflected in the list,
     * and vice versa.
     *
     * @return a {@link List} view of the current JSON data
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    @Nonnull
    List<@Nonnull Object> asJsonArray() throws JsonDataException;

    /**
     * Formats and returns the string representation of the current JSON data.
     *
     * @return the formatted string representation of the current JSON data
     * @throws JsonDataException if the current JSON data is not a valid JSON
     */
    default @Nonnull String toJsonString() throws JsonDataException {
        StringWriter stringWriter = new StringWriter();
        OutputStream out = IOKit.newOutputStream(stringWriter);
        writeTo(out);
        return stringWriter.toString();
    }
}
