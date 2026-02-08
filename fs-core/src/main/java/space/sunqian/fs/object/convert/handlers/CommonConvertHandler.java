package space.sunqian.fs.object.convert.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.lang.EnumKit;
import space.sunqian.fs.base.number.NumKit;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.collect.ArrayOperator;
import space.sunqian.fs.collect.CollectKit;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.PropertyCopier;
import space.sunqian.fs.object.create.CreatorProvider;
import space.sunqian.fs.object.create.ObjectCreator;
import space.sunqian.fs.reflect.ReflectionException;
import space.sunqian.fs.reflect.TypeKit;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

/**
 * The common implementation of {@link ObjectConverter.Handler}, also be the default last handler of
 * {@link ObjectConverter#defaultConverter()}. Using {@link #getInstance()} can get a same one instance of this
 * handler.
 * <p>
 * This handler providers the common conversion logic for all types. This is a table showing the conversion logic of
 * this handler for different target types:
 * <table summary="Conversion Logic">
 * <tr>
 *     <th>Target</th>
 *     <th>Source</th>
 *     <th>Conversion Logic</th>
 * </tr>
 * <tr>
 *     <td rowspan="5">{@link String}, {@link CharSequence}</td>
 *     <td>{@link InputStream}, {@link ReadableByteChannel}, {@link Reader}, {@link ByteBuffer}, {@code byte[]}</td>
 *     <td>Using {@link ConvertOption#ioOperator(IOOperator)} and {@link ConvertOption#charset(Charset)}
 *     to decode to string.</td>
 * </tr>
 * <tr>
 *     <td>{@code char[]}</td>
 *     <td>Using {@link String#String(char[])} to construct to string.</td>
 * </tr>
 * <tr>
 *     <td>{@link BigDecimal}</td>
 *     <td>Using {@link BigDecimal#toPlainString()}.</td>
 * </tr>
 * <tr>
 *     <td>Date and Time Objects</td>
 *     <td>Using {@link ConvertOption#dateFormatter(DateFormatter)} to handle.</td>
 * </tr>
 * <tr>
 *     <td>Others</td>
 *     <td>Using {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td>{@code byte[]}, {@code char[]}, {@link ByteBuffer}, {@link CharBuffer}</td>
 *     <td>{@link String}</td>
 *     <td>Using {@link String#getBytes(Charset)} or {@link String#toCharArray()}.</td>
 * </tr>
 * <tr>
 *     <td rowspan="2">Numbers</td>
 *     <td>{@link String}</td>
 *     <td>Using {@link NumKit#toNumber(CharSequence, Class)}.</td>
 * </tr>
 * <tr>
 *     <td>Other Numbers</td>
 *     <td>Using {@link NumKit#toNumber(Number, Class)}.</td>
 * </tr>
 * <tr>
 *     <td>{@code long} and {@link Long}</td>
 *     <td>Date and Time</td>
 *     <td>Returns epoch milliseconds of the date or time object.</td>
 * </tr>
 * <tr>
 *     <td rowspan="2">{@code boolean} and {@link Boolean}</td>
 *     <td>Numbers</td>
 *     <td>{@code false} for {@code 0}, otherwise {@code true}.</td>
 * </tr>
 * <tr>
 *     <td>{@link String}</td>
 *     <td>{@code true} for {@code equalsIgnoreCase("true")}, otherwise {@code false}.</td>
 * </tr>
 * <tr>
 *     <td rowspan="2">Date and Time</td>
 *     <td>{@link String} and Other Date Time Objects</td>
 *     <td>Using {@link ConvertOption#dateFormatter(DateFormatter)} to handle.</td>
 * </tr>
 * <tr>
 *     <td>{@code long} and {@link Long}</td>
 *     <td>Treated as an epoch milliseconds, then using {@link ConvertOption#dateFormatter(DateFormatter)} to
 *     handle.</td>
 * </tr>
 * <tr>
 *     <td>Enums</td>
 *     <td>Any Objects</td>
 *     <td>Using {@link EnumKit#findEnum(Class, String)} with the name from {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td>Array and Collection Objects</td>
 *     <td>Array or Iterable Objects</td>
 *     <td>Array created using reflection. Collection created using its constructor, the supported collection types:
 *     {@link Iterable}, {@link Collection}, {@link List}, {@link AbstractList}, {@link ArrayList}, {@link LinkedList},
 *     {@link CopyOnWriteArrayList}, {@link Set}, {@link LinkedHashSet}, {@link HashSet}, {@link TreeSet},
 *     {@link ConcurrentSkipListSet}. After creating the container, uses the {@code converter} parameter to handle
 *     component types.
 *     </td>
 * </tr>
 * <tr>
 *     <td>Map and Data Objects</td>
 *     <td>Any Objects</td>
 *     <td>Generating data object is based on {@link ConvertOption#creatorProvider(CreatorProvider)} and
 *     {@link ConvertOption#propertyCopier(PropertyCopier)}. Generating map using its constructor, and copying properties
 *     also using {@link ConvertOption#propertyCopier(PropertyCopier)}. The supported map types:
 *     {@link Map}, {@link AbstractMap}, {@link LinkedHashMap}, {@link HashMap}, {@link TreeMap}, {@link ConcurrentMap},
 *     {@link ConcurrentHashMap}, {@link Hashtable}, {@link ConcurrentSkipListMap}.
 *     </td>
 * </tr>
 * </table>
 * <p>
 * Note that this handler typically creates new objects for each conversion, and does not perform the same handing as
 * {@link AssignableConvertHandler}.
 */
