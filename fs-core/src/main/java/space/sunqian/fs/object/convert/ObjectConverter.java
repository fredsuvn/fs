package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.ObjectCreatorProvider;
import space.sunqian.fs.object.convert.handlers.AssignableConvertHandler;
import space.sunqian.fs.object.convert.handlers.CommonConvertHandler;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to convert an object from the specified type to the target type.
 * <p>
 * It contains and uses a list of {@link Handler}s to sequentially attempt conversion. A handler can return
 * {@link Status#HANDLER_CONTINUE}, {@link Status#HANDLER_BREAK} or a normal value as the final result. The conversion
 * logic is as follows:
 * <pre>{@code
 * for (Handler handler : handlers()) {
 *     Object ret;
 *     try {
 *         ret = handler.convert(src, srcType, target, this, options);
 *     } catch (Exception e) {
 *         throw new ObjectConvertException(e);
 *     }
 *     if (ret == Status.HANDLER_CONTINUE) {
 *         continue;
 *     }
 *     if (ret == Status.HANDLER_BREAK) {
 *         throw new UnsupportedObjectConvertException(src, srcType, target, this, options);
 *     }
 *     return ret;
 * }
 * throw new UnsupportedObjectConvertException(src, srcType, target, this, options);
 * }</pre>
 * <p>
 * The thread safety of the methods in this interface is determined by its dependent {@link PropertiesMapper},
 * {@link ObjectCreatorProvider}, and other objects. By default, they are all thread-safe.
 *
 * @author sunqian
 */
public interface ObjectConverter {

    /**
     * Returns the default {@link ObjectConverter}, of which handlers are:
     * <ul>
     *     <li>{@link AssignableConvertHandler#INSTANCE};</li>
     *     <li>{@link CommonConvertHandler#INSTANCE};</li>
     * </ul>
     *
     * @return the default converter
     */
    static @Nonnull ObjectConverter defaultConverter() {
        return ObjectConverterImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link ObjectConverter} with the given handlers.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectConverter} with the given handlers
     */
    static @Nonnull ObjectConverter newConverter(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newConverter(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectConverter} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectConverter} with given handlers
     */
    static @Nonnull ObjectConverter newConverter(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return new ObjectConverterImpl(handlers);
    }

    /**
     * Converts the given source map from {@code Map<String, Object>} to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source map
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from {@code Map<String, Object>} to the target type
     *                                           is not supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default <T> T convertMap(
        @Nonnull Map<String, Object> src,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return Fs.as(convert(src, new TypeRef<Map<String, Object>>() {}.type(), target, options));
    }

    /**
     * Converts the given source map from {@code Map<String, Object>} to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source map
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from {@code Map<String, Object>} to the target type
     *                                           is not supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default <T> T convertMap(
        @Nonnull Map<String, Object> src,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return Fs.as(convert(src, new TypeRef<Map<String, Object>>() {}.type(), target.type(), options));
    }

    /**
     * Converts the given source map from {@code Map<String, Object>} to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source map
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from {@code Map<String, Object>} to the target type
     *                                           is not supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default Object convertMap(
        @Nonnull Map<String, Object> src,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return convert(src, new TypeRef<Map<String, Object>>() {}.type(), target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return Fs.as(convert(src, (Type) target, options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return Fs.as(convert(src, target.type(), options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default Object convert(
        @Nullable Object src,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return convert(src, src == null ? Object.class : src.getClass(), target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     */
    default Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        for (Handler handler : handlers()) {
            Object ret;
            try {
                ret = handler.convert(src, srcType, target, this, options);
            } catch (Exception e) {
                throw new ObjectConvertException(e);
            }
            if (ret == Status.HANDLER_CONTINUE) {
                continue;
            }
            if (ret == Status.HANDLER_BREAK) {
                throw new UnsupportedObjectConvertException(src, srcType, target, this, options);
            }
            return Fs.as(ret);
        }
        throw new UnsupportedObjectConvertException(src, srcType, target, this, options);
    }

    /**
     * Returns all handlers of this converter.
     *
     * @return all handlers of this converter
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new converter of which first handler is the given handler and the next handler is this converter as a
     * {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newConverter(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new converter of which first handler is the given handler and the next handler is this converter as a
     * {@link Handler}
     */
    default @Nonnull ObjectConverter withFirstHandler(@Nonnull Handler firstHandler) {
        return newConverter(firstHandler, this.asHandler());
    }

    /**
     * Returns this converter as a {@link Handler}.
     *
     * @return this converter as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectConverter}, provides the specific conversion logic.
     * <p>
     * The thread safety of the methods in this interface is determined by its dependent {@link PropertiesMapper},
     * {@link ObjectCreatorProvider}, and other objects. By default, they are all thread-safe.
     *
     * @author sunqian
     */
    interface Handler {

        /**
         * Converts the given source object to the given target type.
         *
         * @param src       the given source object
         * @param srcType   the specified type of the given source object
         * @param target    the specified type of the target object
         * @param converter the converter where this handler in
         * @param options   the other conversion options
         * @return the converted object, {@code null} is permitted, or {@link Status#HANDLER_CONTINUE} /
         * {@link Status#HANDLER_BREAK} if conversion failed
         * @throws Exception any exception can be thrown here
         */
        Object convert(
            @Nullable Object src,
            @Nonnull Type srcType,
            @Nonnull Type target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception;
    }

    /**
     * Represents the status of conversion.
     *
     * @author sunqian
     */
    enum Status {

        /**
         * This status is returned by a {@link Handler} in a conversion process, to indicate that the current handler
         * cannot convert but can continue to convert by the next handler.
         */
        HANDLER_CONTINUE,

        /**
         * This status is returned by a {@link Handler} in a conversion process, to indicate that the current handler
         * cannot convert and the conversion process needs to end in failure.
         */
        HANDLER_BREAK
    }
}
