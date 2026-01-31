package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This is a {@link List} interface extension for {@code JSON} array, of which signature is {@code List<Object>}. There
 * are only 6 java types of elements in this list to map the JSON types:
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
 * In addition to the method of {@link List} itself, this interface also provides methods for directly obtaining the
 * value of Java type corresponding to JSON, such as {@link #getString(int)} for JSON string,
 * {@link #getInt(int)} for JSON number, etc.
 *
 * @author sunqian
 */
public interface JsonList extends List<Object>, JsonData {

    /**
     * Gets the string value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the string value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON string
     */
    default @Nullable String getString(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getString(index, null);
    }

    /**
     * Gets the string value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the string value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON string
     */
    default @Nullable String getString(
        int index, @Nullable String defaultValue
    ) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof String) {
            return (String) value;
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON string.");
    }

    /**
     * Gets the int value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the int value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     */
    default int getInt(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getInt(index, 0);
    }

    /**
     * Gets the int value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the int value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     */
    default int getInt(int index, int defaultValue) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON number.");
    }

    /**
     * Gets the long value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the long value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     */
    default long getLong(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getLong(index, 0L);
    }

    /**
     * Gets the long value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the long value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     **/
    default long getLong(int index, long defaultValue) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON number.");
    }

    /**
     * Gets the float value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the float value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     */
    default float getFloat(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getFloat(index, 0.0f);
    }

    /**
     * Gets the float value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the float value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     **/
    default float getFloat(int index, float defaultValue) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON number.");
    }

    /**
     * Gets the double value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the double value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     */
    default double getDouble(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getDouble(index, 0.0);
    }

    /**
     * Gets the double value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the double value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON number
     **/
    default double getDouble(int index, double defaultValue) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON number.");
    }

    /**
     * Gets the decimal value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the decimal value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON decimal number
     */
    default BigDecimal getDecimal(int index) throws IndexOutOfBoundsException, JsonDataException {
        return getDecimal(index, null);
    }

    /**
     * Gets the decimal value for the specified index, or {@code defaultValue} if the value is JSON null.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the decimal value of the specified index, or {@code defaultValue} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON decimal number
     */
    default BigDecimal getDecimal(
        int index, BigDecimal defaultValue
    ) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return defaultValue;
        }
        if (value instanceof java.math.BigDecimal) {
            return (java.math.BigDecimal) value;
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON decimal number.");
    }

    /**
     * Gets the JSON object value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the JSON object value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON object
     */
    default @Nullable JsonMap getJsonObject(int index) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return null;
        }
        if (value instanceof Map<?, ?>) {
            return Fs.as(value);
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON object.");
    }

    /**
     * Gets the JSON array value for the specified index, or {@code null} if the value is JSON null.
     *
     * @param index the specified index
     * @return the JSON array value of the specified index, or {@code null} if the value is JSON null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws JsonDataException         if the value is not a JSON array
     */
    default @Nullable JsonList getJsonArray(int index) throws IndexOutOfBoundsException, JsonDataException {
        Object value = get(index);
        if (JsonBack.isNullValue(value)) {
            return null;
        }
        if (value instanceof List<?>) {
            return Fs.as(value);
        }
        throw new JsonDataException("Value of index '" + index + "' is not a JSON array.");
    }
}
