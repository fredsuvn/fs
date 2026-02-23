package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;

/**
 * A {@link SimpleKey} represents a key consists of a group of elements. For example, for a cache of method results, its
 * key is the parameter of the method, and this class can be used to be that key.
 *
 * @author sunqian
 */
public final class SimpleKey {

    /**
     * Returns a new {@link SimpleKey} with the given elements.
     *
     * @param elements the given elements
     * @return a new {@link SimpleKey} with the given elements
     */
    public static @Nonnull SimpleKey of(Object @Nonnull ... elements) {
        return new SimpleKey(elements);
    }

    private final Object @Nonnull [] elements;

    private SimpleKey(Object @Nonnull [] elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleKey)) {
            return false;
        }
        SimpleKey osk = (SimpleKey) o;
        return Objects.deepEquals(elements, osk.elements);
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
