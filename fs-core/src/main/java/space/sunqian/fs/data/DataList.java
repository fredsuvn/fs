package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface extends the {@link List} interface with signature {@code List<Object>}, provides methods to get and
 * convert values to common types, such as {@link #getString(int)}, {@link #getInt(int)}, etc. The original methods of
 * {@link List} are also supported and without any conversion.
 *
 * @author sunqian
 */
public interface DataList extends List<Object> {

    /**
     * Returns a new {@link DataList} based on the {@link ArrayList} with the {@link ObjectConverter#defaultConverter()}
     * to use for conversion values and empty options.
     *
     * @return the wrapped {@link DataList}
     */
    static @Nonnull DataList newList() {
        return wrap(new ArrayList<>());
    }

    /**
     * Wraps the given {@link List} to a {@link DataList} with the {@link ObjectConverter#defaultConverter()} to use for
     * conversion values and empty options.
     *
     * @param list the {@link List} to wrap
     * @return the wrapped {@link DataList}
     */
    static @Nonnull DataList wrap(@Nonnull List<?> list) {
        return wrap(list, ObjectConverter.defaultConverter(), Option.emptyOptions());
    }

    /**
     * Wraps the given {@link List} to a {@link DataList} with the given {@link ObjectConverter} and default options.
     *
     * @param list           the {@link List} to wrap
     * @param converter      the {@link ObjectConverter} to use for conversion values
     * @param defaultOptions the default options to use for conversion
     * @return the wrapped {@link DataList}
     */
    static @Nonnull DataList wrap(
        @Nonnull List<?> list,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... defaultOptions
    ) {
        return new DataListImpl(list, converter, defaultOptions);
    }

    /**
     * Gets the value at the specified index, then converts it to the type of the default value. If the key is out of
     * bounds, returns {@code defaultValue}.
     * <p>
     * Note that this method uses {@link Object#getClass()} to get the type of the default value, if the default value
     * is {@code null}, the type to convert the value to will be {@link Object}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted value of the specified index, or {@code defaultValue} if the key is out of bounds
     */
    default <T> T get(int index, T defaultValue) throws DataException {
        return get(index, defaultValue == null ? Object.class : defaultValue.getClass(), defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the specified type. If the key is out of bounds,
     * returns {@code defaultValue}.
     * <p>
     * Note the specified type must be the type of the default value.
     *
     * @param index        the specified index
     * @param type         the type to convert the value to
     * @param defaultValue the default value
     * @return the converted value of the specified index, or {@code defaultValue} if the key is out of bounds
     */
    <T> T get(int index, @Nonnull Type type, T defaultValue) throws DataException;

    /**
     * Gets the value for the specified index, then converts it to the String type. If the key is out of bounds, returns
     * {@code null}.
     *
     * @param index the specified index
     * @return the converted string value of the specified index, or {@code null} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable String getString(int index) throws DataException {
        return getString(index, null);
    }

    /**
     * Gets the value for the specified index, then converts it to the String type. If the key is out of bounds, returns
     * {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted string value of the specified index, or {@code defaultValue} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default String getString(int index, String defaultValue) throws DataException {
        return get(index, String.class, defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the int type. If the key is out of bounds, returns
     * {@code 0}.
     *
     * @param index the specified index
     * @return the converted int value of the specified index, or {@code 0} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default int getInt(int index) throws DataException {
        return getInt(index, 0);
    }

    /**
     * Gets the value for the specified index, then converts it to the int type. If the key is out of bounds, returns
     * {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted int value of the specified index, or {@code defaultValue} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default int getInt(int index, int defaultValue) throws DataException {
        return get(index, int.class, defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the long type. If the key is out of bounds, returns
     * {@code 0}.
     *
     * @param index the specified index
     * @return the converted long value of the specified index, or {@code 0} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default long getLong(int index) throws DataException {
        return getLong(index, 0L);
    }

    /**
     * Gets the value for the specified index, then converts it to the long type. If the key is out of bounds, returns
     * {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted long value of the specified index, or {@code defaultValue} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default long getLong(int index, long defaultValue) throws DataException {
        return get(index, long.class, defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the float type. If the key is out of bounds, returns
     * {@code 0.0f}.
     *
     * @param index the specified index
     * @return the converted float value of the specified index, or {@code 0.0f} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default float getFloat(int index) throws DataException {
        return getFloat(index, 0.0f);
    }

    /**
     * Gets the value for the specified index, then converts it to the float type. If the key is out of bounds, returns
     * {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted float value of the specified index, or {@code defaultValue} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default float getFloat(int index, float defaultValue) throws DataException {
        return get(index, float.class, defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the double type. If the key is out of bounds, returns
     * {@code 0.0}.
     *
     * @param index the specified index
     * @return the converted double value of the specified index, or {@code 0.0} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default double getDouble(int index) throws DataException {
        return getDouble(index, 0.0);
    }

    /**
     * Gets the value for the specified index, then converts it to the double type. If the key is out of bounds, returns
     * {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted double value of the specified index, or {@code defaultValue} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default double getDouble(int index, double defaultValue) throws DataException {
        return get(index, double.class, defaultValue);
    }

    /**
     * Gets the value for the specified index, then converts it to the {@link BigDecimal} type. If the key is out of
     * bounds, returns {@code null}.
     *
     * @param index the specified index
     * @return the converted BigDecimal value of the specified index, or {@code null} if the key is out of bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable BigDecimal getBigDecimal(int index) throws DataException {
        return getBigDecimal(index, null);
    }

    /**
     * Gets the value for the specified index, then converts it to the {@link BigDecimal} type. If the key is out of
     * bounds, returns {@code defaultValue}.
     *
     * @param index        the specified index
     * @param defaultValue the default value
     * @return the converted BigDecimal value of the specified index, or {@code defaultValue} if the key is out of
     * bounds
     * @throws DataException if an error occurs during the search or conversion
     */
    default @Nullable BigDecimal getBigDecimal(
        int index, @Nullable BigDecimal defaultValue
    ) throws DataException {
        return get(index, BigDecimal.class, defaultValue);
    }

    /**
     * Converts this {@link DataList} to a new list of which elements' type is the specified class.
     *
     * @param cls the specified class
     * @param <T> the type of the object to be returned`
     * @return a new list of which elements' type is the specified class
     * @throws ObjectException if an error occurs during the conversion
     */
    default <T> @Nonnull List<T> toObjectList(Class<T> cls) throws ObjectException {
        return Fs.as(toObjectList((Type) cls));
    }

    /**
     * Converts this {@link DataList} to a new list of which elements' type is the specified class.
     *
     * @param typeRef the reference of the specified class
     * @param <T>     the type of the object to be returned`
     * @return a new list of which elements' type is the specified class
     * @throws ObjectException if an error occurs during the conversion
     */
    default <T> @Nonnull List<T> toObjectList(TypeRef<T> typeRef) throws ObjectException {
        return Fs.as(toObjectList(typeRef.type()));
    }

    /**
     * Converts this {@link DataList} to a new list of which elements' type is the specified type.
     *
     * @param type the specified type
     * @return a new list of which elements' type is the specified type
     * @throws ObjectException if an error occurs during the conversion
     */
    @Nonnull
    List<Object> toObjectList(Type type) throws ObjectException;

    /**
     * Returns {@code true} if the given object is an instance of {@link DataList} and their contents are equal,
     * {@code false} otherwise.
     *
     * @param o object to be compared for equality with this
     * @return {@code true} if the given object is an instance of {@link DataList} and their contents are equal,
     * {@code false} otherwise.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns {@code true} if the content of this {@link DataList } are equal to the content of the given list,
     * {@code false} otherwise.
     *
     * @param o the given list to be compared with this
     * @return {@code true} if the content of this {@link DataList } are equal to the content of the given list,
     * {@code false} otherwise.
     */
    boolean contentEquals(@Nullable List<?> o);
}
