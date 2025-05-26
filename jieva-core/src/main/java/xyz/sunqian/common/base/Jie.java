package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.function.BooleanCallable;
import xyz.sunqian.common.base.thread.JieThread;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieList;
import xyz.sunqian.common.collect.JieMap;
import xyz.sunqian.common.collect.JieSet;
import xyz.sunqian.common.mapping.BeanMapper;
import xyz.sunqian.common.mapping.Mapper;
import xyz.sunqian.common.mapping.MappingOptions;
import xyz.sunqian.common.reflect.TypeRef;
import xyz.sunqian.common.task.JieTask;
import xyz.sunqian.common.task.SubmissionException;
import xyz.sunqian.common.task.TaskExecutor;
import xyz.sunqian.common.task.TaskReceipt;
import xyz.sunqian.common.task.VoidReceipt;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * Utilities for object and common/base operations.
 *
 * @author fredsuvn
 */
public class Jie {

    /**
     * Casts and returns the given object as the specified type {@code T}. This method is equivalent to:
     * <pre>{@code
     * return (T) obj;
     * }</pre>
     *
     * @param obj the given object
     * @param <T> the specified type
     * @return the given object as the specified type {@code T}
     */
    @SuppressWarnings("noinspection unchecked")
    public static <T> T as(@Nullable Object obj) {
        return (T) obj;
    }

