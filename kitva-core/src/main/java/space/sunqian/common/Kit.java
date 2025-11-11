package space.sunqian.common;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.exception.UnknownArrayTypeException;
import space.sunqian.common.base.option.Option;
import space.sunqian.common.base.process.ProcessKit;
import space.sunqian.common.base.thread.ThreadKit;
import space.sunqian.common.collect.ArrayKit;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.collect.MapKit;
import space.sunqian.common.collect.SetKit;
import space.sunqian.common.collect.StreamKit;
import space.sunqian.common.base.function.callable.BooleanCallable;
import space.sunqian.common.base.function.callable.VoidCallable;
import space.sunqian.common.io.IORuntimeException;
import space.sunqian.common.object.convert.ConvertOption;
import space.sunqian.common.object.convert.DataMapper;
import space.sunqian.common.object.convert.ObjectConvertException;
import space.sunqian.common.object.convert.ObjectConverter;
import space.sunqian.common.object.convert.UnsupportedObjectConvertException;
import space.sunqian.common.object.data.ObjectSchema;
import space.sunqian.common.runtime.reflect.TypeRef;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The core utility class of this library, provides support methods for {@link Object}, such as {@code equals} and
 * {@code hashCode}. And many methods to improve language convenience, such as {@code nonnull}, {@code uncheck}. And
 * shortcuts to other commonly used methods in this lib, such as {@link #list(Object[])},
 * {@link #copyProperties(Object, Object, Option[])}, {@link #convert(Object, Class, Option[])}, etc.
 *
 * @author sunqian
 */
public class Kit {

    /**
     * Name of this lib.
     */
    public static final @Nonnull String LIB_NAME = "KitVa";

    /**
     * Version of this lib.
     */
    public static final @Nonnull String LIB_VERSION = "0.0.0";

    /**
     * String value for {@code null}.
     */
    public static final @Nonnull String NULL_STRING = Objects.toString(null);

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
     * Directly returns the given object itself. The given object is annotated by {@link Nullable}, but the return value
     * is annotated by {@link Nonnull}. This method is used to suppress some IDE or compilation warnings.
     *
     * @param obj the given object
     * @param <T> the type of the given object
     * @return the given object itself
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> @Nonnull T asNonnull(@Nullable T obj) {
        return obj;
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
     * Runs the given action and wraps any exception into an unchecked exception, the unchecked exception is generated
     * by the given unchecked exception generator. The logic as follows:
     * <pre>{@code
     * try {
     *     action.call();
     * } catch (Exception e) {
     *     throw unchecked.apply(e);
     * }
     * }</pre>
     *
     * @param action    the given action
     * @param unchecked the given unchecked exception generator
     */
    public static void uncheck(
        @Nonnull VoidCallable action,
        @Nonnull Function<? super @Nonnull Exception, ? extends @Nonnull RuntimeException> unchecked
    ) {
        try {
            action.call();
        } catch (Exception e) {
            throw unchecked.apply(e);
        }
    }

    /**
     * Runs the given action and wraps any exception into an unchecked exception, the unchecked exception is generated
     * by the given unchecked exception generator. The logic as follows:
     * <pre>{@code
     * try {
     *     return action.call();
     * } catch (Exception e) {
     *     throw unchecked.apply(e);
     * }
     * }</pre>
     *
     * @param action    the given action
     * @param unchecked the given unchecked exception generator
     * @param <T>       the type of the result
     * @return the result of the given action
     */
    public static <T> T uncheck(
        @Nonnull Callable<T> action,
        @Nonnull Function<? super @Nonnull Exception, ? extends @Nonnull RuntimeException> unchecked
    ) {
        try {
            return action.call();
        } catch (Exception e) {
            throw unchecked.apply(e);
        }
    }

    /**
     * Runs the given action and ignores any exception thrown by the action. The logic as follows:
     * <pre>{@code
     * try {
     *     action.call();
     * } catch (Exception ignored) {
     * }
     * }</pre>
     *
     * @param action the given action
     */
    public static void ignoreException(@Nonnull VoidCallable action) {
        try {
            action.call();
        } catch (Exception ignored) {
        }
    }

    /**
     * Calls the given action and returns the result. If any exception is thrown, returns the default value. The logic
     * as follows:
     * <pre>{@code
     * try {
     *     return action.call();
     * } catch (Exception e) {
     *     return defaultValue;
     * }
     * }</pre>
     *
     * @param action       the given action
     * @param defaultValue the default value
     * @param <T>          the type of the result
     * @return the result of the given action, or the default value if any exception is thrown
     */
    public static <T> T call(@Nonnull Callable<T> action, T defaultValue) {
        try {
            return action.call();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Calls the given action and returns the result. If any exception is thrown, the exception will be passed to the
     * given generator, and the generator will return a result as the action's result. The logic as follows:
     * <pre>{@code
     * try {
     *     return action.call();
     * } catch (Exception e) {
     *     return generator.apply(e);
     * }
     * }</pre>
     *
     * @param action    the given action
     * @param generator the given generator to handle the exception
     * @param <T>       the type of the result
     * @return the result of the given action, or the result of the given generator if any exception is thrown
     */
    public static <T> T callUncheck(
        @Nonnull Callable<T> action,
        @Nonnull Function<? super @Nonnull Exception, ? extends T> generator
    ) {
        try {
            return action.call();
        } catch (Exception e) {
            return generator.apply(e);
        }
    }

    /**
     * Executes the given task until it returns {@code true} or throws an exception. The original exception will be
     * wrapped by {@link AwaitingException} then thrown, using {@link AwaitingException#getCause()} can get the original
     * exception. The logic of this method is as follows:
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
     *
     * @param task the given task to be executed
     * @throws AwaitingException if an error occurs while awaiting
     */
    public static void until(@Nonnull BooleanCallable task) throws AwaitingException {
        try {
            while (true) {
                if (task.call()) {
                    return;
                }
            }
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
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
        if (typeA.isArray() && equals(typeA, typeB)) {
            return equalsArray(a, b, deep);
        }
        return Objects.equals(a, b);
    }

    private static boolean equalsArray(@Nonnull Object a, @Nonnull Object b, boolean deep) {
        if (a instanceof Object[]) {
            return deep ? Arrays.deepEquals((Object[]) a, (Object[]) b) : Arrays.equals((Object[]) a, (Object[]) b);
        } else if (a instanceof boolean[]) {
            return Arrays.equals((boolean[]) a, (boolean[]) b);
        } else if (a instanceof byte[]) {
            return Arrays.equals((byte[]) a, (byte[]) b);
        } else if (a instanceof short[]) {
            return Arrays.equals((short[]) a, (short[]) b);
        } else if (a instanceof char[]) {
            return Arrays.equals((char[]) a, (char[]) b);
        } else if (a instanceof int[]) {
            return Arrays.equals((int[]) a, (int[]) b);
        } else if (a instanceof long[]) {
            return Arrays.equals((long[]) a, (long[]) b);
        } else if (a instanceof float[]) {
            return Arrays.equals((float[]) a, (float[]) b);
        } else if (a instanceof double[]) {
            return Arrays.equals((double[]) a, (double[]) b);
        }
        throw new UnknownArrayTypeException(a.getClass());
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
            return hashArray(obj, deep);
        }
        return obj.hashCode();
    }

    private static int hashArray(@Nonnull Object obj, boolean deep) {
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
        throw new UnknownArrayTypeException(obj.getClass());
    }

    /**
     * Returns the identity hashcode of the given object, this method is equivalent to
     * {@link System#identityHashCode(Object)}.
     *
     * @param obj the given object
     * @return the identity hashcode of the given object
     */
    public static int hashId(@Nullable Object obj) {
        return System.identityHashCode(obj);
    }

    //---------------- Copy Properties Begin ----------------//

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link DataMapper#copyProperties(Object, Object, Option[])}.
     *
     * @param src     the given source object
     * @param dst     the given destination object
     * @param options the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     * @see DataMapper
     */
    public static void copyProperties(
        @Nonnull Object src, @Nonnull Object dst, @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        DataMapper.defaultMapper().copyProperties(src, dst, options);
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link DataMapper#copyProperties(Object, Object, ObjectConverter, Option[])}.
     *
     * @param src       the given source object
     * @param dst       the given destination object
     * @param converter the converter for converting values of the properties if needed
     * @param options   the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     * @see DataMapper
     */
    public static void copyProperties(
        @Nonnull Object src,
        @Nonnull Object dst,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        DataMapper.defaultMapper().copyProperties(src, dst, converter, options);
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link DataMapper#copyProperties(Object, Type, Object, Type, Option[])}.
     *
     * @param src     the given source object
     * @param srcType specifies the type of the given source object
     * @param dst     the given destination object
     * @param dstType specifies the type of the given destination object
     * @param options the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     * @see DataMapper
     */
    public static void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        DataMapper.defaultMapper().copyProperties(
            src,
            srcType,
            dst,
            dstType,
            options
        );
    }

    /**
     * Copy properties from the given source object to the given destination object. The object can be a {@link Map} or
     * a non-map object which can be parsed to {@link ObjectSchema}.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the
     * {@link DataMapper#copyProperties(Object, Type, Object, Type, ObjectConverter, Option[])}.
     *
     * @param src       the given source object
     * @param srcType   specifies the type of the given source object
     * @param dst       the given destination object
     * @param dstType   specifies the type of the given destination object
     * @param converter the converter for converting values of the properties if needed
     * @param options   the options for copying properties
     * @throws ObjectConvertException if an error occurs during copying properties
     * @see DataMapper
     */
    public static void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        DataMapper.defaultMapper().copyProperties(
            src,
            srcType,
            dst,
            dstType,
            converter,
            options
        );
    }

    //---------------- Copy Properties End ----------------//

    //---------------- Object Conversion Begin ----------------//

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link ObjectConverter#convert(Object, Class, Option[])}.
     *
     * @param src     the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     * @see ObjectConverter
     */
    public static <T> T convert(
        @Nullable Object src,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return ObjectConverter.defaultConverter().convert(src, target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link ObjectConverter#convert(Object, TypeRef, Option[])}.
     *
     * @param src     the given source object
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     * @see ObjectConverter
     */
    public static <T> T convert(
        @Nullable Object src,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return ObjectConverter.defaultConverter().convert(src, target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link ObjectConverter#convert(Object, Type, Class, Option[])}.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     * @see ObjectConverter
     */
    public static <T> T convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Class<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return ObjectConverter.defaultConverter().convert(src, srcType, target, options);
    }

    /**
     * Converts the given source object from the specified type to the target type.
     * <p>
     * The options parameter can be empty, in which case the default behavior will be used, or built-in options in
     * {@link ConvertOption} or other custom options for custom implementations.
     * <p>
     * This method is a shortcut to the {@link ObjectConverter#convert(Object, Type, TypeRef, Option[])}.
     *
     * @param src     the given source object
     * @param srcType the specified type of the given source object
     * @param target  the specified type ref of the target object
     * @param options the other conversion options
     * @param <T>     the target type
     * @return the converted object, {@code null} is permitted
     * @throws UnsupportedObjectConvertException if the conversion from the specified type to the target type is not
     *                                           supported
     * @throws ObjectConvertException            if the conversion failed
     * @see ObjectConverter
     */
    public static <T> T convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull TypeRef<? extends T> target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws UnsupportedObjectConvertException, ObjectConvertException {
        return ObjectConverter.defaultConverter().convert(src, srcType, target, options);
    }

    //---------------- Object Conversion End ----------------//

    //---------------- Collection Begin ----------------//

    /**
     * Directly returns the given variable arguments as an array.
     * <p>
     * This method is a shortcut to the {@link ArrayKit#array(Object[])}.
     *
     * @param elements the given variable arguments
     * @param <T>      the component type
     * @return the given variable arguments as an array
     * @see ArrayKit
     */
    @SafeVarargs
    public static <T> T @Nonnull [] array(T @Nonnull @RetainedParam ... elements) {
        return ArrayKit.array(elements);
    }

    /**
     * Returns an immutable list backed by the given array. The returned list is immutable but the backing array is not,
     * changes to the backing array "write through" to the returned list. The returned list is serializable and
     * implements {@link RandomAccess}.
     * <p>
     * This method is a shortcut to the {@link ListKit#list(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return an immutable list backed by the given array
     * @see ListKit
     */
    @SafeVarargs
    public static <T> @Nonnull @Immutable List<T> list(T @Nonnull @RetainedParam ... array) {
        return ListKit.list(array);
    }

    /**
     * Returns a new {@link ArrayList} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link ListKit#arrayList(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link ArrayList} initialing with the given array
     * @see ListKit
     */
    @SafeVarargs
    public static <T> @Nonnull ArrayList<T> arrayList(T @Nonnull ... array) {
        return ListKit.arrayList(array);
    }

    /**
     * Returns a new {@link LinkedList} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link ListKit#linkedList(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedList} initialing with the given array
     * @see ListKit
     */
    @SafeVarargs
    public static <T> @Nonnull LinkedList<T> linkedList(T @Nonnull ... array) {
        return ListKit.linkedList(array);
    }

    /**
     * Returns a new immutable set of which content is added from the given array. The content of the set is added in
     * array order, and the duplicate elements will be ignored. The behavior of this method is equivalent to:
     * <pre>{@code
     * return Collections.unmodifiableSet(linkedHashSet(array));
     * }</pre>
     * <p>
     * This method is a shortcut to the {@link SetKit#set(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new immutable set of which content is added from the given array
     * @see SetKit
     */
    @SafeVarargs
    public static <T> @Nonnull @Immutable Set<T> set(T @Nonnull ... array) {
        return SetKit.set(array);
    }

    /**
     * Returns a new {@link HashSet} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link SetKit#hashSet(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link HashSet} initialing with the given array
     * @see SetKit
     */
    @SafeVarargs
    public static <T> @Nonnull HashSet<T> hashSet(T @Nonnull ... array) {
        return SetKit.hashSet(array);
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given array.
     * <p>
     * This method is a shortcut to the {@link SetKit#linkedHashSet(Object[])}.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedHashSet} initialing with the given array
     * @see SetKit
     */
    @SafeVarargs
    public static <T> @Nonnull LinkedHashSet<T> linkedHashSet(T @Nonnull ... array) {
        return SetKit.linkedHashSet(array);
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
     * This method is a shortcut to the {@link MapKit#map(Object[])}.
     *
     * @param array the given array
     * @param <K>   the key type
     * @param <V>   the value type
     * @return a new {@link HashMap} initialing with the given array
     * @see MapKit
     */
    public static <K, V> @Nonnull @Immutable Map<K, V> map(Object @Nonnull ... array) {
        return MapKit.map(array);
    }

    /**
     * Returns a new {@link HashMap} initialing with the given array.
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * This method is a shortcut to the {@link MapKit#hashMap(Object[])}.
     *
     * @param array the given array
     * @param <K>   the key type
     * @param <V>   the value type
     * @return a new {@link HashMap} initialing with the given array
     * @see MapKit
     */
    public static <K, V> @Nonnull HashMap<K, V> hashMap(Object @Nonnull ... array) {
        return MapKit.hashMap(array);
    }

    /**
     * Returns a new {@link LinkedHashMap} initialing with the given array
     * <p>
     * Every two elements of the array form a key-value pair, that means, the {@code array[0]} and {@code array[1]} will
     * be the first key-value pair, the {@code array[2]} and {@code array[3]} will be the second key-value pair, and so
     * on. If the length of the array is odd and the last key cannot match the value, then the last pair will be the
     * key-{@code null} pair to put.
     * <p>
     * This method is a shortcut to the {@link MapKit#linkedHashMap(Object[])}.
     *
     * @param array the given array
     * @param <K>   the key type
     * @param <V>   the value type
     * @return a new {@link HashMap} initialing with the given array
     * @see MapKit
     */
    public static <K, V> @Nonnull LinkedHashMap<K, V> linkedHashMap(Object @Nonnull ... array) {
        return MapKit.linkedHashMap(array);
    }

    /**
     * Returns a {@link Stream} from the given elements.
     * <p>
     * This method is a shortcut to the {@link StreamKit#stream(Object[])}.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements
     * @see StreamKit
     */
    @SafeVarargs
    public static <T> @Nonnull Stream<T> stream(T @Nonnull ... elements) {
        return StreamKit.stream(elements);
    }

    /**
     * Returns a {@link Stream} from the given elements.
     * <p>
     * This method is a shortcut to the {@link StreamKit#stream(Iterable)}.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements
     * @see StreamKit
     */
    public static <T> @Nonnull Stream<T> stream(@Nonnull Iterable<T> elements) {
        return StreamKit.stream(elements);
    }

    //---------------- Collection End ----------------//

    //---------------- Thread Begin ----------------//

    /**
     * Sleeps the current thread for the specified milliseconds.
     * <p>
     * This method is a shortcut to the {@link ThreadKit#sleep(long)}.
     *
     * @param millis the specified milliseconds
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     * @see ThreadKit
     */
    public static void sleep(long millis) throws AwaitingException {
        ThreadKit.sleep(millis);
    }

    /**
     * Sleeps the current thread for the specified duration.
     * <p>
     * This method is a shortcut to the {@link ThreadKit#sleep(Duration)}.
     *
     * @param duration the specified duration
     * @throws AwaitingException if the current thread is interrupted or an error occurs while sleeping
     * @see ThreadKit
     */
    public static void sleep(@Nonnull Duration duration) throws AwaitingException {
        ThreadKit.sleep(duration);
    }

    //---------------- Thread End ----------------//

    //---------------- Process Begin ----------------//

    /**
     * Starts a new process with the specified command, returns the process.
     * <p>
     * This method is a shortcut to the {@link ProcessKit#start(String)}.
     *
     * @param command the specified command
     * @return the process
     * @throws IORuntimeException if any error occurs
     * @see ProcessKit
     */
    public static @Nonnull Process process(@Nonnull String command) throws IORuntimeException {
        return ProcessKit.start(command);
    }

    /**
     * Starts a new process with the specified command and arguments, returns the process.
     * <p>
     * This method is a shortcut to the {@link ProcessKit#start(String...)}.
     *
     * @param command the specified command and arguments
     * @return the process
     * @throws IORuntimeException if any error occurs
     * @see ProcessKit
     */
    public static @Nonnull Process process(@Nonnull String @Nonnull ... command) throws IORuntimeException {
        return ProcessKit.start(command);
    }

    //---------------- Process Begin ----------------//

    private Kit() {
    }
}
