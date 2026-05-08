package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ValueClass;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SimpleKey2} represents a key that consists of a list of elements, it is the implementation of
 * {@link SimpleKey} containing {@code 2} elements.
 *
 * @author sunqian
 * @see SimpleKey
 */
@ValueClass
@Immutable
public final class SimpleKey2 implements SimpleKey {

    /**
     * Creates a new {@link SimpleKey2} instance containing the specified elements.
     *
     * @param first  the first element to include in the key
     * @param second the second element to include in the key
     * @return a new {@link SimpleKey2} containing the specified elements
     */
    public static @Nonnull SimpleKey2 of(Object first, Object second) {
        return new SimpleKey2(first, second);
    }

    private final Object first;
    private final Object second;

    private SimpleKey2(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element in this key, cast to the specified type.
     *
     * @param <T> the target type for casting
     * @return the first element, cast to type {@code T}
     * @throws ClassCastException if the first element cannot be cast to the specified type
     */
    public <T> T firstAs() throws ClassCastException {
        return Fs.as(first);
    }

    /**
     * Returns the first element in this key.
     *
     * @return the first element
     */
    public Object first() {
        return first;
    }

    /**
     * Returns the second element in this key, cast to the specified type.
     *
     * @param <T> the target type for casting
     * @return the second element, cast to type {@code T}
     * @throws ClassCastException if the second element cannot be cast to the specified type
     */
    public <T> T secondAs() throws ClassCastException {
        return Fs.as(second);
    }

    /**
     * Returns the second element in this key.
     *
     * @return the second element
     */
    public Object second() {
        return second;
    }

    @Override
    @SuppressWarnings("EnhancedSwitchMigration")
    public <T> T getAs(int index) throws IndexOutOfBoundsException, ClassCastException {
        switch (index) {
            case 0:
                return firstAs();
            case 1:
                return secondAs();
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    @Override
    @SuppressWarnings("EnhancedSwitchMigration")
    public Object get(int index) throws IndexOutOfBoundsException {
        switch (index) {
            case 0:
                return first();
            case 1:
                return second();
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    @Override
    public @Nonnull List<Object> elements() {
        return ListKit.list(first, second);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SimpleKey2) {
            return Objects.equals(first, ((SimpleKey2) o).first) &&
                Objects.equals(second, ((SimpleKey2) o).second);
        }
        if (o instanceof SimpleKey) {
            return SimpleKeyBack.equals(this, (SimpleKey) o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + Objects.hashCode(first);
        result = 31 * result + Objects.hashCode(second);
        return result;
    }

    @Override
    public @Nonnull String toString() {
        return "k:[" + first + ", " + second + "]";
    }
}