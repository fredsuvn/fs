package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This is a {@link Map} interface extension for {@code JSON} object, of which signature is {@code Map<String, Object>}.
 * There are only 6 java types of values in this map to map the JSON types:
 * <ul>
 *     <li>{@link String} to JSON string;</li>
 *     <li>{@link Number} to JSON number;</li>
 *     <li>{@link JsonMap} to JSON object;</li>
 *     <li>{@link JsonList} to JSON array;</li>
 *     <li>{@link Boolean} to JSON boolean;</li>
 *     <li>{@link JsonNull} to JSON null;</li>
 * </ul>
 * Writes the value of which type is not in the above 6 types will throw a {@link JsonDataException}.
 * <p>
 * In addition to the method of {@link Map} itself, this interface also provides methods for directly obtaining the
 * value of Java type corresponding to JSON, such as {@link #getString(String)} for JSON string,
 * {@link #getInt(String)} for JSON number, etc.
 *
 * @author sunqian
 */
public interface JsonMap extends Map<String, Object>, JsonOutputableData {

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
        Object value = get(key);
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
        Object value = get(key);
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
        Object value = get(key);
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
        Object value = get(key);
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
        Object value = get(key);
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
        Object value = get(key);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof java.math.BigDecimal) {
            return (java.math.BigDecimal) value;
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON number.");
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
    default @Nullable JsonMap getObject(@Nonnull String key) throws JsonDataException {
        Object value = get(key);
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
    default @Nullable JsonList getArray(@Nonnull String key) throws JsonDataException {
        Object value = get(key);
        if (JsonBack.isNullValue(value)) {
            return null;
        }
        if (value instanceof List<?>) {
            return Fs.as(value);
        }
        throw new JsonDataException("Value of key '" + key + "' is not a JSON array.");
    }
}
