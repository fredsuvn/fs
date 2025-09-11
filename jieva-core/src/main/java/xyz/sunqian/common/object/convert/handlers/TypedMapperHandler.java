package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.data.ObjectProperty;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Typed mapper handler implementation, which fast finds specified {@link Converter} for target type and convert from
 * source object.
 * <p>
 * This handler has a {@link Converter} map ({@code Map<Type, Converter<?>>}). If source object is {@code null}, return
 * {@link Flag#CONTINUE}. Else the map tries to find the converter for target type, if the converter is not found, or
 * result of {@link Converter#convert(Object, Type, ObjectProperty, ConversionOptions)} is {@code null}, return
 * {@link Flag#CONTINUE}.
 * <p>
 * The converter map should be specified in {@link #TypedMapperHandler(Map)}, or use default map
 * ({@link TypedConverters#DEFAULT_CONVERTERS}) in {@link #TypedMapperHandler()}.
 *
 * @author fredsuvn
 */
public class TypedMapperHandler implements ObjectConverter.Handler {

    private final Map<Type, Converter<?>> converters;

    /**
     * Constructs with {@link TypedConverters#DEFAULT_CONVERTERS}.
     */
    public TypedMapperHandler() {
        this(TypedConverters.DEFAULT_CONVERTERS, true);
    }

    /**
     * Constructs with specified converter map.
     *
     * @param converters specified converter map
     */
    public TypedMapperHandler(Map<Type, Converter<?>> converters) {
        this(converters, false);
    }

    private TypedMapperHandler(Map<Type, Converter<?>> converters, boolean directly) {
        this.converters = directly ? converters : Collections.unmodifiableMap(new HashMap<>(converters));
    }

    // @Override
    // public Object map(@Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
    //     return mapProperty(source, sourceType, targetType, null, objectConverter, options);
    // }
    //
    // @Override
    // public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
    //     if (source == null) {
    //         return Flag.CONTINUE;
    //     }
    //     Converter<?> converter = converters.get(targetType);
    //     if (converter == null) {
    //         return Flag.CONTINUE;
    //     }
    //     Object targetObject = converter.convert(source, sourceType, targetProperty, options);
    //     if (targetObject == null) {
    //         return Flag.CONTINUE;
    //     }
    //     return wrapResult(targetObject);
    // }

    /**
     * Returns {@link Converter} map of this handler.
     *
     * @return {@link Converter} map of this handler
     */
    public Map<Type, Converter<?>> getConverters() {
        return converters;
    }

    /**
     * Returns a new {@link TypedMapperHandler} of which converter map includes original converters from
     * {@link #getConverters()} and given more converters.
     *
     * @param moreConverters given more converters
     * @return a new {@link TypedMapperHandler} of which converter map includes original converters from
     * {@link #getConverters()} and given more converters
     */
    public TypedMapperHandler withMoreConverters(Map<Type, Converter<?>> moreConverters) {
        Map<Type, Converter<?>> newConverters = new HashMap<>(this.converters);
        newConverters.putAll(moreConverters);
        return new TypedMapperHandler(Collections.unmodifiableMap(newConverters), true);
    }

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (src == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        Converter<?> typeConverter = converters.get(target);
        if (typeConverter == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        Object targetObject = typeConverter.convert(src, srcType, null, ConversionOptions.defaultOptions2());
        if (targetObject == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        return targetObject;
    }

    /**
     * Converter to convert source object from source type to target type.
     */
    @FunctionalInterface
    public interface Converter<T> {

        /**
         * Converts and returns a new object from source type to target type, or returns {@code null} if unsupported.
         *
         * @param source     source object
         * @param sourceType source type
         * @param options    mapping options
         * @return a new object from source type to target type or null if unsupported
         */
        @Nullable
        T convert(Object source, Type sourceType, @Nullable ObjectProperty targetProperty, ConversionOptions options);
    }
}
