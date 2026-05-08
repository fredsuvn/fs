package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ValueClass;

import java.util.List;

/**
 * A {@link SimpleKey} represents a key that consists of a group of elements.
 * <p>
 * Two {@link SimpleKey} instances are equal if and only if their elements are equal at the same positions. Instances of
 * different implementation classes (with the same number of elements in corresponding positions) * behave identically
 * in terms of {@code equals}, {@code hashCode}, and {@code toString}. For better performance, it is recommended to
 * compare instances of the same implementation class whenever possible.
 * <p>
 * Implementations of this interface are particularly useful for scenarios where composite keys are needed, such as
 * caching method results where the key comprises the method parameters. There are built-in implementations available:
 * <ul>
 *   <li>{@link SimpleKey2} for keys with two elements.</li>
 *   <li>{@link SimpleKey3} for keys with three elements.</li>
 *   <li>{@link SimpleKeyN} for keys with {@code n} number of elements.</li>
 * </ul>
 *
 * @author sunqian
 * @implNote The implementations should be immutable and could be optimized as a value class.
 */
@ValueClass
@Immutable
public interface SimpleKey {

    /**
     * Returns the element at the specified position in this key, cast to the specified type.
     *
     * @param index the index of the element to return
     * @param <T>   the target type for casting
     * @return the element at the specified position, cast to type {@code T}
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException        if the element cannot be cast to the specified type
     */
    <T> T getAs(int index) throws IndexOutOfBoundsException, ClassCastException;

    /**
     * Returns the element at the specified position in this key.
     *
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    Object get(int index) throws IndexOutOfBoundsException;

    /**
     * Returns an immutable list contains all elements of this key.
     *
     * @return an immutable list contains all elements of this key
     */
    @Nonnull
    @Immutable
    List<Object> elements();

    /**
     * Returns the number of elements in this key.
     *
     * @return the number of elements in this key
     */
    int size();

    /**
     * Compares whether this key equals to the given object. Two {@code SimpleKey} instances are equal if and only if:
     * <ol>
     *   <li>Both are instances of {@code SimpleKey} (can be different implementation classes);</li>
     *   <li>Their corresponding elements (at the same index) are equal.</li>
     * </ol>
     *
     * @param o the given object to compare.
     * @return {@code true} if this key equals to the given object, {@code false} otherwise
     */
    @Override
    boolean equals(@Nullable Object o);

    /**
     * Returns hash code of this key. The hash code is computed using the following algorithm:
     * <pre>{@code
     * int result = 0;
     * for (Object e : elements()) {
     *     result = 31 * result + Objects.hashCode(e);
     * }
     * return result;
     * }</pre>
     *
     * @return the hash code of this key
     */
    @Override
    int hashCode();

    /**
     * Returns a string representation of this key. The format is as follows:
     * <pre>{@code k:[e1, e2, e3,...]}</pre>
     *
     * @return the string representation of this key
     */
    @Override
    @Nonnull
    String toString();
}
