package xyz.sunqian.common.mapping;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.objects.PropertyDef;
import xyz.sunqian.common.mapping.handlers.*;
import xyz.sunqian.common.ref.Val;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Mapper interface to map object from source type to target type. A {@link Mapper} typically has a list of
 * {@link Handler}s, and in default implementation, the {@link Handler}s provide actual map operation for core methods
 * {@link #map(Object, Type, Type, MappingOptions)} and
 * {@link #mapProperty(Object, Type, Type, PropertyDef, MappingOptions)}.
 *
 * @author fredsuvn
 * @see Handler#map(Object, Type, Type, Mapper, MappingOptions)
 * @see Handler#mapProperty(Object, Type, Type, PropertyDef, Mapper, MappingOptions)
 */
@ThreadSafe
public interface Mapper {

    /**
     * Returns default mapper with {@link MappingOptions#defaultOptions()}, and of which handlers are:
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
    static Mapper defaultMapper() {
        return MapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Returns new {@link Mapper} with given handlers.
     *
     * @param handlers given handlers
     * @return new {@link Mapper}
     */
    static Mapper newMapper(Handler... handlers) {
        return newMapper(Jie.list(handlers));
    }

    /**
     * Returns new {@link Mapper} with given handlers.
     *
     * @param handlers given handlers
     * @return new {@link Mapper}
     */
    static Mapper newMapper(Iterable<Handler> handlers) {
        return newMapper(handlers, MappingOptions.defaultOptions());
    }

    /**
     * Returns new {@link Mapper} with given handlers and default options.
     *
     * @param handlers given handlers
     * @param options  default options
     * @return new {@link Mapper}
     */
    static Mapper newMapper(Iterable<Handler> handlers, MappingOptions options) {
        return new MapperImpl(handlers, options);
    }

    /**
     * Returns actual result from {@link #map(Object, Type, Type, MappingOptions)}. The code is similar to the
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
     * @param result result from {@link #map(Object, Type, Type, MappingOptions)}
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
     * result itself is null. This method is equivalent to ({@link #map(Object, Class, MappingOptions)}):
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
     * or the result itself is null. This method is equivalent to ({@link #map(Object, TypeRef, MappingOptions)}):
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
     * the result itself is null. This method is equivalent to ({@link #map(Object, Type, MappingOptions)}):
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
     * This method is equivalent to ({@link #map(Object, Type, Type, MappingOptions)}):
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
     * in current context, may not equal to {@link PropertyDef#getType()} of target property. The result of this method
     * in 3 types:
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
     * This method is equivalent to ({@link #mapProperty(Object, Type, Type, PropertyDef, MappingOptions)}):
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
        PropertyDef targetProperty
    ) {
        return mapProperty(source, sourceType, targetType, targetProperty, getOptions());
    }

    /**
     * Maps source object from source type to target, returns null if mapping failed or the result itself is null. This
     * method is equivalent to ({@link #map(Object, Type, MappingOptions)}):
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
    default <T> T map(@Nullable Object source, Class<T> targetType, MappingOptions options) {
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
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef, MappingOptions options) {
        Object result = map(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
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
    default <T> T map(@Nullable Object source, Type targetType, MappingOptions options) {
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
     * {@link Handler#map(Object, Type, Type, Mapper, MappingOptions)} for each handler in {@link Mapper#getHandlers()}
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
    default Object map(@Nullable Object source, Type sourceType, Type targetType, MappingOptions options) {
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
     * equal to {@link PropertyDef#getType()} of target property. The result of this method in 3 types:
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
     * {@link Handler#mapProperty(Object, Type, Type, PropertyDef, Mapper, MappingOptions)} for each handler in
     * {@link Mapper#getHandlers()} sequentially. It is equivalent to:
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
        PropertyDef targetProperty,
        MappingOptions options
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

    /**
     * Returns all handlers.
     *
     * @return all handlers
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a new {@link Mapper} of which handler list consists of given handler as first element, followed by
     * {@link #getHandlers()} of current mapper.
     *
     * @param handler given handler
     * @return a new {@link Mapper} of which handler list consists of given handler as first element, followed by
     * {@link #getHandlers()} of current mapper
     */
    Mapper addFirstHandler(Handler handler);

    /**
     * Returns a new {@link Mapper} of which handler list consists of {@link #getHandlers()} of current mapper, followed
     * by given handler as last element.
     *
     * @param handler given handler
     * @return a {@link Mapper} of which handler list consists of {@link #getHandlers()} of current mapper, followed by
     * given handler as last element
     */
    Mapper addLastHandler(Handler handler);

    /**
     * Returns a new {@link Mapper} of which handler list comes from a copy of {@link #getHandlers()} of current mapper
     * but the first element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link Mapper} of which handler list comes from a copy of {@link #getHandlers()} of current mapper
     * but the first element is replaced by given handler
     */
    Mapper replaceFirstHandler(Handler handler);

    /**
     * Returns a new {@link Mapper} of which handler list comes from a copy of {@link #getHandlers()} of current mapper
     * but the last element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link Mapper} of which handler list comes from a copy of {@link #getHandlers()} of current mapper
     * but the last element is replaced by given handler
     */
    Mapper replaceLastHandler(Handler handler);

    /**
     * Returns default options of this {@link Mapper}.
     *
     * @return default options of this {@link Mapper}
     */
    MappingOptions getOptions();

    /**
     * Returns a new {@link Mapper} of which default options is replaced by given options.
     * <p>
     * Note if replaced options equals given options, return this-self.
     *
     * @param options given options
     * @return a new {@link Mapper} of which default options is replaced by given options
     */
    Mapper replaceOptions(MappingOptions options);

    /**
     * Returns this mapper as {@link Handler}.
     *
     * @return this mapper as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link Mapper} to provide map operation.
     * <p>
     * This interface also provides a default util method {@link #wrapResult(Object)}.
     *
     * @author fredsuvn
     * @see Mapper
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
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param mapper     mapper of current context.
         * @param options    mapping options
         * @return converted object
         */
        Object map(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            Mapper mapper,
            MappingOptions options
        );

        /**
         * Maps object from source type to the target type of target property. The target type is specified in current
         * context, may not equal to {@link PropertyDef#getType()} of target property. The result of this method in 4
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
         * @param source         source object
         * @param sourceType     source type
         * @param targetType     target type
         * @param targetProperty target property
         * @param mapper         mapper of current context.
         * @param options        mapping options
         * @return converted object
         */
        Object mapProperty(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            PropertyDef targetProperty,
            Mapper mapper,
            MappingOptions options
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
