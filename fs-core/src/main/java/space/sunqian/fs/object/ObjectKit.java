package space.sunqian.fs.object;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.UnknownArrayTypeException;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.schema.ObjectParser;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Object utilities.
 *
 * @author sunqian
 */
public class ObjectKit {

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
        if (typeA.isArray() && Objects.equals(typeA, typeB)) {
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
    public static int id(@Nullable Object obj) {
        return System.identityHashCode(obj);
    }

    /**
     * Returns the value of the specified property from the given object.
     * <p>
     * This method supports nested property access using dot notation (e.g., "parent.child.property"). If any part of
     * the property path is not found or is {@code null}, this method returns {@code null}.
     * <p>
     * Note this method use {@link ConvertKit#objectParser()} to parse object schemas, it is equivalent to:
     * <pre>{@code
     * ObjectKit.getPropertyValue(object, propertyName, ConvertKit.objectParser());
     * }</pre>
     *
     * @param object       the given object from which to retrieve the property value
     * @param propertyName the name of the property to retrieve, supporting dot notation for nested properties
     * @return the value of the specified property, or {@code null} if the property path is not found or is {@code null}
     */
    public static @Nullable Object getPropertyValue(
        @Nullable Object object,
        @Nonnull String propertyName
    ) {
        return getPropertyValue(object, propertyName, ConvertKit.objectParser());
    }

    /**
     * Returns the value of the specified property from the given object.
     * <p>
     * This method supports nested property access using dot notation (e.g., "parent.child.property"). If any part of
     * the property path is not found or is {@code null}, this method returns {@code null}.
     *
     * @param object       the given object from which to retrieve the property value
     * @param propertyName the name of the property to retrieve, supporting dot notation for nested properties
     * @param objectParser the parser used to resolve object schemas
     * @return the value of the specified property, or {@code null} if the property path is not found or is {@code null}
     */
    public static @Nullable Object getPropertyValue(
        @Nullable Object object,
        @Nonnull String propertyName,
        @Nonnull ObjectParser objectParser
    ) {
        if (object == null) {
            return null;
        }
        int start = 0;
        Object cur = object;
        while (true) {
            int nextDotIndex = propertyName.indexOf('.', start);
            if (nextDotIndex == -1) {
                return getPropertyValue0(cur, propertyName.substring(start), objectParser);
            } else {
                cur = getPropertyValue0(cur, propertyName.substring(start, nextDotIndex), objectParser);
                start = nextDotIndex + 1;
            }
        }
    }

    private static @Nullable Object getPropertyValue0(
        @Nullable Object object,
        @Nonnull String propertyName,
        @Nonnull ObjectParser objectParser
    ) {
        if (object == null) {
            return null;
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).get(propertyName);
        }
        ObjectSchema schema = objectParser.parse(object.getClass());
        ObjectProperty property = schema.getProperty(propertyName);
        if (property == null) {
            return null;
        }
        return property.getValue(object);
    }

    private ObjectKit() {
    }
}
