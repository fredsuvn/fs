package xyz.sunqian.common.coll;

import xyz.sunqian.common.base.Jie;

import java.io.Serializable;
import java.util.*;

final class Impls {

    static final String UNABLE_NULL_TYPE =
        "Unable to create a new array because all mapped elements are null, preventing the determination of the new array's type.";

    static <T> List<T> immutableList(Object[] array) {
        return new ImmutableList<>(array);
    }

    static <T> Enumeration<T> emptyEnumeration() {
        return Jie.as(EmptyEnumeration.SINGLETON);
    }

    private static final class ImmutableList<T> extends AbstractList<T> implements RandomAccess, Serializable {

        private final Object[] array;

        private ImmutableList(Object[] array) {
            this.array = array;
        }

        @Override
        public T get(int index) {
            return Jie.as(array[index]);
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class EmptyEnumeration implements Enumeration<Object> {

        private static final EmptyEnumeration SINGLETON = new EmptyEnumeration();

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException();
        }
    }
}