    /**
     * Returns the default value if the given object is {@code null}, or the given object itself if it is not
     * {@code null}. This method is equivalent to:
     * <pre>{@code
     * return obj == null ? defaultValue : obj;
     * }</pre>
     * <p>
     * Note this method does not guarantee that the returned value must be {@code nonnull}.
     *
     * @param obj          the given object
     * @param defaultValue the default value
     * @param <T>          the type of the returned value
     * @return the default value if the given object is {@code null}, or the given object itself if it is not
     * {@code null}
     */
    public static <T> T nonnull(@Nullable T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    /**
     * Returns the value computed from the specified supplier if the given object is {@code null}, or the given object
     * itself if it is not {@code null}. This method is equivalent to:
     * <pre>{@code
     * return obj == null ? supplier.get() : obj;
     * }</pre>
     * <p>
     * Note this method does not guarantee that the returned value must be {@code nonnull}.
     *
     * @param obj      the given object
     * @param supplier the specified supplier
     * @param <T>      the type of the returned value
     * @return the value computed from the specified supplier if the given object is {@code null}, or the given object
     * itself if it is not {@code null}
     */
    public static <T> T nonnull(@Nullable T obj, Supplier<? extends T> supplier) {
        return obj == null ? supplier.get() : obj;
    }

    /**
     * Returns whether the given objects are equal. If the given objects are arrays, uses {@code Arrays.equals} or
     * {@link Arrays#deepEquals(Object[], Object[])} if necessary.
     * <p>
     * This method is equivalent to ({@link #equalsWith(Object, Object, boolean, boolean)}):
     * {@code equalsWith(a, b, true, true)}.
     *
     * @param a the given object a
     * @param b the given object b
     * @return whether the given objects are equal
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return equalsWith(a, b, true, true);
    }

    /**
     * Returns whether the given objects are equal each other by {@link #equals(Object, Object)}.
     *
     * @param objects the given objects
     * @return whether the given objects are equal
     */
    public static boolean equalsAll(@Nullable Object @Nonnull ... objects) {
        for (int i = 0; i < objects.length - 1; i++) {
            if (!equals(objects[i], objects[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given objects are equal. This method follows the following logic:
     * <ul>
     *     <li>
     *         If {@code a == b}, returns {@code true};
     *     </li>
     *     <li>
     *         If the {@code arrayEquals} is {@code true}:
     *         <ul>
     *             <li>
     *                 If the {@code deep} is {@code true}, uses {@link Arrays#deepEquals(Object[], Object[])} for
     *                 them. Otherwise, uses {@code Arrays.equals}.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         Returns {@link Objects#equals(Object, Object)} otherwise.
     *     </li>
     * </ul>
     *
     * @param a           the given object a
     * @param b           the given object b
     * @param arrayEquals the arrayEquals option
     * @param deep        the deep option
     * @return whether the given objects are equal
     */
    public static boolean equalsWith(@Nullable Object a, @Nullable Object b, boolean arrayEquals, boolean deep) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!arrayEquals) {
            return Objects.equals(a, b);
        }
        Class<?> typeA = a.getClass();
        Class<?> typeB = b.getClass();
        if (typeA.isArray() && typeB.isArray()) {
            if (a instanceof Object[] && b instanceof Object[]) {
                return deep ? Arrays.deepEquals((Object[]) a, (Object[]) b) : Arrays.equals((Object[]) a, (Object[]) b);
            } else if (a instanceof boolean[] && b instanceof boolean[]) {
                return Arrays.equals((boolean[]) a, (boolean[]) b);
            } else if (a instanceof byte[] && b instanceof byte[]) {
                return Arrays.equals((byte[]) a, (byte[]) b);
            } else if (a instanceof short[] && b instanceof short[]) {
                return Arrays.equals((short[]) a, (short[]) b);
            } else if (a instanceof char[] && b instanceof char[]) {
                return Arrays.equals((char[]) a, (char[]) b);
            } else if (a instanceof int[] && b instanceof int[]) {
                return Arrays.equals((int[]) a, (int[]) b);
            } else if (a instanceof long[] && b instanceof long[]) {
                return Arrays.equals((long[]) a, (long[]) b);
            } else if (a instanceof float[] && b instanceof float[]) {
                return Arrays.equals((float[]) a, (float[]) b);
            } else if (a instanceof double[] && b instanceof double[]) {
                return Arrays.equals((double[]) a, (double[]) b);
            }
        }
        return Objects.equals(a, b);
    }

    /**
     * Returns the hashcode of the given object. If the given object is array, uses {@code Arrays.hashCode} or
     * {@link Arrays#deepHashCode(Object[])} if necessary.
     * <p>
     * This method is equivalent to ({@link #hashWith(Object, boolean, boolean)}): {@code hashWith(obj, true, true)}.
     *
     * @param obj the given object
     * @return the hashcode of the given object
     */
    public static int hashCode(@Nullable Object obj) {
        return hashWith(obj, true, true);
    }

    /**
     * Returns the hashcode of the given objects via {@link Arrays#deepHashCode(Object[])}.
     *
     * @param objs the given objects
     * @return the hashcode of the given objects via {@link Arrays#deepHashCode(Object[])}
     */
    public static int hashAll(@Nullable Object @Nonnull ... objs) {
        return Arrays.deepHashCode(objs);
    }

    /**
     * Returns the hashcode of the given object. This method follows the following logic:
     * <ul>
     *     <li>
     *         If the given object is not an array, returns {@link Objects#hashCode(Object)}.
     *     </li>
     *     <li>
     *         If the {@code arrayHash} is {@code true}:
     *         <ul>
     *             <li>
     *                 If the {@code deep} is {@code true}, uses {@link Arrays#deepHashCode(Object[])} for them.
     *                 Otherwise, uses {@code Arrays.hashCode}.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         Returns {@link Objects#hashCode(Object)} otherwise.
     *     </li>
     * </ul>
     *
     * @param obj       the given object
     * @param arrayHash the arrayHash option
     * @param deep      the deep option
     * @return the hashcode of the given object
     */
    public static int hashWith(@Nullable Object obj, boolean arrayHash, boolean deep) {
        if (obj == null || !arrayHash) {
            return Objects.hashCode(obj);
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            if (obj instanceof Object[]) {
                return deep ? Arrays.deepHashCode((Object[]) obj) : Arrays.hashCode((Object[]) obj);
            }
            if (obj instanceof boolean[]) {
                return Arrays.hashCode((boolean[]) obj);
            }
            if (obj instanceof byte[]) {
                return Arrays.hashCode((byte[]) obj);
            }
            if (obj instanceof short[]) {
                return Arrays.hashCode((short[]) obj);
            }
            if (obj instanceof char[]) {
                return Arrays.hashCode((char[]) obj);
            }
            if (obj instanceof int[]) {
                return Arrays.hashCode((int[]) obj);
            }
            if (obj instanceof long[]) {
                return Arrays.hashCode((long[]) obj);
            }
            if (obj instanceof float[]) {
                return Arrays.hashCode((float[]) obj);
            }
            if (obj instanceof double[]) {
                return Arrays.hashCode((double[]) obj);
            }
        }
        return obj.hashCode();
    }

    /**
     * Returns identity hash code for given object, same as {@link System#identityHashCode(Object)}.
     *
     * @param obj given object
     * @return the system hash code
     */
    public static int systemHash(@Nullable Object obj) {
        return System.identityHashCode(obj);
    }

    /**
     * Returns enum object of specified name from given enum class, may be null if not found.
     *
     * @param enumClass given enum class
     * @param name      specified name
     * @param <T>       type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, String name) {
        return findEnum(enumClass, name, false);
    }

    /**
     * Returns enum object of specified name from given enum class, may be null if not found.
     *
     * @param enumClass  given enum class
     * @param name       specified name
     * @param ignoreCase whether ignore case for specified name
     * @param <T>        type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, String name, boolean ignoreCase) {
        JieCheck.checkArgument(enumClass.isEnum(), "Not an enum class.");
        if (!ignoreCase) {
            try {
                return Enum.valueOf(as(enumClass), name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        Object[] enums = enumClass.getEnumConstants();
        if (JieArray.isEmpty(enums)) {
            return null;
        }
        for (Object anEnum : enums) {
            if (name.equalsIgnoreCase(anEnum.toString())) {
                return as(anEnum);
            }
        }
        return null;
    }

    /**
     * Returns enum object at specified index from given enum class, may be null if not found.
     *
     * @param enumClass given enum class
     * @param index     specified index
     * @param <T>       type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, int index) {
        JieCheck.checkArgument(enumClass.isEnum(), enumClass + " is not an enum.");
        JieCheck.checkArgument(index >= 0, "index must >= 0.");
        Object[] enums = enumClass.getEnumConstants();
        if (JieArray.isEmpty(enums) || index >= enums.length) {
            return null;
        }
        return as(enums[index]);
    }

    /**
     * Finds resource of given resource path (starts with "/").
     *
     * @param resPath given resource
     * @return url of resource of given resource path
     */
    public static URL findRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(JieString.removeStart(resPath, "/"));
    }

    /**
     * Finds all resources of given resource path (starts with "/").
     *
     * @param resPath given resource
     * @return url set of resource of given resource path
     */
    public static Set<URL> findAllRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Enumeration<URL> urls = classLoader.getResources(JieString.removeStart(resPath, "/"));
            if (!urls.hasMoreElements()) {
                return Collections.emptySet();
            }
            Set<URL> result = new LinkedHashSet<>();
            while (urls.hasMoreElements()) {
                result.add(urls.nextElement());
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to ({@link Mapper#map(Object, Class, MappingOptions)}):
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, Class, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, Class<T> targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to ({@link Mapper#map(Object, Type, MappingOptions)}):
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, Type, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, Type targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to ({@link Mapper#map(Object, TypeRef, MappingOptions)}):
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, TypeRef, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, TypeRef<T> targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Copies properties from source object to dest object, return the dest object. This method is equivalent to
     * ({@link BeanMapper#copyProperties(Object, Object)}):
     * <pre>
     *     return BeanMapper.defaultMapper().copyProperties(source, dest);
     * </pre>
     * Using {@link BeanMapper} for more mapping operations.
     *
     * @param source source object
     * @param dest   dest object
     * @param <T>    dest type
     * @return dest object
     * @see BeanMapper#defaultMapper()
     * @see BeanMapper#copyProperties(Object, Object)
     */
    public static <T> T copyProperties(Object source, T dest) {
        return BeanMapper.defaultMapper().copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object (specified ignored properties will be excluded), return the
     * dest object. This method is equivalent to ({@link BeanMapper#copyProperties(Object, Object, MappingOptions)}):
     * <pre>
     *     return BeanMapper.defaultMapper().copyProperties(source, dest,
     *         MappingOptions.builder().ignored(JieArray.asList(ignoredProperties)).build());
     * </pre>
     * Using {@link BeanMapper} for more mapping operations.
     *
     * @param source            source object
     * @param dest              dest object
     * @param ignoredProperties ignored properties
     * @param <T>               dest type
     * @return dest object
     * @see BeanMapper#defaultMapper()
     * @see BeanMapper#copyProperties(Object, Object, MappingOptions)
     */
    public static <T> T copyProperties(Object source, T dest, Object... ignoredProperties) {
        return BeanMapper.defaultMapper().copyProperties(source, dest,
            MappingOptions.builder().ignored(JieArray.asList(ignoredProperties)).build());
    }

    /**
     * Copies properties from source object to dest object, return the dest object. This method is equivalent to
     * ({@link BeanMapper#copyProperties(Object, Object, MappingOptions)}):
     * <pre>
     *     return BeanMapper.defaultMapper().copyProperties(source, dest, options);
     * </pre>
     * Using {@link BeanMapper} for more mapping operations.
     *
     * @param source  source object
     * @param dest    dest object
     * @param options mapping options
     * @param <T>     dest type
     * @return dest object
     * @see BeanMapper#defaultMapper()
     * @see BeanMapper#copyProperties(Object, Object, MappingOptions)
     */
    public static <T> T copyProperties(Object source, T dest, MappingOptions options) {
        return BeanMapper.defaultMapper().copyProperties(source, dest, options);
    }

    /**
     * Returns a new starter to build and start a {@link Thread}.
     *
     * @return a new starter to build and start a {@link Thread}
     */
    public static ThreadStarter threadStarter() {
        return ThreadStarter.newInstance();
    }

    /**
     * Returns a new starter to build and start a {@link Process}.
     *
     * @return a new starter to build and start a {@link Process}
     */
    public static ProcessStarter processStarter() {
        return ProcessStarter.newInstance();
    }

    /**
     * Returns a new builder to build a {@link ExecutorService}.
     *
     * @return a new builder to build a {@link ExecutorService}
     */
    public static ExecutorBuilder executorBuilder() {
        return ExecutorBuilder.newInstance();
    }

    /**
     * Returns a new builder to build a {@link ScheduledExecutorService}.
     *
     * @return a new builder to build a {@link ScheduledExecutorService}
     */
    public static ScheduledBuilder scheduledBuilder() {
        return ScheduledBuilder.newInstance();
    }

    /**
     * Directly returns the given variable arguments as an array.
     * <p>
     * This method is a shortcut to the {@link JieArray#array(Object[])}.
     *
     * @param elements the given variable arguments
     * @param <T>      the component type
     * @return the given variable arguments as an array
     */
    @SafeVarargs
    public static <T> T @Nonnull [] array(T @Nonnull @RetainedParam ... elements) {
        return JieArray.array(elements);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     * <p>
     * This method is a shortcut to the {@link JieList#list(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return an immutable list backed by the given array
     */
    @SafeVarargs
    public static <T> @Nonnull @Immutable List<T> list(T @Nonnull @RetainedParam ... array) {
        return JieList.list(array);
    }

    /**
     * Returns a new {@link ArrayList} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link JieList#arrayList(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link ArrayList} initialing with the given array
     */
    @SafeVarargs
    public static <T> @Nonnull ArrayList<T> arrayList(T @Nonnull ... array) {
        return JieList.arrayList(array);
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link JieList#linkedList(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedList} initialing with the given array
     */
    @SafeVarargs
    public static <T> @Nonnull LinkedList<T> linkedList(T @Nonnull ... array) {
        return JieList.linkedList(array);
    }

    /**
     * Returns a new immutable set of which content is added from the given array. The content of the set is added in
     * array order, and the duplicate elements will be ignored. The behavior of this method is equivalent to:
     * <pre>{@code
     * return Collections.unmodifiableSet(linkedHashSet(array));
     * }</pre>
     * <p>
     * This method is a shortcut to the {@link JieSet#set(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new immutable set of which content is added from the given array
     */
    @SafeVarargs
    public static <T> @Nonnull @Immutable Set<T> set(T @Nonnull ... array) {
        return JieSet.set(array);
    }

    /**
     * Returns a new {@link HashSet} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link JieSet#hashSet(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link HashSet} initialing with the given array
     */
    @SafeVarargs
    public static <T> @Nonnull HashSet<T> hashSet(T @Nonnull ... array) {
        return JieSet.hashSet(array);
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link JieSet#linkedHashSet(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedHashSet} initialing with the given array
     */
    @SafeVarargs
    public static <T> @Nonnull LinkedHashSet<T> linkedHashSet(T @Nonnull ... array) {
        return JieSet.linkedHashSet(array);
    }

    /**
     * Returns a new immutable map of which content is added from the given array.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * The behavior of this method is equivalent to:
     * <pre>{@code
     *  return Collections.unmodifiableMap(linkedHashMap(array));
     *  }</pre>
     * <p>
     * This method is a shortcut to the {@link JieMap#map(Object[])}.
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> @Nonnull @Immutable Map<K, V> map(Object @Nonnull ... array) {
        return JieMap.map(array);
    }

    /**
     * Returns a new {@link HashMap} initialing with the given array.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * This method is a shortcut to the {@link JieMap#hashMap(Object[])}.
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> @Nonnull HashMap<K, V> hashMap(Object @Nonnull ... array) {
        return JieMap.hashMap(array);
    }

    /**
     * Returns a new {@link LinkedHashMap} initialing with the given array
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * This method is a shortcut to the {@link JieMap#linkedHashMap(Object[])}.
     *
     * @param array the given array
     * @param <K>>  the key type
     * @param <V>>  the value type
     * @return a new {@link HashMap} initialing with the given array
     */
    public static <K, V> @Nonnull LinkedHashMap<K, V> linkedHashMap(Object @Nonnull ... array) {
        return JieMap.linkedHashMap(array);
    }

    //---------------- Thread Begin ----------------//

    /**
     * Sleeps the current thread until it is interrupted.
     * <p>
     * This method is a shortcut to the {@link JieThread#sleep()}.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep() throws AwaitingException {
        JieThread.sleep();
    }

    /**
     * Sleeps the current thread for the specified milliseconds.
     * <p>
     * This method is a shortcut to the {@link JieThread#sleep(long)}.
     *
     * @param millis the specified milliseconds
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(long millis) throws AwaitingException {
        JieThread.sleep(millis);
    }

    /**
     * Sleeps the current thread for the specified duration.
     * <p>
     * This method is a shortcut to the {@link JieThread#sleep(Duration)}.
     *
     * @param duration the specified duration
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     */
    public static void sleep(@Nonnull Duration duration) throws AwaitingException {
        JieThread.sleep(duration);
    }

    /**
     * Executes the given task until it returns {@code true} or throws an exception. The exception will be wrapped by
     * {@link AwaitingException} then thrown. This is the unchecked version of {@link #untilChecked(BooleanCallable)},
     * and its logic is as follows:
     * <pre>{@code
     * try {
     *     while (true) {
     *         if (task.call()) {
     *             return;
     *         }
     *     }
     * } catch (Exception e) {
     *     throw new AwaitingException(e);
     * }
     * }</pre>
     * <p>
     * Note this method may cause high CPU usage. When the task determines to return {@code false}, consider adding some
     * measures (such as sleep the current thread in a very short time) to avoid it.
     * <p>
     * This method is a shortcut to the {@link JieThread#until(BooleanCallable)}.
     *
     * @param task the given task to be executed
     * @throws AwaitingException if an error occurs while awaiting
     */
    public static void until(@Nonnull BooleanCallable task) throws AwaitingException {
        JieThread.until(task);
    }

    /**
     * Executes the given task until it returns {@code true} or throws an {@link Exception}. Its logic is as follows:
     * <pre>{@code
     * while (true) {
     *     if (task.call()) {
     *         return;
     *     }
     * }
     * }</pre>
     * <p>
     * Note this method may cause high CPU usage. When the task determines to return {@code false}, consider adding some
     * measures (such as sleep the current thread in a very short time) to avoid it.
     * <p>
     * This method is a shortcut to the {@link JieThread#untilChecked(BooleanCallable)}.
     *
     * @param task the given task to be executed
     * @throws Exception if the {@link Exception} thrown by the given task
     */
    public static void untilChecked(@Nonnull BooleanCallable task) throws Exception {
        JieThread.untilChecked(task);
    }

    //---------------- Thread End ----------------//

    //---------------- Task Begin ----------------//

    /**
     * Runs the given task asynchronously.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newExecutor()}, and is a shortcut to the
     * {@link JieTask#run(Runnable)}.
     *
     * @param task the task to run
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static void run(@Nonnull Runnable task) throws SubmissionException {
        JieTask.run(task);
    }

    /**
     * Runs the given task asynchronously, and returns the receipt of the task.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newExecutor()}, and is a shortcut to the
     * {@link JieTask#run(Callable)}.
     *
     * @param task the task to run
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> run(@Nonnull Callable<? extends T> task) throws SubmissionException {
        return JieTask.run(task);
    }

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given delay.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#schedule(Runnable, Duration)}.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt schedule(@Nonnull Runnable task, @Nonnull Duration delay) throws SubmissionException {
        return JieTask.schedule(task, delay);
    }

    /**
     * Schedules the given task with a specified delay time from now, returns a {@link TaskReceipt} for the task. The
     * task becomes enabled after the given delay.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#schedule(Callable, Duration)}.
     *
     * @param task  the given task
     * @param delay the specified delay time
     * @param <T>   the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> schedule(
        @Nonnull Callable<? extends T> task,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return JieTask.schedule(task, delay);
    }

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given time.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#scheduleAt(Runnable, Instant)}.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleAt(
        @Nonnull Runnable task, @Nonnull Instant time
    ) throws SubmissionException {
        return JieTask.scheduleAt(task, time);
    }

    /**
     * Schedules the given task to be executed at the specified time, returns a {@link VoidReceipt} for the task. The
     * task becomes enabled after the given time.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#scheduleAt(Callable, Instant)}.
     *
     * @param task the given task
     * @param time the specified time to execute the task
     * @param <T>  the type of the task result
     * @return the receipt of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static <T> @Nonnull TaskReceipt<T> scheduleAt(
        @Nonnull Callable<? extends T> task,
        @Nonnull Instant time
    ) throws SubmissionException {
        return JieTask.scheduleAt(task, time);
    }

    /**
     * Schedules the given periodic task that becomes enabled first after the given initial delay, and subsequently with
     * the given period. That is, the executions will commence after {@code initialDelay} then
     * {@code initialDelay + period}, then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor. If any execution of this task takes longer than its period, then
     * subsequent executions may start late, but will not concurrently execute.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#scheduleWithRate(Runnable, Duration, Duration)}.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param period       the given period between successive executions
     * @return the receipt representing pending completion of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleWithRate(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration period
    ) throws SubmissionException {
        return JieTask.scheduleWithRate(task, initialDelay, period);
    }

    /**
     * Schedules the given periodic task that becomes enabled first after the given initial delay, and subsequently with
     * the given delay between the termination of one execution and the commencement of the next.
     * <p>
     * If any execution of the task fails, subsequent executions are suppressed. Otherwise, the task will only terminate
     * via cancellation or termination of the executor.
     * <p>
     * This method is backed by a global executor from {@link TaskExecutor#newScheduler()}, and is a shortcut to the
     * {@link JieTask#scheduleWithDelay(Runnable, Duration, Duration)}.
     *
     * @param task         the given periodic task
     * @param initialDelay the given initial delay for first execution
     * @param delay        the given delay between the termination of one execution and the commencement of the next
     * @return the receipt representing pending completion of the task
     * @throws SubmissionException if an error occurs during the submitting
     */
    public static @Nonnull VoidReceipt scheduleWithDelay(
        @Nonnull Runnable task,
        @Nonnull Duration initialDelay,
        @Nonnull Duration delay
    ) throws SubmissionException {
        return JieTask.scheduleWithDelay(task, initialDelay, delay);
    }

    //---------------- Task End ----------------//
}
