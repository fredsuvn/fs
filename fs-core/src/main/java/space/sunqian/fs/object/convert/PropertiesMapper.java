package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.data.DataSchema;
import space.sunqian.fs.object.data.MapSchema;
import space.sunqian.fs.object.data.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This interface is used to map data properties from an object to another object. The object should be a {@link Map} or
 * a non-map object which can be parsed to {@link MapSchema} and {@link ObjectSchema}.
 * <p>
 * A {@link PropertiesMapper} typically uses a {@link SchemaCache} to cache the parsed {@link DataSchema}s, and the
 * thread safety is determined by the {@link SchemaCache}. By default, they are thread-safe.
 *
 * @author sunqian
 */
public interface PropertiesMapper {

    /**
     * Returns the default data mapper.
     * <p>
     * The default data mapper will cache the {@link DataSchema}s parsed into a {@link ConcurrentHashMap} if needed, so
     * it is thread-safe.
     *
     * @return the default data mapper
     */
    static @Nonnull PropertiesMapper defaultMapper() {
        return PropertiesMapperImpl.DEFAULT;
    }

    /**
     * Returns a new data mapper with the given schema cache. The thread safety is determined by the given cache.
     *
     * @param schemaCache the given schema cache
     * @return a new data mapper with the given schema cache
     */
    static @Nonnull PropertiesMapper newMapper(@Nonnull SchemaCache schemaCache) {
        return new PropertiesMapperImpl(schemaCache);
    }

    /**
     * Returns a new data mapper with the given map as schema cache. The thread safety is determined by the given map.
     *
     * @param map the given map as schema cache
     * @return a new data mapper with the given map as schema cache
     */
    static @Nonnull PropertiesMapper newMapper(@Nonnull Map<@Nonnull Type, @Nonnull DataSchema> map) {
        return newMapper(newSchemaCache(map));
    }

    /**
     * Returns a new data schema cache with the given map. The thread safety is determined by the given map.
     *
     * @param map the given map
     * @return a new data schema cache with the given map
     */
    static @Nonnull SchemaCache newSchemaCache(@Nonnull Map<@Nonnull Type, @Nonnull DataSchema> map) {
        return new PropertiesMapperImpl.SchemaCacheImpl(map);
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param dst     the given destination object
     * @param options the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src, @Nonnull Object dst, @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        copyProperties(src, src.getClass(), dst, dst.getClass(), options);
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src       the given source object
     * @param dst       the given destination object
     * @param converter the converter for converting values of the properties if needed
     * @param options   the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Object dst,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        copyProperties(src, src.getClass(), dst, dst.getClass(), converter, options);
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param srcType specifies the type of the given source object
     * @param dst     the given destination object
     * @param dstType specifies the type of the given destination object
     * @param options the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        copyProperties(
            src,
            srcType,
            dst,
            dstType,
            ObjectConverter.defaultConverter(),
            options
        );
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src       the given source object
     * @param srcType   specifies the type of the given source object
     * @param dst       the given destination object
     * @param dstType   specifies the type of the given destination object
     * @param converter the converter for converting values of the properties if needed
     * @param options   the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     */
    void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException;

    /**
     * Cache for {@link DataSchema}s parsed during the mapping process.
     */
    interface SchemaCache {

        /**
         * Returns the {@link DataSchema} for the given type. If the schema is not cached, it will be loaded by the
         * given loader. The semantics of this method are the same as {@link Map#computeIfAbsent(Object, Function)}.
         *
         * @param type   the given type to be parsed to {@link DataSchema}
         * @param loader the loader for loading new {@link DataSchema}
         * @return the {@link DataSchema} for the given type
         * @throws ObjectConvertException if an error occurs during parsing
         */
        @Nonnull
        DataSchema get(
            @Nonnull Type type,
            @Nonnull Function<? super @Nonnull Type, ? extends @Nonnull DataSchema> loader
        ) throws ObjectConvertException;
    }

    /**
     * Exception handler for copying object property, used to handle exceptions thrown when copying a property.
     */
    interface ExceptionHandler {

        /**
         * Handles the exception thrown when copying a source property to a destination property, can throw an exception
         * directly here.
         * <p>
         * This method is applicable to both {@link Map} and non-map object. For non-map objects, the type of property
         * name must be {@link String}.
         *
         * @param e            the exception thrown when copying a source property to a destination property
         * @param propertyName the name of the specified property to be copied
         * @param src          the source object
         * @param srcSchema    the schema of the source object
         * @param dst          the destination object
         * @param dstSchema    the schema of the destination object
         * @param converter    the converter used in the mapping process
         * @param options      the options used in the mapping process
         * @throws Exception any exception can be thrown here
         */
        void handle(
            @Nonnull Throwable e,
            @Nonnull Object propertyName,
            @Nonnull Object src,
            @Nonnull DataSchema srcSchema,
            @Nonnull Object dst,
            @Nonnull DataSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception;
    }
}
