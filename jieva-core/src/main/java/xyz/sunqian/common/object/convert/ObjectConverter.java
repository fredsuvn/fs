package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.object.convert.handlers.AssignableMapperHandler;
import xyz.sunqian.common.object.convert.handlers.BeanMapperHandler;
import xyz.sunqian.common.object.convert.handlers.CollectionMappingHandler;
import xyz.sunqian.common.object.convert.handlers.EnumMapperHandler;
import xyz.sunqian.common.object.convert.handlers.TypedMapperHandler;
import xyz.sunqian.common.object.data.DataProperty;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Mapper interface to map object from source type to target type. A {@link ObjectConverter} typically has a list of
 * {@link Handler}s, and in default implementation, the {@link Handler}s provide actual map operation for core methods
 * {@link #map(Object, Type, Type, ConversionOptions)} and
 * {@link #mapProperty(Object, Type, Type, DataProperty, ConversionOptions)}.
 *
 * @author fredsuvn
 * @see Handler#map(Object, Type, Type, ObjectConverter, ConversionOptions)
 * @see Handler#mapProperty(Object, Type, Type, DataProperty, ObjectConverter, ConversionOptions)
 */
@ThreadSafe
public interface ObjectConverter {

    /**
     * Returns default mapper with {@link ConversionOptions#defaultOptions2()}, and of which handlers are:
     * <ul>
     *     <li>{@link AssignableMapperHandler};</li>
     *     <li>{@link EnumMapperHandler};</li>
     *     <li>{@link TypedMapperHandler};</li>
     *     <li>{@link CollectionMappingHandler};</li>
     *     <li>{@link BeanMapperHandler};</li>
     * </ul>
     *
     * @return default converter
     */
    static ObjectConverter defaultConverter() {
        return ObjectConverterImpl.DEFAULT_MAPPER;
    }

    /**
     * Returns new {@link ObjectConverter} with given handlers.
     *
     * @param handlers given handlers
     * @return new {@link ObjectConverter}
     */
    static ObjectConverter newMapper(Handler... handlers) {
        return newMapper(Jie.list(handlers));
    }

    /**
     * Returns new {@link ObjectConverter} with given handlers.
     *
     * @param handlers given handlers
     * @return new {@link ObjectConverter}
     */
    static ObjectConverter newMapper(Iterable<Handler> handlers) {
        return newMapper(handlers, ConversionOptions.defaultOptions2());
    }

    /**
     * Returns new {@link ObjectConverter} with given handlers and default options.
     *
     * @param handlers given handlers
     * @param options  default options
     * @return new {@link ObjectConverter}
     */
    static ObjectConverter newMapper(Iterable<Handler> handlers, ConversionOptions options) {
        return new ObjectConverterImpl(handlers, options);
    }

    /**
     * Returns actual result from {@link #map(Object, Type, Type, ConversionOptions)}. The code is similar to the
     * following:
     * <pre>
     *     if (result == null) {
     *         return null;
     *     }
     *     if (result instanceof Val) {
     *         return Jie.as(((Val&lt;?&gt;) result).get());
     *     }
     *     return Jie.as(result);
     * </pre>
     *
     * @param result result from {@link #map(Object, Type, Type, ConversionOptions)}
     * @param <T>    target type
     * @return the actual result
     */
    @Nullable
    static <T> T resolveResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof Val) {
            return Jie.as(((Val<?>) result).get());
        }
        return Jie.as(result);
    }

    /**
     * Maps source object from source type to target with {@link #getOptions()}, returns null if mapping failed or the
     * result itself is null. This method is equivalent to ({@link #map(Object, Class, ConversionOptions)}):
     * <pre>
     *     return map(source, (Type) targetType, defaultOptions());
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Class<T> targetType) {
        return map(source, (Type) targetType, getOptions());
    }

    /**
     * Maps source object from source type to target type ref with {@link #getOptions()}, returns null if mapping failed
     * or the result itself is null. This method is equivalent to ({@link #map(Object, TypeRef, ConversionOptions)}):
     * <pre>
     *     return map(source, targetTypeRef, defaultOptions());
     * </pre>
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param <T>           target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef) {
        return map(source, targetTypeRef, getOptions());
    }

    /**
     * Maps source object from source type to target type with {@link #getOptions()}, returns null if mapping failed or
     * the result itself is null. This method is equivalent to ({@link #map(Object, Type, ConversionOptions)}):
     * <pre>
     *     return map(source, targetType, defaultOptions());
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Type targetType) {
        return map(source, targetType, getOptions());
    }

    /**
     * Maps source object from source type to target type with {@link #getOptions()}. The result of this method in 3
     * types:
     * <ul>
     *     <li>
     *         {@code null}: mapping failed;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is returned object;
     *     </li>
     * </ul>
     * This method is equivalent to ({@link #map(Object, Type, Type, ConversionOptions)}):
     * <pre>
     *     return map(source, sourceType, targetType, defaultOptions());
     * </pre>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @return mapped object or null
     */
    @Nullable
    default Object map(@Nullable Object source, Type sourceType, Type targetType) {
        return map(source, sourceType, targetType, getOptions());
    }


    /**
     * Maps source object from source type to target property with {@link #getOptions()}. The target type is specified
     * in current context, may not equal to {@link DataProperty#type()} of target property. The result of this method in
     * 3 types:
     * <ul>
     *     <li>
     *         {@code null}: mapping failed;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is returned object;
     *     </li>
     * </ul>
     * This method is equivalent to ({@link #mapProperty(Object, Type, Type, DataProperty, ConversionOptions)}):
     * <pre>
     *     return mapProperty(source, sourceType, targetType, targetProperty, defaultOptions());
     * </pre>
     *
     * @param source         source object
     * @param sourceType     source type
     * @param targetProperty target property
     * @param targetType     target type
     * @return mapped object or null
     */
    @Nullable
    default Object mapProperty(
        @Nullable Object source,
        Type sourceType,
        Type targetType,
        DataProperty targetProperty
    ) {
        return mapProperty(source, sourceType, targetType, targetProperty, getOptions());
    }

    /**
     * Maps source object from source type to target, returns null if mapping failed or the result itself is null. This
     * method is equivalent to ({@link #map(Object, Type, ConversionOptions)}):
     * <pre>
     *     return map(source, (Type) targetType, options);
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Class<T> targetType, ConversionOptions options) {
        return map(source, (Type) targetType, options);
    }

    /**
     * Maps source object from source type to target type ref, returns null if mapping failed or the result itself is
     * null. This method is equivalent to:
     * <pre>
     *     Object result = map(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
     *     return resolveResult(result);
     * </pre>
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param options       mapping options
     * @param <T>           target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef, ConversionOptions options) {
        Object result = map(source, source == null ? Object.class : source.getClass(), targetTypeRef.type(), options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type, returns null if mapping failed or the result itself is null.
     * This method is equivalent to:
     * <pre>
     *     Object result = map(source, source == null ? Object.class : source.getClass(), targetType, options);
     *     return resolveResult(result);
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Type targetType, ConversionOptions options) {
        Object result = map(source, source == null ? Object.class : source.getClass(), targetType, options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type. The result of this method in 3 types:
     * <ul>
     *     <li>
     *         {@code null}: mapping failed;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is returned object;
     *     </li>
     * </ul>
     * In the default implementation, this method will invoke
     * {@link Handler#map(Object, Type, Type, ObjectConverter, ConversionOptions)} for each handler in {@link ObjectConverter#getHandlers()}
     * sequentially. It is equivalent to:
     * <pre>
     *     for (Handler handler : getHandlers()) {
     *         Object value = handler.map(source, sourceType, targetType, this, options);
     *         if (value == Flag.CONTINUE) {
     *             continue;
     *         }
     *         if (value == Flag.BREAK) {
     *             return null;
     *         }
     *         return value;
     *     }
     *     return null;
     * </pre>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    mapping options
     * @return mapped object or null
     */
    @Nullable
    default Object map(@Nullable Object source, Type sourceType, Type targetType, ConversionOptions options) {
        for (Handler handler : getHandlers()) {
            Object value = handler.map(source, sourceType, targetType, this, options);
            if (value == Flag.CONTINUE) {
                continue;
            }
            if (value == Flag.BREAK) {
                return null;
            }
            return value;
        }
        return null;
    }

    /**
     * Maps source object from source type to target property. The target type is specified in current context, may not
     * equal to {@link DataProperty#type()} of target property. The result of this method in 3 types:
     * <ul>
     *     <li>
     *         {@code null}: mapping failed;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is returned object;
     *     </li>
     * </ul>
     * In the default implementation, this method will invoke
     * {@link Handler#mapProperty(Object, Type, Type, DataProperty, ObjectConverter, ConversionOptions)} for each handler in
     * {@link ObjectConverter#getHandlers()} sequentially. It is equivalent to:
     * <pre>
     *     for (Handler handler : getHandlers()) {
     *         Object value = handler.mapProperty(source, sourceType, targetType, this, options);
     *         if (value == Flag.CONTINUE) {
     *             continue;
     *         }
     *         if (value == Flag.BREAK) {
     *             return null;
     *         }
     *         return value;
     *     }
     *     return null;
     * </pre>
     *
     * @param source         source object
     * @param sourceType     source type
     * @param targetProperty target property
     * @param targetType     target type
     * @param options        mapping options
     * @return mapped object or null
     */
    @Nullable
    default Object mapProperty(
        @Nullable Object source,
        Type sourceType,
        Type targetType,
        DataProperty targetProperty,
        ConversionOptions options
    ) {
        for (Handler handler : getHandlers()) {
            Object value = handler.mapProperty(source, sourceType, targetType, targetProperty, this, options);
            if (value == Flag.CONTINUE) {
                continue;
            }
            if (value == Flag.BREAK) {
                return null;
            }
            return value;
        }
        return null;
    }

    default Object convert(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        return map(src, srcType, dstType);
    }

    /**
     * Returns all handlers.
     *
     * @return all handlers
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a new {@link ObjectConverter} of which handler list consists of given handler as first element, followed
     * by {@link #getHandlers()} of current mapper.
     *
     * @param handler given handler
     * @return a new {@link ObjectConverter} of which handler list consists of given handler as first element, followed
     * by {@link #getHandlers()} of current mapper
     */
    ObjectConverter addFirstHandler(Handler handler);

    /**
     * Returns a new {@link ObjectConverter} of which handler list consists of {@link #getHandlers()} of current mapper,
     * followed by given handler as last element.
     *
     * @param handler given handler
     * @return a {@link ObjectConverter} of which handler list consists of {@link #getHandlers()} of current mapper,
     * followed by given handler as last element
     */
    ObjectConverter addLastHandler(Handler handler);

    /**
     * Returns a new {@link ObjectConverter} of which handler list comes from a copy of {@link #getHandlers()} of
     * current mapper but the first element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link ObjectConverter} of which handler list comes from a copy of {@link #getHandlers()} of
     * current mapper but the first element is replaced by given handler
     */
    ObjectConverter replaceFirstHandler(Handler handler);

    /**
     * Returns a new {@link ObjectConverter} of which handler list comes from a copy of {@link #getHandlers()} of
     * current mapper but the last element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link ObjectConverter} of which handler list comes from a copy of {@link #getHandlers()} of
     * current mapper but the last element is replaced by given handler
     */
    ObjectConverter replaceLastHandler(Handler handler);

    /**
     * Returns default options of this {@link ObjectConverter}.
     *
     * @return default options of this {@link ObjectConverter}
     */
    ConversionOptions getOptions();

    /**
     * Returns a new {@link ObjectConverter} of which default options is replaced by given options.
     * <p>
     * Note if replaced options equals given options, return this-self.
     *
     * @param options given options
     * @return a new {@link ObjectConverter} of which default options is replaced by given options
     */
    ObjectConverter replaceOptions(ConversionOptions options);

    /**
     * Returns this mapper as {@link Handler}.
     *
     * @return this mapper as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link ObjectConverter} to provide map operation.
     * <p>
     * This interface also provides a default util method {@link #wrapResult(Object)}.
     *
     * @author fredsuvn
     * @see ObjectConverter
     */
    @ThreadSafe
    interface Handler {

        /**
         * Maps object from source type to target type. The result of this method in 4 types:
         * <ul>
         *     <li>
         *         {@link Flag#CONTINUE}: mapping failed, hands off to next handler;
         *     </li>
         *     <li>
         *         {@link Flag#BREAK}: mapping failed, breaks the handler chain;
         *     </li>
         *     <li>
         *         {@link Val}: mapping successful, the result is {@link Val#get()};
         *     </li>
         *     <li>
         *         {@code others}: mapping successful, the result is returned object;
         *     </li>
         * </ul>
         *
         * @param source          source object
         * @param sourceType      source type
         * @param targetType      target type
         * @param objectConverter mapper of current context.
         * @param options         mapping options
         * @return converted object
         */
        Object map(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            ObjectConverter objectConverter,
            ConversionOptions options
        );

        /**
         * Maps object from source type to the target type of target property. The target type is specified in current
         * context, may not equal to {@link DataProperty#type()} of target property. The result of this method in 4
         * types:
         * <ul>
         *     <li>
         *         {@link Flag#CONTINUE}: mapping failed, hands off to next handler;
         *     </li>
         *     <li>
         *         {@link Flag#BREAK}: mapping failed, breaks the handler chain;
         *     </li>
         *     <li>
         *         {@link Val}: mapping successful, the result is {@link Val#get()};
         *     </li>
         *     <li>
         *         {@code others}: mapping successful, the result is returned object;
         *     </li>
         * </ul>
         *
         * @param source          source object
         * @param sourceType      source type
         * @param targetType      target type
         * @param targetProperty  target property
         * @param objectConverter mapper of current context.
         * @param options         mapping options
         * @return converted object
         */
        Object mapProperty(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            DataProperty targetProperty,
            ObjectConverter objectConverter,
            ConversionOptions options
        );


        /**
         * This method is used to help wrap the actual result of this handler. The code is similar to the following:
         * <pre>
         *     if (result == null) {
         *         return Val.ofNull();
         *     }
         *     if ((result instanceof Flag) || (result instanceof Val)) {
         *         return Val.of(result);
         *     }
         *     return result;
         * </pre>
         *
         * @param result actual result of this handler
         * @return wrapped result
         */
        default Object wrapResult(@Nullable Object result) {
            if (result == null) {
                return Val.ofNull();
            }
            if ((result instanceof Flag) || (result instanceof Val)) {
                return Val.of(result);
            }
            return result;
        }
    }
}
