package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.data.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface is used to copy properties from an object to another object, The object can be a {@link Map} or a
 * non-map object which can be parsed to {@link ObjectSchema}.
 *
 * @author sunqian
 */
public interface DataMapper {

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     *
     * @param src the given source object
     * @param dst the given destination object
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(@Nonnull Object src, @Nonnull Object dst) throws ObjectConversionException {
        copyProperties(src, src.getClass(), dst, dst.getClass());
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     *
     * @param src     the given source object
     * @param srcType specifies the type of the given source object
     * @param dst     the given destination object
     * @param dstType specifies the type of the given destination object
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType
    ) throws ObjectConversionException {
        copyProperties(
            src,
            srcType,
            dst,
            dstType,
            ObjectConverter.defaultConverter(),
            ConversionOptions.defaultOptions()
        );
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     *
     * @param src       the given source object
     * @param srcType   specifies the type of the given source object
     * @param dst       the given destination object
     * @param dstType   specifies the type of the given destination object
     * @param converter the converter for converting values of the properties if needed
     * @param options   the options for copying properties
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConversionException {

    }

    /**
     * Returns an option to specify the {@link PropertyMapper}.
     * <p>
     * By default, all properties which is both readable and writable will be copied with their original names.
     *
     * @param propertyMapper the {@link PropertyMapper} to be specified
     * @return an option to specify the {@link PropertyMapper}
     */
    static @Nonnull Option<Key, @Nonnull PropertyMapper> propertyMapper(
        @Nonnull PropertyMapper propertyMapper
    ) {
        return Option.of(Key.PROPERTY_MAPPER, propertyMapper);
    }

    /**
     * Returns an option to specify the {@link ExceptionHandler}.
     * <p>
     * By default, the exception will be thrown directly.
     *
     * @param exceptionHandler the {@link ExceptionHandler} to be specified
     * @return an option to specify the {@link ExceptionHandler}
     */
    static @Nonnull Option<Key, @Nonnull ExceptionHandler> exceptionHandler(
        @Nonnull ExceptionHandler exceptionHandler
    ) {
        return Option.of(Key.EXCEPTION_HANDLER, exceptionHandler);
    }

    /**
     * Option key for copying properties.
     */
    enum Key {

        /**
         * Key of {@link #propertyMapper(PropertyMapper)}.
         */
        PROPERTY_MAPPER,

        /**
         * Key of {@link #exceptionHandler(ExceptionHandler)}.
         */
        EXCEPTION_HANDLER,
        ;
    }

    /**
     * Property mapper for copying object property, this interface is called when copying each property.
     */
    interface PropertyMapper {

        /**
         * Maps the source property with the specified name. this method determines the name and value of the actual
         * destination property that the specified property needs to be copied to. The returned entry's key and value
         * are the name and value of the actual destination property. If this method returns {@code null}, then the
         * specified property will not be copied.
         * <p>
         * This method is applicable to both {@link Map} and non-map object. For non-map objects, the type of property
         * name must be {@link String}.
         *
         * @param propertyName the name of the specified property to be copied
         * @param src          the source object
         * @param srcSchema    the schema of the source object, may be {@code null} if the source object is a
         *                     {@link Map}
         * @param dst          the destination object
         * @param dstSchema    the schema of the destination object, may be {@code null} if the destination object is a
         *                     {@link Map}
         * @param converter    the converter used in the mapping process
         * @param options      the options used in the mapping process
         * @return the mapped name and value, may be {@code null} to ignore copy of this property
         */
        Map.@Nullable Entry<@Nonnull Object, Object> map(
            @Nonnull Object propertyName,
            @Nonnull Object src,
            @Nullable ObjectSchema srcSchema,
            @Nonnull Object dst,
            @Nullable ObjectSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        );
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
         * @param srcSchema    the schema of the source object, may be {@code null} if the source object is a
         *                     {@link Map}
         * @param dst          the destination object
         * @param dstSchema    the schema of the destination object, may be {@code null} if the destination object is a
         *                     {@link Map}
         * @param converter    the converter used in the mapping process
         * @param options      the options used in the mapping process
         * @throws Exception any exception can be thrown here
         */
        void handle(
            @Nonnull Throwable e,
            @Nonnull Object propertyName,
            @Nonnull Object src,
            @Nullable ObjectSchema srcSchema,
            @Nonnull Object dst,
            @Nullable ObjectSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception;
    }
}
