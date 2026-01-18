package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.object.schema.DataSchema;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This interface is used to map data properties from an object to another object. The object should be a {@link Map} or
 * a non-map object which can be parsed to {@link MapSchema} and {@link ObjectSchema}.
 * <p>
 * A {@link ObjectCopier} typically uses a {@link CacheFunction} to cache the parsed {@link DataSchema}s, and the
 * thread safety is determined by the {@link CacheFunction}. By default, they are thread-safe.
 *
 * @author sunqian
 */
public interface ObjectCopier {

    /**
     * Returns the default data mapper.
     * <p>
     * The default data mapper will cache the parsed {@link DataSchema}s by a cache function based on a
     * {@link ConcurrentHashMap}, so it is thread-safe.
     *
     * @return the default data mapper
     */
    static @Nonnull ObjectCopier defaultCopier() {
        return ObjectCopierImpl.DEFAULT;
    }

    /**
     * Returns a new data mapper with the given cache function. The thread safety is determined by the given cache.
     *
     * @param cache the given cache function
     * @return a new data mapper with the given cache function
     */
    static @Nonnull ObjectCopier newCopier(@Nonnull CacheFunction<@Nonnull Type, @Nonnull DataSchema> cache) {
        return new ObjectCopierImpl(cache);
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
