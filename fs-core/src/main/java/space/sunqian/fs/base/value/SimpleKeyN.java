package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ValueClass;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A {@link SimpleKeyN} represents a key that consists of a list of elements, it is the implementation of
 * {@link SimpleKey} containing {@code n} number of elements.
 *
 * @author sunqian
 * @see SimpleKey
 */
@ValueClass
@Immutable
public final class SimpleKeyN implements SimpleKey {

    /**
     * Creates a new {@link SimpleKeyN} instance containing the specified elements.
     *
     * @param elements the elements to include in the key
     * @return a new {@link SimpleKeyN} containing the specified elements
     */
    public static @Nonnull SimpleKeyN of(Object @Nonnull ... elements) {
        return new SimpleKeyN(elements);
    }

    private final Object @Nonnull [] elements;

    private SimpleKeyN(Object @Nonnull [] elements) {
        this.elements = elements;
    }

    @Override
    public <T> T getAs(int index) throws IndexOutOfBoundsException, ClassCastException {
        return Fs.as(get(index));
    }

    @Override
    public Object get(int index) throws IndexOutOfBoundsException {
        return elements[index];
    }

    @Override
    public @Nonnull @Immutable List<Object> elements() {
        return ListKit.list(elements);
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SimpleKeyN) {
            return Arrays.equals(elements, ((SimpleKeyN) o).elements);
        }
        if (o instanceof SimpleKey) {
            return SimpleKeyBack.equals(this, (SimpleKey) o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Object v : elements) {
            result = 31 * result + Objects.hashCode(v);
        }
        return result;
    }

    @Override
    public @Nonnull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("k:[");
        for (Object v : elements) {
            sb.append(v);
            sb.append(", ");
        }
        if (sb.length() != 3) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}