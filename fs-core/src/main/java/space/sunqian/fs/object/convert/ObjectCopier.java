package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.convert.handlers.CommonCopierHandler;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectProperty;
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
     * Returns the default {@link ObjectCopier}. Here are handlers in the default converter:
     * <ul>
     *     <li>{@link CommonCopierHandler#getInstance()};</li>
     * </ul>
     *
     * @return the default {@link ObjectCopier}
     * @see CommonCopierHandler
     */
    static @Nonnull ObjectCopier defaultCopier() {
        return ObjectCopierImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link ObjectCopier} with the given handlers.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectCopier} with the given handlers
     */
    static @Nonnull ObjectCopier newCopier(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newCopier(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectCopier} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectCopier} with given handlers
     */
    static @Nonnull ObjectCopier newCopier(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return newCopier(handlers, Collections.emptyList());
    }

    /**
     * Creates and returns a new {@link ObjectCopier} with given handlers and default options.
     *
     * @param handlers       given handlers
     * @param defaultOptions given default options
     * @return a new {@link ObjectCopier} with given handlers and default options
     */
    static @Nonnull ObjectCopier newCopier(
        @Nonnull @RetainedParam List<@Nonnull Handler> handlers,
        @Nonnull @RetainedParam List<@Nonnull Option<?, ?>> defaultOptions
    ) {
        return new ObjectCopierImpl(handlers, defaultOptions);
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
     * Returns all handlers of this {@link ObjectCopier}.
     *
     * @return all handlers of this {@link ObjectCopier}
     */
    @Nonnull
    @Immutable
    List<@Nonnull Handler> handlers();

    /**
     * Returns the default options of this {@link ObjectCopier}.
     *
     * @return the default options of this {@link ObjectCopier}
     */
    @Nonnull
    @Immutable
    List<@Nonnull Option<?, ?>> defaultOptions();

    /**
     * Returns a new {@link ObjectCopier} of which first handler is the given handler and the next handler is this
     * {@link ObjectCopier} as a {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newConverter(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new {@link ObjectCopier} of which first handler is the given handler and the next handler is this
     * {@link ObjectCopier} as a {@link Handler}
     */
    default @Nonnull ObjectCopier withFirstHandler(@Nonnull Handler firstHandler) {
        return newCopier(firstHandler, this.asHandler());
    }

    /**
     * Returns a new {@link ObjectCopier} of which default options are the given options.
     *
     * @param defaultOptions the default options
     * @return a new {@link ObjectCopier} of which default options are the given options
     */
    default @Nonnull ObjectCopier withDefaultOptions(@Nonnull Option<?, ?> @Nonnull ... defaultOptions) {
        return newCopier(handlers(), ListKit.list(defaultOptions));
    }

    /**
     * Returns this {@link ObjectCopier} as a {@link Handler}.
     *
     * @return this {@link ObjectCopier} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Property mapper for copying each object property to the destination object. It has 4 methods:
     * <ul>
     *     <li>
     *         {@link #copyProperty(Object, Object, Map, MapSchema, Map, MapSchema, ObjectConverter, Option[])}:
     *         from source map entry to destination map entry;
     *     </li>
     *     <li>
     *         {@link #copyProperty(Object, Object, Map, MapSchema, Object, ObjectSchema, ObjectConverter, Option[])}:
     *         from source map entry to destination object property;
     *     </li>
     *     <li>
     *         {@link #copyProperty(String, ObjectProperty, Object, ObjectSchema, Map, MapSchema, ObjectConverter, Option[])}:
     *         from source object property to destination map entry;
     *     </li>
     *     <li>
     *         {@link #copyProperty(String, ObjectProperty, Object, ObjectSchema, Map, MapSchema, ObjectConverter, Option[])}:
     *         from source object property to destination object property;
     *     </li>
     * </ul>
     * All those methods have default implementations, which directly copy properties from the source object to the
     * target object, following the rules of the specified options defined in {@link ConvertOption}.
     *
     * @author sunqian
     */
    interface Handler {

        /**
         * This method will be invoked when copy an entry from the source map to a destination map. Returns
         * {@code false} to prevent subsequent handlers to continue to copy, otherwise returns {@code true} to continue
         * to copy.
         *
         * @param srcKey    the key of the entry to be copied
         * @param srcValue  the value of the entry to be copied
         * @param srcSchema the schema of the source map
         * @param dst       the destination map
         * @param dstSchema the schema of the destination map
         * @param converter the converter used in the mapping process
         * @param options   the options used in the mapping process
         * @return whether to continue to copy
         * @throws Exception any exception can be thrown here
         */
        default boolean copyProperty(
            @Nonnull Object srcKey,
            @Nullable Object srcValue,
            @Nonnull Map<Object, Object> src,
            @Nonnull MapSchema srcSchema,
            @Nonnull Map<Object, Object> dst,
            @Nonnull MapSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception {
            if (ConvertBack.ignored(srcKey, options)) {
                return false;
            }
            if (srcValue == null && ConvertBack.ignoreNull(options)) {
                return false;
            }
            if (srcKey instanceof String) {
                srcKey = Fs.as(ConvertBack.getNameMapper(options).map((String) srcKey));
            }
            Object dstKey = converter.convert(srcKey, srcSchema.keyType(), dstSchema.keyType(), options);
            Object dstValue = converter.convert(srcValue, srcSchema.valueType(), dstSchema.valueType(), options);
            dst.put(dstKey, dstValue);
            return false;
        }

        /**
         * This method will be invoked when copy an entry from the source map to a destination object. Returns
         * {@code false} to prevent subsequent handlers to continue to copy, otherwise returns {@code true} to continue
         * to copy.
         *
         * @param srcKey    the key of the entry to be copied
         * @param srcValue  the value of the entry to be copied
         * @param srcSchema the schema of the source map
         * @param dst       the destination object
         * @param dstSchema the schema of the destination object
         * @param converter the converter used in the mapping process
         * @param options   the options used in the mapping process
         * @return whether to continue to copy
         * @throws Exception any exception can be thrown here
         */
        default boolean copyProperty(
            @Nonnull Object srcKey,
            @Nullable Object srcValue,
            @Nonnull Map<Object, Object> src,
            @Nonnull MapSchema srcSchema,
            @Nonnull Object dst,
            @Nonnull ObjectSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception {
            if (ConvertBack.ignored(srcKey, options)) {
                return false;
            }
            if (srcValue == null && ConvertBack.ignoreNull(options)) {
                return false;
            }
            if (srcKey instanceof String) {
                srcKey = ConvertBack.getNameMapper(options).map((String) srcKey);
            }
            String dstPropertyName = Fs.as(converter.convert(srcKey, srcSchema.keyType(), String.class, options));
            ObjectProperty dstProperty = dstSchema.getProperty(dstPropertyName);
            if (dstProperty == null || !dstProperty.isWritable()) {
                return false;
            }
            Object dstPropertyValue = converter.convert(srcValue, srcSchema.valueType(), dstProperty.type(), options);
            dstProperty.setValue(dst, dstPropertyValue);
            return false;
        }

        /**
         * This method will be invoked when copy a property from the source object to a destination map. Returns
         * {@code false} to prevent subsequent handlers to continue to copy, otherwise returns {@code true} to continue
         * to copy.
         *
         * @param srcPropertyName the name of the property to be copied
         * @param srcProperty     the property to be copied
         * @param src             the source object
         * @param srcSchema       the schema of the source object
         * @param dst             the destination map
         * @param dstSchema       the schema of the destination map
         * @param converter       the converter used in the mapping process
         * @param options         the options used in the mapping process
         * @return whether to continue to copy
         * @throws Exception any exception can be thrown here
         */
        default boolean copyProperty(
            @Nonnull String srcPropertyName,
            @Nonnull ObjectProperty srcProperty,
            @Nonnull Object src,
            @Nonnull ObjectSchema srcSchema,
            @Nonnull Map<Object, Object> dst,
            @Nonnull MapSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception {
            if (ConvertBack.ignored(srcProperty.name(), options)) {
                return false;
            }
            if (!srcProperty.isReadable()) {
                return false;
            }
            // do not map "class"
            if ("class".equals(srcPropertyName)) {
                return false;
            }
            String actualSrcPropertyName = ConvertBack.getNameMapper(options).map(srcPropertyName);
            Object srcPropertyValue = srcProperty.getValue(src);
            if (srcPropertyValue == null && ConvertBack.ignoreNull(options)) {
                return false;
            }
            Object dstKey = converter.convert(actualSrcPropertyName, String.class, dstSchema.keyType(), options);
            Object dstValue = converter.convert(srcPropertyValue, srcProperty.type(), dstSchema.valueType(), options);
            dst.put(dstKey, dstValue);
            return false;
        }

        /**
         * This method will be invoked when copy a property from the source object to a destination object. Returns
         * {@code false} to prevent subsequent handlers to continue to copy, otherwise returns {@code true} to continue
         * to copy.
         *
         * @param srcPropertyName the name of the property to be copied
         * @param srcProperty     the property to be copied
         * @param src             the source object
         * @param srcSchema       the schema of the source object
         * @param dst             the destination object
         * @param dstSchema       the schema of the destination object
         * @param converter       the converter used in the mapping process
         * @param options         the options used in the mapping process
         * @return whether to continue to copy
         * @throws Exception any exception can be thrown here
         */
        default boolean copyProperty(
            @Nonnull String srcPropertyName,
            @Nonnull ObjectProperty srcProperty,
            @Nonnull Object src,
            @Nonnull ObjectSchema srcSchema,
            @Nonnull Object dst,
            @Nonnull ObjectSchema dstSchema,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception {
            if (ConvertBack.ignored(srcProperty.name(), options)) {
                return false;
            }
            if (!srcProperty.isReadable()) {
                return false;
            }
            // do not map "class"
            if ("class".equals(srcPropertyName)) {
                return false;
            }
            String actualSrcPropertyName = ConvertBack.getNameMapper(options).map(srcPropertyName);
            Object srcPropertyValue = srcProperty.getValue(src);
            if (srcPropertyValue == null && ConvertBack.ignoreNull(options)) {
                return false;
            }
            String dstPropertyName = Fs.as(converter.convert(actualSrcPropertyName, String.class, String.class, options));
            ObjectProperty dstProperty = dstSchema.getProperty(dstPropertyName);
            if (dstProperty == null || !dstProperty.isWritable()) {
                return false;
            }
            Object dstPropertyValue = converter.convert(
                srcPropertyValue, srcProperty.type(), dstProperty.type(), options
            );
            dstProperty.setValue(dst, dstPropertyValue);
            return false;
        }
    }
}
