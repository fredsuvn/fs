package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * This interface represents a JSON data, which can be formatted to a string.
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
     * Returns the string representation of the current JSON data if the type of the current JSON data is JSON string.
     *
     * @return the string representation of the current JSON data if the type of the current JSON data is JSON string
     * @throws JsonDataException if the current JSON data is not a JSON string
     */
    @Nonnull
    String asString() throws JsonDataException;

    /**
     * Returns the {@link JsonMap} representation of the current JSON data if the type of the current JSON data is JSON
     * object.
     *
     * @return the {@link JsonMap} representation of the current JSON data if the type of the current JSON data is JSON
     * object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    @Nonnull
    JsonMap asObject() throws JsonDataException;

    /**
     * Returns an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object.
     *
     * @param clazz the object type to parse the current JSON data to
     * @param <T>   the type of the object to parse the current JSON data to
     * @return an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    default @Nonnull <T> T asObject(Class<T> clazz) throws JsonDataException {
        return asObject((Type) clazz);
    }

    /**
     * Returns an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object.
     *
     * @param typeRef the object type reference to parse the current JSON data to
     * @param <T>     the type of the object to parse the current JSON data to
     * @return an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    default @Nonnull <T> T asObject(TypeRef<T> typeRef) throws JsonDataException {
        return asObject(typeRef.type());
    }

    /**
     * Returns an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object.
     *
     * @param type the object type to parse the current JSON data to
     * @param <T>  the type of the object to parse the current JSON data to
     * @return an object of the given type representation of the current JSON data if the type of the current JSON data
     * is JSON object
     * @throws JsonDataException if the current JSON data is not a JSON object
     */
    @Nonnull
    <T> T asObject(Type type) throws JsonDataException;

    /**
     * Returns the {@link JsonList} representation of the current JSON data if the type of the current JSON data is JSON
     * array.
     *
     * @return the {@link JsonList} representation of the current JSON data if the type of the current JSON data is JSON
     * array
     * @throws JsonDataException if the current JSON data is not a JSON array
     */
    @Nonnull
    JsonList asArray() throws JsonDataException;

    /**
     * Returns the int representation of the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return the int representation of the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    int asInt() throws JsonDataException;

    /**
     * Returns the long representation of the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return the long representation of the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    long asLong() throws JsonDataException;

    /**
     * Returns the float representation of the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return the float representation of the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    float asFloat() throws JsonDataException;

    /**
     * Returns the double representation of the current JSON data if the type of the current JSON data is JSON number.
     *
     * @return the double representation of the current JSON data if the type of the current JSON data is JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    double asDouble() throws JsonDataException;

    /**
     * Returns the {@link BigDecimal} representation of the current JSON data if the type of the current JSON data is
     * JSON number.
     *
     * @return the {@link BigDecimal} representation of the current JSON data if the type of the current JSON data is
     * JSON number
     * @throws JsonDataException if the current JSON data is not a JSON number
     */
    @Nonnull
    BigDecimal asBigDecimal() throws JsonDataException;

    /**
     * Returns the boolean representation of the current JSON data if the type of the current JSON data is JSON
     * boolean.
     *
     * @return the boolean representation of the current JSON data if the type of the current JSON data is JSON boolean
     * @throws JsonDataException if the current JSON data is not a JSON boolean
     */
    boolean asBoolean() throws JsonDataException;

    /**
     * Returns the null representation of the current JSON data if the type of the current JSON data is JSON null.
     *
     * @return the null representation of the current JSON data if the type of the current JSON data is JSON null
     * @throws JsonDataException if the current JSON data is not a JSON null
     */
    @Nonnull
    JsonNull asNUll() throws JsonDataException;
}