public class CommonConvertHandler implements ObjectConverter.Handler {

    private static final @Nonnull CommonConvertHandler INST = new CommonConvertHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonConvertHandler getInstance() {
        return INST;
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
        if (target instanceof Class<?>) {
            Class<?> targetClass = (Class<?>) target;
            if (targetClass.isEnum()) {
                // to enum:
                String name = src.toString();
                return EnumKit.findEnum(Fs.as(targetClass), name);
            }
            if (srcType.equals(String.class)) {
                if (target.equals(byte[].class) || target.equals(ByteBuffer.class)) {
                    Charset charset = Fs.nonnull(
                        OptionKit.findValue(ConvertOption.CHARSET, options),
                        CharsKit.defaultCharset()
                    );
                    byte[] bytes = ((String) src).getBytes(charset);
                    return target.equals(ByteBuffer.class) ? ByteBuffer.wrap(bytes) : bytes;
                }
                if (target.equals(char[].class)) {
                    return ((String) src).toCharArray();
                }
                if (target.equals(CharBuffer.class)) {
                    return CharBuffer.wrap((String) src);
                }
            }
            if (targetClass.isArray()) {
                // to array
                return toArray(src, srcType, targetClass, converter, options);
            }
            ClassHandler classHandler = TargetClasses.get(targetClass);
            if (classHandler != null) {
                return classHandler.convert(src, srcType, targetClass, converter, options);
            }
            IntFunction<Collection<Object>> collectionFunc = CollectionClasses.get(targetClass);
            if (collectionFunc != null) {
                // to collection
                return toCollection(
                    src, srcType, collectionFunc, targetClass.getTypeParameters()[0], converter, options
                );
            }
            // to map or data object
            return toDataObject(src, srcType, targetClass, target, converter, options);
        } else if (target instanceof GenericArrayType) {
            // to generic array
            return toArray(src, srcType, (GenericArrayType) target, converter, options);
        } else if (target instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) target;
            Class<?> rawTarget = (Class<?>) paramType.getRawType();
            IntFunction<Collection<Object>> collectionFunc = CollectionClasses.get(rawTarget);
            if (collectionFunc != null) {
                // to collection
                return toCollection(
                    src, srcType, collectionFunc, paramType.getActualTypeArguments()[0], converter, options
                );
            }
            // to map or data object
            return toDataObject(src, srcType, rawTarget, target, converter, options);
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private Object toArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        return toArray(
            src,
            srcType,
            target,
            target.getComponentType(),
            converter,
            options
        );
    }

