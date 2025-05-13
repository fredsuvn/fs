package xyz.sunqian.common.collection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class CollectionBack {

    static <T> Collection<T> immutableColl(T[] array) {
        return new ImmutableColl<>(array);
    }

    private static final class ImmutableColl<T>
        extends AbstractCollection<T> implements Serializable {

        private static final long serialVersionUID = 0L;

        private final T[] array;

        private ImmutableColl(T[] array) {
            this.array = array;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {

                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < array.length;
                }

                @Override
                public T next() {
                    if (i >= array.length) {
                        throw new NoSuchElementException();
                    }
                    T t = array[i];
                    i++;
                    return t;
                }
            };
        }

        @Override
        public int size() {
            return array.length;
        }
    }
}
