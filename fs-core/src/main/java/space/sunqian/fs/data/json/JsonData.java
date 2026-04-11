package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.data.ByteData;
import space.sunqian.fs.data.CharData;
import space.sunqian.fs.data.DataException;
import space.sunqian.fs.data.DataList;
import space.sunqian.fs.data.DataMap;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * The interface represents JSON data.
 *
 * @author sunqian
 */
public interface JsonData extends ByteData, CharData {

    /**
     * Returns a {@link JsonData} represents JSON null.
     *
     * @return a {@link JsonData} represents JSON null
     */
    static @Nonnull JsonData ofNull() {
        return JsonDataBack.JsonNull.INST;
    }

    /**
     * Returns a {@link JsonData} represents a JSON string whose value is the given string.
     *
     * @param string the given JSON string
     * @return a {@link JsonData} represents a JSON string whose value is the given string
     */
    static @Nonnull JsonData ofString(@Nonnull String string) {
        return new JsonDataBack.JsonString(string);
    }

    /**
     * Returns a {@link JsonData} represents a JSON number whose value is the given number.
     *
     * @param number the given JSON number
     * @return a {@link JsonData} represents a JSON number whose value is the given number
     */
    static @Nonnull JsonData ofNumber(@Nonnull Number number) {
        return new JsonDataBack.JsonNumber(number);
    }

    /**
     * Returns a {@link JsonData} represents a JSON boolean whose value is the given boolean.
     *
     * @param bool the given JSON boolean
     * @return a {@link JsonData} represents a JSON boolean whose value is the given boolean
     */
    static @Nonnull JsonData ofBoolean(boolean bool) {
        return bool ? JsonDataBack.JsonBoolean.TRUE : JsonDataBack.JsonBoolean.FALSE;
    }

    /**
     * Returns a {@link JsonData} represents a JSON object whose value is the given map.
     *
     * @param map the given map
     * @return a {@link JsonData} represents a JSON object whose value is the given map
     */
    static @Nonnull JsonData ofMap(@Nonnull @RetainedParam Map<@Nonnull String, @Nullable Object> map) {
        return new JsonDataBack.JsonObject(map);
    }

    /**
     * Returns a {@link JsonData} represents a JSON array whose value is the given list.
     *
     * @param array the given list
     * @return a {@link JsonData} represents a JSON array whose value is the given list
     */
    static @Nonnull JsonData ofList(@Nonnull @RetainedParam List<@Nullable Object> array) {
        return new JsonDataBack.JsonArray(array);
    }

    /**
     * Returns a {@link JsonData} represents a JSON array whose value is the given array.
     *
     * @param array the given array
     * @return a {@link JsonData} represents a JSON array whose value is the given array
     */
    static @Nonnull JsonData ofArray(@Nullable Object @Nonnull @RetainedParam ... array) {
        return new JsonDataBack.JsonArray(array);
    }

    /**
     * Returns the type of the current JSON data.
     *
     * @return the type of the current JSON data
     */
    @Nonnull
    JsonType type();

    /**
     * Returns {@code true} if the current JSON data is JSON null.
     *
     * @return {@code true} if the current JSON data is JSON null
     */
    default boolean isNull() {
        return type() == JsonType.NULL;
    }

    /**
     * Returns the string parsed from the current JSON data if the type of the current JSON data is JSON string.
     *
     * @return the string parsed from the current JSON data if the type of the current JSON data is JSON string
     * @throws JsonDataException if the current JSON data is not a JSON string
     */
    @Nonnull
    String asString() throws JsonDataException;

    /**
     * Returns a map parsed from the current JSON data if the type of the current JSON data is JSON object. Note the
     * returned map is mutable.
     *
     * @return a map parsed from the current JSON data if the type of the current JSON data is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    @Nonnull
    Map<String, Object> asMap() throws JsonDataException;

    /**
     * Returns a list parsed from the current JSON data if the type of the current JSON data is JSON array. Note the
     * returned list is mutable.
     *
     * @return a list parsed from the current JSON data if the type of the current JSON data is JSON array
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    @Nonnull
    List<Object> asList() throws JsonDataException;

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
     * Returns a {@link DataMap} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asMap()} if the
     * type of the current JSON data is JSON object.
     *
     * @return a {@link DataMap} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asMap()} if the
     * type of the current JSON data is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    default @Nonnull DataMap asDataMap() throws JsonDataException {
        return DataMap.wrap(asMap());
    }

    /**
     * Returns a {@link DataList} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asList()} if
     * the type of the current JSON data is JSON array.
     *
     * @return a {@link DataList} with the {@link ObjectConverter#defaultConverter()} to wrap the {@link #asList()} if
     * the type of the current JSON data is JSON array
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    default @Nonnull DataList asDataList() throws JsonDataException {
        return DataList.wrap(asList());
    }

    /**
     * Converts this {@link JsonData} to a new object of the specified class.
     * <p>
     * This method supports {@code JSON Object}, {@code JSON Array}, {@code JSON String}, {@code JSON Number},
     * {@code JSON Boolean}, but not {@code JSON Null}.
     *
     * @param cls the specified class
     * @param <T> the type of the object to be returned`
     * @return a new object of the specified class
     * @throws DataException if an error occurs during the conversion
     */
    default <T> @Nonnull T toObject(Class<T> cls) throws DataException {
        return Fs.as(toObject((Type) cls));
    }

    /**
     * Converts this {@link JsonData} to a new object of the specified class.
     * <p>
     * This method supports {@code JSON Object}, {@code JSON Array}, {@code JSON String}, {@code JSON Number},
     * {@code JSON Boolean}, but not {@code JSON Null}.
     *
     * @param typeRef the reference of the specified class
     * @param <T>     the type of the object to be returned`
     * @return a new object of the specified class
     * @throws DataException if an error occurs during the conversion
     */
    default <T> @Nonnull T toObject(TypeRef<T> typeRef) throws DataException {
        return Fs.as(toObject(typeRef.type()));
    }

    /**
     * Converts this {@link JsonData} to a new object of the specified type.
     * <p>
     * This method supports {@code JSON Object}, {@code JSON Array}, {@code JSON String}, {@code JSON Number},
     * {@code JSON Boolean}, but not {@code JSON Null}.
     *
     * @param type the specified type
     * @return a new object of the specified type
     * @throws DataException if an error occurs during the conversion
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    default @Nonnull Object toObject(Type type) throws DataException {
        JsonType jsonType = type();
        switch (jsonType) {
            case OBJECT:
                return asDataMap().toObject(type);
            case ARRAY:
                return asDataList().toObject(type);
            case STRING:
                return asString();
            case NUMBER:
                return asNumber();
            case BOOLEAN:
                return asBoolean();
            default:
                throw new DataException("Unsupported JSON type: " + jsonType + ".");
        }
    }

    /**
     * Returns the JSON string of the current JSON data.
     *
     * @return the JSON string of the current JSON data
     */
    @Nonnull
    String toString();
}