    private Object toArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull GenericArrayType target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        Class<?> targetClass = Fs.asNonnull(TypeKit.toRuntimeClass(target));
        return toArray(
            src,
            srcType,
            targetClass,
            target.getGenericComponentType(),
            converter,
            options
        );
    }

    private Object toArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> target,
        @Nonnull Type targetComponentType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        if (srcType instanceof Class<?>) {
            Class<?> srcClass = (Class<?>) srcType;
            if (srcClass.isArray()) {
                ArrayOperator srcOperator = ArrayOperator.of(srcClass);
                int size = srcOperator.size(src);
                Object newArray = ArrayKit.newArray(target.getComponentType(), size);
                Class<?> srcComponentType = srcClass.getComponentType();
                ArrayOperator targetOperator = ArrayOperator.of(target);
                for (int i = 0; i < size; i++) {
                    Object srcElement = srcOperator.get(src, i);
                    Object targetElement = converter.convert(srcElement, srcComponentType, targetComponentType, options);
                    targetOperator.set(newArray, i, targetElement);
                }
                return newArray;
            }
        }
        Type srcComponentType = resolveComponentType(srcType);
        if (srcComponentType == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        Collection<?> srcCollection;
        if (src instanceof Collection<?>) {
            srcCollection = (Collection<?>) src;
        } else if (src instanceof Iterable<?>) {
            Iterable<?> iter = (Iterable<?>) src;
            srcCollection = CollectKit.addAll(new ArrayList<>(), iter);
        } else {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        int size = srcCollection.size();
        Object newArray = ArrayKit.newArray(target.getComponentType(), size);
        ArrayOperator targetOperator = ArrayOperator.of(target);
        int i = 0;
        for (Object srcElement : srcCollection) {
            Object targetElement = converter.convert(srcElement, srcComponentType, targetComponentType, options);
            targetOperator.set(newArray, i++, targetElement);
        }
        return newArray;
    }

    private Object toCollection(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull IntFunction<Collection<Object>> collectionFunc,
        @Nonnull Type targetComponentType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        if (srcType instanceof Class<?>) {
            Class<?> srcClass = (Class<?>) srcType;
            if (srcClass.isArray()) {
                ArrayOperator srcOperator = ArrayOperator.of(srcClass);
                int size = srcOperator.size(src);
                Collection<Object> newCollection = collectionFunc.apply(size);
                Class<?> srcComponentType = srcClass.getComponentType();
                for (int i = 0; i < size; i++) {
                    Object srcElement = srcOperator.get(src, i);
                    Object targetElement = converter.convert(srcElement, srcComponentType, targetComponentType, options);
                    newCollection.add(targetElement);
                }
                return newCollection;
            }
        }
        Type srcComponentType = resolveComponentType(srcType);
        if (srcComponentType == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        Collection<?> srcCollection;
        if (src instanceof Collection<?>) {
            srcCollection = (Collection<?>) src;
        } else if (src instanceof Iterable<?>) {
            Iterable<?> iter = (Iterable<?>) src;
            srcCollection = CollectKit.addAll(new ArrayList<>(), iter);
        } else {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        int size = srcCollection.size();
        Collection<Object> newCollection = collectionFunc.apply(size);
        int i = 0;
        for (Object srcElement : srcCollection) {
            Object targetElement = converter.convert(srcElement, srcComponentType, targetComponentType, options);
            newCollection.add(targetElement);
        }
        return newCollection;
    }

    private @Nullable Type resolveComponentType(@Nonnull Type type) {
        try {
            return TypeKit.resolveActualTypeArguments(type, Iterable.class).get(0);
        } catch (ReflectionException e) {
            return null;
        }
    }

    private Object toDataObject(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> rawTarget,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        IntFunction<Object> mapFunc = MapClasses.get(rawTarget);
        PropertyCopier propertyCopier = Fs.nonnull(
            OptionKit.findValue(ConvertOption.PROPERTY_COPIER, options),
            PropertyCopier.defaultCopier()
        );
        if (mapFunc != null) {
            Object targetObject = mapFunc.apply(0);
            propertyCopier.copyProperties(src, srcType, targetObject, target, converter, options);
            return targetObject;
        } else {
            CreatorProvider creatorProvider = ConvertKit.creatorProvider(options);
            ObjectCreator creator = creatorProvider.forType(target);
            if (creator == null) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
            Object targetBuilder = creator.createBuilder();
            propertyCopier.copyProperties(src, srcType, targetBuilder, creator.builderType(), converter, options);
            return creator.buildTarget(targetBuilder);
        }
    }

    private static final class CollectionClasses {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Collection<Object>>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Iterable.class, ArrayList::new);
            CLASS_MAP.put(Collection.class, HashSet::new);
            CLASS_MAP.put(List.class, ArrayList::new);
            CLASS_MAP.put(AbstractList.class, ArrayList::new);
            CLASS_MAP.put(ArrayList.class, ArrayList::new);
            CLASS_MAP.put(LinkedList.class, size -> new LinkedList<>());
            CLASS_MAP.put(CopyOnWriteArrayList.class, size -> new CopyOnWriteArrayList<>());
            CLASS_MAP.put(Set.class, HashSet::new);
            CLASS_MAP.put(LinkedHashSet.class, LinkedHashSet::new);
            CLASS_MAP.put(HashSet.class, HashSet::new);
            CLASS_MAP.put(TreeSet.class, size -> new TreeSet<>());
            CLASS_MAP.put(ConcurrentSkipListSet.class, size -> new ConcurrentSkipListSet<>());
        }

        public static @Nullable IntFunction<@Nonnull Collection<Object>> get(@Nonnull Class<?> target) {
            return CLASS_MAP.get(target);
        }
    }

    private static final class MapClasses {

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

    private interface ClassHandler {
        Object convert(
            @Nonnull Object src,
            @Nonnull Type srcType,
            @Nonnull Class<?> target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception;
    }

    private static final class TargetClasses {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull ClassHandler> HANDLER_MAP;

        static {
            HANDLER_MAP = new HashMap<>();
            HANDLER_MAP.put(String.class, StringClassHandler.INST);
            HANDLER_MAP.put(CharSequence.class, StringClassHandler.INST);
            HANDLER_MAP.put(boolean.class, BooleanClassHandler.INST);
            HANDLER_MAP.put(Boolean.class, BooleanClassHandler.INST);
            HANDLER_MAP.put(byte.class, NumberClassHandler.INST);
            HANDLER_MAP.put(short.class, NumberClassHandler.INST);
            HANDLER_MAP.put(char.class, NumberClassHandler.INST);
            HANDLER_MAP.put(int.class, NumberClassHandler.INST);
            HANDLER_MAP.put(long.class, NumberClassHandler.INST);
            HANDLER_MAP.put(float.class, NumberClassHandler.INST);
            HANDLER_MAP.put(double.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Byte.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Short.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Character.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Integer.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Long.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Float.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Double.class, NumberClassHandler.INST);
            HANDLER_MAP.put(BigInteger.class, NumberClassHandler.INST);
            HANDLER_MAP.put(BigDecimal.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Number.class, NumberClassHandler.INST);
            HANDLER_MAP.put(Date.class, DateClassHandler.INST);
            HANDLER_MAP.put(Instant.class, DateClassHandler.INST);
            HANDLER_MAP.put(LocalDateTime.class, DateClassHandler.INST);
            HANDLER_MAP.put(ZonedDateTime.class, DateClassHandler.INST);
            HANDLER_MAP.put(OffsetDateTime.class, DateClassHandler.INST);
            HANDLER_MAP.put(LocalDate.class, DateClassHandler.INST);
            HANDLER_MAP.put(LocalTime.class, DateClassHandler.INST);
        }

        public static @Nullable CommonConvertHandler.ClassHandler get(@Nonnull Class<?> target) {
            return HANDLER_MAP.get(target);
        }
    }

    private enum StringClassHandler implements ClassHandler {

        INST;

        @Override
        public Object convert(
            @Nonnull Object src,
            @Nonnull Type srcType,
            @Nonnull Class<?> target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws Exception {
            if (src instanceof char[]) {
                return new String((char[]) src);
            }
            if (src instanceof BigDecimal) {
                return ((BigDecimal) src).toPlainString();
            }
            if (src instanceof Date) {
                DateFormatter dateFormatter = Fs.nonnull(
                    OptionKit.findValue(ConvertOption.DATE_FORMATTER, options),
                    DateFormatter.defaultFormatter()
                );
                return dateFormatter.format((Date) src);
            }
            if (src instanceof TemporalAccessor) {
                DateFormatter dateFormatter = Fs.nonnull(
                    OptionKit.findValue(ConvertOption.DATE_FORMATTER, options),
                    DateFormatter.defaultFormatter()
                );
                return dateFormatter.format((TemporalAccessor) src);
            }
            Charset charset = Fs.nonnull(
                OptionKit.findValue(ConvertOption.CHARSET, options),
                CharsKit.defaultCharset()
            );
            if (src instanceof byte[]) {
                return new String((byte[]) src, charset);
            }
            if (src instanceof ByteBuffer) {
                return BufferKit.string((ByteBuffer) src, charset);
            }
            IOOperator ioOperator = Fs.nonnull(
                OptionKit.findValue(ConvertOption.IO_OPERATOR, options),
                IOOperator.defaultOperator()
            );
            if (src instanceof Reader) {
                return ioOperator.string((Reader) src);
            }
            if (src instanceof InputStream) {
                return ioOperator.string((InputStream) src, charset);
            }
            if (src instanceof ReadableByteChannel) {
                return ioOperator.string((ReadableByteChannel) src, charset);
            }
            return src.toString();
        }
    }

    private enum NumberClassHandler implements ClassHandler {

        INST;

        @Override
        public Object convert(
            @Nonnull Object src,
            @Nonnull Type srcType,
            @Nonnull Class<?> target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) {
            if (src instanceof String) {
                return NumKit.toNumber((String) src, target);
            }
            // date to long
            if (target.equals(Long.class) || target.equals(long.class)) {
                if (src instanceof Date) {
                    return ((Date) src).getTime();
                }
                if (src instanceof TemporalAccessor) {
                    TemporalAccessor ta = (TemporalAccessor) src;
                    DateFormatter dateFormatter = Fs.nonnull(
                        OptionKit.findValue(ConvertOption.DATE_FORMATTER, options),
                        DateFormatter.defaultFormatter()
                    );
                    Date date = dateFormatter.convert(ta, Date.class);
                    return date.getTime();
                }
            }
            if (src instanceof Number) {
                Number srcNum = (Number) src;
                return NumKit.toNumber(srcNum, target);
            }
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
    }

    private enum BooleanClassHandler implements ClassHandler {

        INST;

        @Override
        public Object convert(
            @Nonnull Object src,
            @Nonnull Type srcType,
            @Nonnull Class<?> target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) {
            if (src instanceof Boolean) {
                return src;
            }
            if (src instanceof Number) {
                return ((Number) src).intValue() != 0;
            }
            if (src instanceof String) {
                return "true".equalsIgnoreCase((String) src);
            }
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
    }

    private enum DateClassHandler implements ClassHandler {

        INST;

        @Override
        public Object convert(
            @Nonnull Object src,
            @Nonnull Type srcType,
            @Nonnull Class<?> target,
            @Nonnull ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) {
            DateFormatter dateFormatter = Fs.nonnull(
                OptionKit.findValue(ConvertOption.DATE_FORMATTER, options),
                DateFormatter.defaultFormatter()
            );
            if (src instanceof String) {
                return dateFormatter.parse((String) src, target);
            }
            if (src instanceof Date) {
                return dateFormatter.convert((Date) src, target);
            }
            if (src instanceof Long) {
                Date date = new Date((Long) src);
                return dateFormatter.convert(date, target);
            }
            if (src instanceof TemporalAccessor) {
                return dateFormatter.convert((TemporalAccessor) src, target);
            }
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
    }
}
