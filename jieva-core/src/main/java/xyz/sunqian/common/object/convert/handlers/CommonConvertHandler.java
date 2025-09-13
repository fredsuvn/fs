package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.EnumKit;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.base.string.StringKit;
import xyz.sunqian.common.base.time.TimeFormatter;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.object.convert.ConvertOption;
import xyz.sunqian.common.object.convert.DataBuilderFactory;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.reflect.ReflectionException;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
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
 * {@link ObjectConverter#defaultConverter()}.
 * <p>
 * This handler providers the common conversion logic for all types. This is a table showing the conversion logic of
 * this handler for different target types:
 * <table border="1px solid">
 * <tr>
 *     <th width="20%">Target</th>
 *     <th width="20%">Source</th>
 *     <th width="60%">Conversion Logic</th>
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
 *     <td>Using {@link ConvertOption#timeFormatter(TimeFormatter)} to handle.</td>
 * </tr>
 * <tr>
 *     <td>Others</td>
 *     <td>Using {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td>Numbers</td>
 *     <td>Any Objects</td>
 *     <td>Using {@link StringKit#toNumber(CharSequence, Class)} with the string from {@link Object#toString()}.</td>
 * </tr>
 * <tr>
 *     <td rowspan="2">Date and Time</td>
 *     <td>{@link String} and Other Date Time Objects</td>
 *     <td>Using {@link ConvertOption#timeFormatter(TimeFormatter)} to handle.</td>
 * </tr>
 * <tr>
 *     <td>{@code long} and {@link Long}</td>
 *     <td>Treated as an epoch milliseconds, then using {@link ConvertOption#timeFormatter(TimeFormatter)} to
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
 *     <td>Generating data object is based on {@link ConvertOption#builderFactory(DataBuilderFactory)} and
 *     {@link ConvertOption#dataMapper(DataMapper)}. Generating map using its constructor, and copying properties
 *     also using {@link ConvertOption#dataMapper(DataMapper)}. The supported map types:
 *     {@link Map}, {@link AbstractMap}, {@link LinkedHashMap}, {@link HashMap}, {@link TreeMap}, {@link ConcurrentMap},
 *     {@link ConcurrentHashMap}, {@link Hashtable}, {@link ConcurrentSkipListMap}.
 *     </td>
 * </tr>
 * </table>
 */
public class CommonConvertHandler implements ObjectConverter.Handler {

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
                // to enum:
                String name = src.toString();
                return EnumKit.findEnum(Jie.as(classType), name);
            }
            if (classType.isArray()) {
                // to array
                return convertToArray(src, srcType, classType, converter, options);
            }
            IntFunction<Collection<Object>> collectionFunc = CollectionGenerator.get(classType);
            if (collectionFunc != null) {
                // to collection
                return convertToCollection(
                    src, srcType, collectionFunc, classType.getTypeParameters()[0], converter, options
                );
            }
            // to map or data object
            return convertToDataObject(src, srcType, classType, target, converter, options);
        } else if (target instanceof GenericArrayType) {
            // to generic array
            return convertToArray(src, srcType, (GenericArrayType) target, converter, options);
        } else if (target instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) target;
            Class<?> rawTarget = (Class<?>) paramType.getRawType();
            IntFunction<Collection<Object>> collectionFunc = CollectionGenerator.get(rawTarget);
            if (collectionFunc != null) {
                // to collection
                return convertToCollection(
                    src, srcType, collectionFunc, paramType.getActualTypeArguments()[0], converter, options
                );
            }
            // to map or data object
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
    ) {
        if (srcType instanceof Class<?>) {
            Class<?> srcClass = (Class<?>) srcType;
            if (srcClass.isArray()) {
                int size = Array.getLength(src);
                Class<?> targetComponentType = target.getComponentType();
                Object newArray = Array.newInstance(targetComponentType, size);
                Class<?> srcComponentType = srcClass.getComponentType();
                for (int i = 0; i < size; i++) {
                    Object srcElement = Array.get(src, i);
                    Array.set(
                        newArray, i, converter.convert(srcElement, srcComponentType, targetComponentType, options)
                    );
                }
                return newArray;
            }
        }
        Type srcComponentType = resolveComponentType(srcType);
        if (srcComponentType == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        if (src instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) src;
            int size = coll.size();
            Class<?> targetComponentType = target.getComponentType();
            Object newArray = Array.newInstance(targetComponentType, size);
            int i = 0;
            for (Object s : coll) {
                Array.set(newArray, i++, converter.convert(s, srcComponentType, targetComponentType, options));
            }
            return newArray;
        }
        if (src instanceof Iterable<?>) {
            Iterable<?> iter = (Iterable<?>) src;
            List<Object> dst = new ArrayList<>();
            Class<?> targetComponentType = target.getComponentType();
            for (Object s : iter) {
                dst.add(converter.convert(s, srcComponentType, targetComponentType, options));
            }
            Object newArray = Array.newInstance(targetComponentType, dst.size());
            int i = 0;
            for (Object s : dst) {
                Array.set(newArray, i++, s);
            }
            return newArray;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private Object convertToArray(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull GenericArrayType target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        if (srcType instanceof Class<?>) {
            Class<?> srcClass = (Class<?>) srcType;
            if (srcClass.isArray()) {
                int size = Array.getLength(src);
                Type targetComponentType = target.getGenericComponentType();
                Object newArray = Array.newInstance(TypeKit.toRuntimeClass(targetComponentType), size);
                Class<?> srcComponentType = srcClass.getComponentType();
                for (int i = 0; i < size; i++) {
                    Object srcElement = Array.get(src, i);
                    Array.set(
                        newArray, i, converter.convert(srcElement, srcComponentType, targetComponentType, options)
                    );
                }
                return newArray;
            }
        }
        Type srcComponentType = resolveComponentType(srcType);
        if (srcComponentType == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        if (src instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) src;
            int size = coll.size();
            Type targetComponentType = target.getGenericComponentType();
            Object newArray = Array.newInstance(TypeKit.toRuntimeClass(targetComponentType), size);
            int i = 0;
            for (Object s : coll) {
                Array.set(newArray, i++, converter.convert(s, srcComponentType, targetComponentType, options));
            }
            return newArray;
        }
        if (src instanceof Iterable<?>) {
            Iterable<?> iter = (Iterable<?>) src;
            List<Object> dst = new ArrayList<>();
            Type targetComponentType = target.getGenericComponentType();
            for (Object s : iter) {
                dst.add(converter.convert(s, srcComponentType, targetComponentType, options));
            }
            Object newArray = Array.newInstance(TypeKit.toRuntimeClass(targetComponentType), dst.size());
            int i = 0;
            for (Object s : dst) {
                Array.set(newArray, i++, s);
            }
            return newArray;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private Object convertToCollection(
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
                int size = Array.getLength(src);
                Collection<Object> newCollection = collectionFunc.apply(size);
                Class<?> srcComponentType = srcClass.getComponentType();
                for (int i = 0; i < size; i++) {
                    Object srcElement = Array.get(src, i);
                    newCollection.add(
                        converter.convert(srcElement, srcComponentType, targetComponentType, options)
                    );
                }
                return newCollection;
            }
        }
        Type srcComponentType = resolveComponentType(srcType);
        if (srcComponentType == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        if (src instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) src;
            int size = coll.size();
            Collection<Object> newCollection = collectionFunc.apply(size);
            for (Object s : coll) {
                newCollection.add(converter.convert(s, srcComponentType, targetComponentType, options));
            }
            return newCollection;
        }
        if (src instanceof Iterable<?>) {
            Iterable<?> iter = (Iterable<?>) src;
            List<Object> dst = new ArrayList<>();
            for (Object s : iter) {
                dst.add(converter.convert(s, srcComponentType, targetComponentType, options));
            }
            Collection<Object> newCollection = collectionFunc.apply(dst.size());
            newCollection.addAll(dst);
            return newCollection;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private @Nullable Type resolveComponentType(@Nonnull Type type) {
        try {
            return TypeKit.resolveActualTypeArguments(type, Iterable.class).get(0);
        } catch (ReflectionException e) {
            return null;
        }
    }

    private Object convertToDataObject(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Class<?> rawTarget,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        IntFunction<Object> mapFunc = MapGenerator.get(rawTarget);
        DataMapper dataMapper = Jie.nonnull(
            Option.findValue(ConvertOption.DATA_MAPPER, options),
            DataMapper.defaultMapper()
        );
        if (mapFunc != null) {
            Object targetObject = mapFunc.apply(0);
            dataMapper.copyProperties(src, srcType, targetObject, target, converter, options);
            return targetObject;
        } else {
            DataBuilderFactory builderFactory = Jie.nonnull(
                Option.findValue(ConvertOption.BUILDER_FACTORY, options),
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

    private static final class CollectionGenerator {

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

    private static final class MapGenerator {

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
