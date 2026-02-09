package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.data.DataList;
import space.sunqian.fs.data.DataMap;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a JSON data, typically parsed from a JSON string, and also can be formatted to a JSON
 * string.
 *
 * @author sunqian
 */
public interface JsonData extends JsonOutputableData {

    /**
     * Returns the type of the current JSON data.
     *
     * @return the type of the current JSON data
     */
    @Nonnull
    JsonType type();

    /**
     * Returns the string parsed from the current JSON data if the type of the current JSON data is JSON string.
     *
     * @return the string parsed from the current JSON data if the type of the current JSON data is JSON string
     * @throws JsonDataException if the current JSON data is not a JSON string
     */
    @Nonnull
    String asString() throws JsonDataException;

    /**
     * Returns a map parsed from the current JSON data if the type of the current JSON data is JSON object.
     *
     * @return a map parsed from the current JSON data if the type of the current JSON data is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    @Nonnull
    Map<String, Object> asObject() throws JsonDataException;

    /**
     * Returns a list parsed from the current JSON data if the type of the current JSON data is JSON array.
     *
     * @return a list parsed from the current JSON data if the type of the current JSON data is JSON array
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    @Nonnull
    List<Object> asArray() throws JsonDataException;

    /**
     * Returns an int value parsed from the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return an int value parsed from the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    default int asInt() throws JsonDataException {
        return asNumber().intValue();
    }

    /**
     * Returns a long value parsed from the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return a long value parsed from the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    default long asLong() throws JsonDataException {
        return asNumber().longValue();
    }

    /**
     * Returns a float value parsed from the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return a float value parsed from the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    default float asFloat() throws JsonDataException {
        return asNumber().floatValue();
    }

    /**
     * Returns a double value parsed from the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return a double value parsed from the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    default double asDouble() throws JsonDataException {
        return asNumber().doubleValue();
    }

    /**
     * Returns a {@link BigDecimal} value parsed from the current JSON data if the type of the current JSON data is JSON
     * number.
     *
     * @return a {@link BigDecimal} value parsed from the current JSON data if the type of the current JSON data is JSON
     * number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    default @Nonnull BigDecimal asBigDecimal() throws JsonDataException {
        Number number = asNumber();
        return number instanceof BigDecimal ? (BigDecimal) number : new BigDecimal(number.toString());
    }

    /**
     * Returns a {@link Number} value parsed from the current JSON data if the type of the current JSON data is JSON
     * number.
     *
     * @return a {@link Number} value parsed from the current JSON data if the type of the current JSON data is JSON
     * number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    @Nonnull
    Number asNumber() throws JsonDataException;

    /**
     * Returns a boolean value parsed from the current JSON data if the type of the current JSON data is JSON boolean.
     *
     * @return a boolean value parsed from the current JSON data if the type of the current JSON data is JSON boolean
     * @throws JsonDataException if the current JSON data is not a JSON boolean
     */
    boolean asBoolean() throws JsonDataException;

    /**
     * Returns a {@link JsonNull} parsed from the current JSON data if the type of the current JSON data is JSON null.
     *
     * @return a {@link JsonNull} parsed from the current JSON data if the type of the current JSON data is JSON null
     * @throws JsonDataException if the current JSON data is not a JSON null
     */
    @Nonnull
    JsonNull asNUll() throws JsonDataException;

    /**
     * Returns a {@link DataMap} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asObject()} if
     * the type of the current JSON data is JSON object.
     *
     * @return a {@link DataMap} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asObject()} if
     * the type of the current JSON data is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    default @Nonnull DataMap asDataMap() throws JsonDataException {
        return DataMap.wrap(asObject());
    }

    /**
     * Returns a {@link DataList} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asArray()} if
     * the type of the current JSON data is JSON array.
     *
     * @return a {@link DataList} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asArray()} if
     * the type of the current JSON data is JSON array
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    default @Nonnull DataList asDataList() throws JsonDataException {
        return DataList.wrap(asArray());
    }
}
