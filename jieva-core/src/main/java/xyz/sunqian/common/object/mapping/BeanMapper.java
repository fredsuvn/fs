package xyz.sunqian.common.object.mapping;

import xyz.sunqian.common.object.data.DataProperty;
import xyz.sunqian.common.object.data.DataSchema;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Mapper for {@link DataSchema}, to copy properties from source object to dest object.
 *
 * @author fredsuvn
 */
public interface BeanMapper {

    /**
     * Returns default bean mapper.
     *
     * @return default bean mapper
     */
    static BeanMapper defaultMapper() {
        return BeanMapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Copies properties from source object to dest object, then returns dest object. Supports both bean object and
     * {@link Map} object. This method is equivalent to ({@link #copyProperties(Object, Object, MappingOptions)}):
     * <pre>
     *     copyProperties(source, dest, MappingOptions.defaultOptions());
     * </pre>
     *
     * @param source source object
     * @param dest   dest object
     * @param <T>    type of dest object
     * @throws MappingException exception when copying
     */
    default <T> T copyProperties(Object source, T dest) throws MappingException {
        return copyProperties(source, dest, MappingOptions.defaultOptions());
    }

    /**
     * Copies properties from source object to dest object, then returns dest object. Supports both bean object and
     * {@link Map} object. This method is equivalent to
     * ({@link #copyProperties(Object, Type, Object, Type, MappingOptions)}):
     * <pre>
     *     copyProperties(source, source.getClass(), dest, dest.getClass(), options);
     * </pre>
     *
     * @param source  source object
     * @param dest    dest object
     * @param options mapper options
     * @param <T>     type of dest object
     * @throws MappingException exception when copying
     */
    default <T> T copyProperties(Object source, T dest, MappingOptions options) throws MappingException {
        return copyProperties(source, source.getClass(), dest, dest.getClass(), options);
    }


    /**
     * Copies properties from source object to dest object, then returns dest object. Thie method is equivalent to
     * ({@link #copyProperties(Object, Type, Object, Type, MappingOptions)}):
     * <pre>
     *     return copyProperties(source, sourceType, dest, destType, MappingOptions.defaultOptions());
     * </pre>
     *
     * @param source     source object
     * @param sourceType type of source object
     * @param dest       dest object
     * @param destType   type of dest object
     * @param <T>        type of dest object
     * @throws MappingException exception when copying
     */
    default <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) throws MappingException {
        return copyProperties(source, sourceType, dest, destType, MappingOptions.defaultOptions());
    }

    /**
     * Copies properties from source object to dest object, then returns dest object.
     * <p>
     * In default implementation:
     * <ul>
     *     <li>
     *         Supports both bean object and {@link Map} object;
     *     </li>
     *     <li>
     *         For bean object, bean info will be provided by {@link BeanProvider} first, type of properties' names
     *         is always {@link String} and types of properties' values are come from {@link DataProperty#type()};
     *     </li>
     *     <li>
     *         For {@link Map} object, types of keys and values are come from {@code sourceType} or {@code destType};
     *     </li>
     * </ul>
     *
     * @param source     source object
     * @param sourceType type of source object
     * @param dest       dest object
     * @param destType   type of dest object
     * @param options    mapper options
     * @param <T>        type of dest object
     * @throws MappingException exception when copying
     */
    <T> T copyProperties(
        Object source, Type sourceType,
        T dest, Type destType,
        MappingOptions options
    ) throws MappingException;
}
