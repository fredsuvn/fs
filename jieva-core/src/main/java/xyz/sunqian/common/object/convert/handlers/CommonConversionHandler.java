package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.EnumKit;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.DataBuilderFactory;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.ObjectConverter;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.IntFunction;

/**
 * The common implementation of {@link ObjectConverter.Handler}, also be the default last handler of
 * {@link ObjectConverter#defaultConverter()}.
 * <p>
 * This handler providers the common conversion logic for all types. This is a table showing the conversion logic of
 * this handler for different target types:
 * <table border="1px solid">
 * <tr>
 *     <th>Target Type</th>
 *     <th>Source Type</th>
 *     <th>Conversion Logic</th>
 * </tr>
 * <tr>
 *     <td rowspan="3">{@link String}, {@link CharSequence}</td>
 *     <td>{@link InputStream}, {@link ReadableByteChannel}, {@link Reader}, {@link ByteBuffer}, {@code byte[]}</td>
 *     <td>Using {@link ConversionOptions#ioOperator(IOOperator)} and {@link ConversionOptions#charset(Charset)}
 *     to decode to string.</td>
 * </tr>
 * <tr>
 *     <td>{@code char[]}</td>
 *     <td>Using {@link String#String(char[])} to construct to string.</td>
 * </tr>
 * <tr>
 *     <td>Others</td>
 *     <td>Using {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td>Number types: {@link Number}, {@link Byte}, {@link Short}, {@link Character}, {@link Integer}, {@link Long},
 *     {@link Float}, {@link Double}, {@link BigInteger}, {@link BigDecimal}</td>
 *     <td>Others</td>
 *     <td>Using {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td>Enum types</td>
 *     <td>Any objects</td>
 *     <td>Using {@link EnumKit#findEnum(Class, String)} with the name from {@link Object#toString()}.</td>
 * </tr>
 * </table>
 */
public class CommonConversionHandler implements ObjectConverter.Handler {

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
        if (target instanceof Class<?>) {
            Class<?> classType = (Class<?>) target;
            if (classType.isEnum()) {
                // enum:
                String name = src.toString();
                return EnumKit.findEnum(Jie.as(classType), name);
            }
            if (classType.isArray()) {
                // for array
                return convertToArray(src, srcType, classType, converter, options);
            }
            // map or data object
            return convertToDataObject(src, srcType, classType, target, converter, options);
        } else if (target instanceof GenericArrayType) {
            // for generic array
            return convertToArray(src, srcType, (GenericArrayType) target, converter, options);
        } else if (target instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) target;
            Class<?> rawTarget = (Class<?>) paramType.getRawType();
            return convertToDataObject(src, srcType, rawTarget, target, converter, options);
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private Object convertToArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        return null;
    }

    private Object convertToArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull GenericArrayType target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        return null;
    }

    private Object convertToDataObject(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> rawTarget,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        IntFunction<Object> instantiator = Instantiator.get(rawTarget);
        DataMapper dataMapper = Jie.nonnull(
            Option.findValue(ConversionOptions.Key.DATA_MAPPER, options),
            DataMapper.defaultMapper()
        );
        if (instantiator != null) {
            Object targetObject = instantiator.apply(0);
            dataMapper.copyProperties(src, srcType, targetObject, target, converter, options);
            return targetObject;
        } else {
            DataBuilderFactory builderFactory = Jie.nonnull(
                Option.findValue(ConversionOptions.Key.BUILDER_FACTORY, options),
                DataBuilderFactory.defaultFactory()
            );
            Object targetBuilder = builderFactory.newBuilder(rawTarget);
            if (targetBuilder == null) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
            dataMapper.copyProperties(src, srcType, targetBuilder, target, converter, options);
            return builderFactory.build(targetBuilder);
        }
    }

    private static final class Instantiator {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Object>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Map.class, HashMap::new);
            CLASS_MAP.put(AbstractMap.class, HashMap::new);
            CLASS_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
            CLASS_MAP.put(HashMap.class, HashMap::new);
            CLASS_MAP.put(TreeMap.class, size -> new TreeMap<>());
            CLASS_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(Hashtable.class, Hashtable::new);
            CLASS_MAP.put(ConcurrentSkipListMap.class, size -> new ConcurrentSkipListMap<>());
        }

        public static @Nullable IntFunction<@Nonnull Object> get(@Nonnull Class<?> target) {
            return CLASS_MAP.get(target);
        }
    }
}
