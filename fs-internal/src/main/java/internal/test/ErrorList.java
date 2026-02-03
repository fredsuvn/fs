package internal.test;

import space.sunqian.annotation.Nonnull;

import java.util.AbstractList;

/**
 * A list implementation that always throws {@link UnsupportedOperationException} for all operations.
 *
 * @param <E> the type of elements
 */
public class ErrorList<E> extends AbstractList<E> {

    @Override
    public @Nonnull E get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }
}
