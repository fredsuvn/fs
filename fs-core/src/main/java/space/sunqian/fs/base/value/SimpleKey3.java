package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ValueClass;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SimpleKey3} represents a key that consists of a list of elements, it is the implementation of
 * {@link SimpleKey} containing {@code 3} elements.
 *
 * @author sunqian
 * @see SimpleKey
 */
@ValueClass
@Immutable
public final class SimpleKey3 implements SimpleKey {

    /**
     * Creates a new {@link SimpleKey3} instance containing the specified elements.
     *
     * @param first  the first element to include in the key
     * @param second the second element to include in the key
     * @param third  the third element to include in the key
     * @return a new {@link SimpleKey3} containing the specified elements
     */
    public static @Nonnull SimpleKey3 of(Object first, Object second, Object third) {
        return new SimpleKey3(first, second, third);
    }

    private final Object first;
    private final Object second;
    private final Object third;

    private SimpleKey3(Object first, Object second, Object third) {
        this.first = first;
        this.second = second;
        this.third = third;
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

    /**
     * Returns the third element in this key, cast to the specified type.
     *
     * @param <T> the target type for casting
     * @return the third element, cast to type {@code T}
     * @throws ClassCastException if the third element cannot be cast to the specified type
     */
    public <T> T thirdAs() throws ClassCastException {
        return Fs.as(third);
    }

    /**
     * Returns the third element in this key.
     *
     * @return the third element
     */
    public Object third() {
        return third;
    }

    @Override
    @SuppressWarnings("EnhancedSwitchMigration")
    public <T> T getAs(int index) throws IndexOutOfBoundsException, ClassCastException {
        switch (index) {
            case 0:
                return firstAs();
            case 1:
                return secondAs();
            case 2:
                return thirdAs();
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
            case 2:
                return third();
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    @Override
    public @Nonnull List<Object> elements() {
        return ListKit.list(first, second, third);
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SimpleKey3) {
            return Objects.equals(first, ((SimpleKey3) o).first) &&
                Objects.equals(second, ((SimpleKey3) o).second) &&
                Objects.equals(third, ((SimpleKey3) o).third);
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
        result = 31 * result + Objects.hashCode(third);
        return result;
    }

    @Override
    public @Nonnull String toString() {
        return "k:[" + first + ", " + second + ", " + third + "]";
    }
}