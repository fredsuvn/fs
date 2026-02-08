package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.schema.DataSchema;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to copy data properties from an object to another object. The object should be a {@link Map}
 * or a non-map object which can be parsed to {@link MapSchema} and {@link ObjectSchema}.
 * <p>
 * A copier can have default options. The options parameter of a copy method (such as
 * {@link #copyProperties(Object, Type, Object, Type, ObjectConverter, Option[])}) will be merged with the default
 * options when the method is called.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectCopier {

    /**
     * Returns the default {@link ObjectCopier}.
     *
     * @return the default data mapper
     */
    static @Nonnull ObjectCopier defaultCopier() {
        return ObjectCopierImpl.DEFAULT;
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
     * @throws ObjectCopyException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src, @Nonnull Object dst, @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectCopyException {
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
     * @throws ObjectCopyException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Object dst,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectCopyException {
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
     * @throws ObjectCopyException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectCopyException {
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
     * @throws ObjectCopyException if an error occurs during copying properties
     */
    void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectCopyException;

    /**
     * Returns the {@link PropertyMapper} of this copier.
     *
     * @return the {@link PropertyMapper} of this copier, may be {@code null} if the default mapping logic is used
     */
    @Nullable
    PropertyMapper propertyMapper();

    /**
     * Returns the {@link ExceptionHandler} of this copier.
     *
     * @return the {@link ExceptionHandler} of this copier, may be {@code null} if the default exception handling logic
     * is used
     */
    @Nullable
    ExceptionHandler exceptionHandler();

    /**
     * Returns the default options of this {@link ObjectCopier}.
     *
     * @return the default options of this {@link ObjectCopier}
     */
    @Nonnull
    @Immutable
    List<@Nonnull Option<?, ?>> defaultOptions();

    /**
     * Returns a new {@link ObjectCopier} based on this copier but with the specified property mapper.
     *
     * @param propertyMapper the property mapper for copying each object property, may be {@code null} to use the
     *                       default mapping logic
     * @return a new {@link ObjectCopier} based on this copier but with the specified property mapper
     */
    default @Nonnull ObjectCopier withPropertyMapper(@Nullable PropertyMapper propertyMapper) {
        return new Builder()
            .propertyMapper(propertyMapper)
            .exceptionHandler(exceptionHandler())
            .defaultOptions(defaultOptions())
            .build();
    }

    /**
     * Returns a new {@link ObjectCopier} based on this copier but with the specified exception handler.
     *
     * @param exceptionHandler the exception handler for handling exceptions that occur during copying properties, may
     *                         be {@code null} to use the default exception handling logic
     * @return a new {@link ObjectCopier} based on this copier but with the specified exception handler
     */
    default @Nonnull ObjectCopier withExceptionHandler(@Nullable ExceptionHandler exceptionHandler) {
        return new Builder()
            .propertyMapper(propertyMapper())
            .exceptionHandler(exceptionHandler)
            .defaultOptions(defaultOptions())
            .build();
    }

    /**
     * Returns a new {@link ObjectCopier} of which default options are the given options.
     *
     * @param defaultOptions the default options
     * @return a new {@link ObjectCopier} of which default options are the given options
     */
    default @Nonnull ObjectCopier withDefaultOptions(@Nonnull Option<?, ?> @Nonnull ... defaultOptions) {
        return new Builder()
            .propertyMapper(propertyMapper())
            .exceptionHandler(exceptionHandler())
            .defaultOptions(ListKit.list(defaultOptions))
            .build();
    }

    /**
     * Property mapper for copying each object property.
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
         * @param srcSchema    the schema of the source object
         * @param dst          the destination object
         * @param dstSchema    the schema of the destination object
         * @param converter    the converter used in the mapping process
         * @param options      the options used in the mapping process
         * @return the mapped name and value, may be {@code null} to ignore copy of this property
         */
        Map.@Nullable Entry<@Nonnull Object, Object> map(
            @Nonnull Object propertyName,
            @Nonnull Object src,
            @Nonnull DataSchema srcSchema,
            @Nonnull Object dst,
            @Nonnull DataSchema dstSchema,
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

    /**
     * Builder for {@link ObjectCopier}.
     */
    class Builder {

        private @Nullable PropertyMapper propertyMapper;
        private @Nullable ExceptionHandler exceptionHandler;
        private @Nullable List<@Nonnull Option<?, ?>> defaultOptions;

        /**
         * Sets the {@link PropertyMapper} for the {@link ObjectCopier}.
         * <p>
         * The default {@link PropertyMapper} is {@code null}, in which case the {@link ObjectCopier} follows the given
         * options and default mapping logic.
         *
         * @param propertyMapper the given {@link PropertyMapper}
         * @return this builder
         */
        public @Nonnull Builder propertyMapper(@Nullable PropertyMapper propertyMapper) {
            this.propertyMapper = propertyMapper;
            return this;
        }

        /**
         * Sets the {@link ExceptionHandler} for the {@link ObjectCopier}.
         * <p>
         * The default {@link ExceptionHandler} is {@code null}, in which case the {@link ObjectCopier} throws the
         * exception directly.
         *
         * @param exceptionHandler the given {@link ExceptionHandler}
         * @return this builder
         */
        public @Nonnull Builder exceptionHandler(@Nullable ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Sets the default options for the {@link ObjectCopier}.
         * <p>
         * By default, it is empty.
         *
         * @param defaultOptions the default options
         * @return this builder
         */
        public @Nonnull Builder defaultOptions(@Nullable @RetainedParam List<@Nonnull Option<?, ?>> defaultOptions) {
            this.defaultOptions = defaultOptions;
            return this;
        }

        /**
         * Builds and returns a new {@link ObjectCopier} with the configured options.
         *
         * @return a new {@link ObjectCopier} with the configured options
         */
        public ObjectCopier build() {
            return new ObjectCopierImpl(
                propertyMapper,
                exceptionHandler,
                Fs.nonnull(defaultOptions, Collections.emptyList())
            );
        }
    }
}
