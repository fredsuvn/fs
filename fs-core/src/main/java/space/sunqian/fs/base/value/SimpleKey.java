package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ValueClass;
import space.sunqian.fs.Fs;

import java.util.Arrays;
import java.util.Objects;

/**
 * A {@link SimpleKey} represents a key that consists of a group of elements. This class is particularly useful for
 * scenarios where composite keys are needed, such as caching method results where the key comprises the method
 * parameters.
 * <p>
 * This class is immutable and could be optimized as a value class.
 *
 * @author sunqian
 */
@ValueClass
@Immutable
public final class SimpleKey {

    /**
     * Creates a new {@link SimpleKey} instance containing the specified elements.
     *
     * @param elements the elements to include in the key
     * @return a new {@link SimpleKey} containing the specified elements
     */
    public static @Nonnull SimpleKey of(Object @Nonnull ... elements) {
        return new SimpleKey(elements);
    }

    private final Object @Nonnull [] elements;

    private SimpleKey(Object @Nonnull [] elements) {
        this.elements = elements;
    }

    /**
     * Returns the element at the specified position in this key, cast to the specified type.
     *
     * @param index the index of the element to return
     * @param <T>   the target type for casting
     * @return the element at the specified position, cast to type {@code T}
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException        if the element cannot be cast to the specified type
     */
    public <T> T getAs(int index) throws IndexOutOfBoundsException, ClassCastException {
        return Fs.as(get(index));
    }

    /**
     * Returns the element at the specified position in this key.
     *
     * @param index the index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        return elements[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleKey)) {
            return false;
        }
        return Objects.deepEquals(elements, ((SimpleKey) o).elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
        return "SimpleKey" + Arrays.toString(elements);
    }
}