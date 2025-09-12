package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.object.convert.handlers.AssignableConversionHandler;
import xyz.sunqian.common.object.convert.handlers.CommonConversionHandler;
import xyz.sunqian.common.object.convert.handlers.TypedMapperHandler;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface is used to convert an object from the specified type to the target type.
 * <p>
 * It uses a list of {@link ObjectConverter.Handler}s to sequentially attempt conversion. A handler can return
 * {@link Status#HANDLER_CONTINUE}, {@link Status#HANDLER_BREAK} or a normal value as the final result. The conversion
 * logic is as follows:
 * <pre>{@code
 * for (Handler handler : handlers()) {
 *     Object ret;
 *     try {
 *         ret = handler.convert(src, srcType, target, this, options);
 *     } catch (Exception e) {
 *         throw new ObjectConversionException(e);
 *     }
 *     if (ret == Status.HANDLER_CONTINUE) {
 *         continue;
 *     }
 *     if (ret == Status.HANDLER_BREAK) {
 *         throw new UnsupportedObjectConversionException(src, srcType, target, this, options);
 *     }
 *     return ret;
 * }
 * throw new UnsupportedObjectConversionException(src, srcType, target, this, options);
 * }</pre>
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectConverter {

    /**
     * Returns the default {@link ObjectConverter}, of which handlers are:
     * <ul>
     *     <li>{@link AssignableConversionHandler};</li>
     *     <li>{@link TypedMapperHandler};</li>
     *     <li>{@link CommonConversionHandler};</li>
     * </ul>
     *
     * @return the default converter
     */
    static ObjectConverter defaultConverter() {
        return ObjectConverterImpl.DEFAULT_MAPPER;
    }

    /**
     * Creates and returns a new {@link ObjectConverter} with the given handlers.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectConverter} with the given handlers
     */
    static @Nonnull ObjectConverter withHandlers(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return withHandlers(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectConverter} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectConverter} with given handlers
     */
    static @Nonnull ObjectConverter withHandlers(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return new ObjectConverterImpl(handlers);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        return Jie.as(convert(src, (Type) target, options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        return Jie.as(convert(src, target.type(), options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default Object convert(
        @Nullable Object src,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        return convert(src, src == null ? Object.class : src.getClass(), target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        return Jie.as(convert(src, srcType, (Type) target, options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default <T> T convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        return Jie.as(convert(src, srcType, target.type(), options));
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConversionOptions} or {@link MappingOptions} or other custom options for custom implementations.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConversionException if the conversion from the specified type to the target type is not
     *                                              supported
     * @throws ObjectConversionException            if the conversion failed
     */
    default Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConversionException, ObjectConversionException {
        for (Handler handler : handlers()) {
            Object ret;
            try {
                ret = handler.convert(src, srcType, target, this, options);
            } catch (Exception e) {
                throw new ObjectConversionException(e);
            }
            if (ret == Status.HANDLER_CONTINUE) {
                continue;
            }
            if (ret == Status.HANDLER_BREAK) {
                throw new UnsupportedObjectConversionException(src, srcType, target, this, options);
            }
            return ret;
        }
        throw new UnsupportedObjectConversionException(src, srcType, target, this, options);
    }

    /**
     * Returns all handlers of this converter.
     *
     * @return all handlers of this converter
     */
    @Nonnull
    @Immutable
    List<Handler> handlers();

    /**
     * Returns a new {@link ObjectConverter} of which handler list consists of the given handler as the first element,
     * followed by {@link #handlers()} of the current converter.
     *
     * @param handler the given handler
     * @return a new {@link ObjectConverter} of which handler list consists of the given handler as the first element,
     * followed by {@link #handlers()} of the current converter
     */
    default @Nonnull ObjectConverter withFirstHandler(Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        return withHandlers(newHandlers);
    }

    /**
     * Returns a new {@link ObjectConverter} of which handler list consists of {@link #handlers()} of the current
     * converter, followed by the given handler as the last element.
     *
     * @param handler the given handler
     * @return a {@link ObjectConverter} of which handler list consists of {@link #handlers()} of the current converter,
     * followed by the given handler as the last element
     */
    default @Nonnull ObjectConverter withLastHandler(Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return withHandlers(newHandlers);
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
     *
     * @author sunqian
     */
    @ThreadSafe
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
