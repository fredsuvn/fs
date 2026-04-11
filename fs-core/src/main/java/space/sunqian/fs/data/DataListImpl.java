package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

final class DataListImpl implements DataList {

    private static final @Nonnull Type LIST_OBJECT_TYPE = new TypeRef<List<?>>() {}.type();

    private final @Nonnull List<Object> delegate;
    private final @Nonnull ObjectConverter converter;
    private final @Nonnull Option<?, ?> @Nonnull [] defaultOptions;

    DataListImpl(
        @Nonnull List<?> delegate,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull [] defaultOptions
    ) {
        this.delegate = Fs.as(delegate);
        this.converter = converter;
        this.defaultOptions = defaultOptions;
    }

    @Override
    public <T> T get(int index, @Nonnull Type type, T defaultValue) throws DataException {
        try {
            if (!Checker.isInBounds(index, 0, delegate.size())) {
                return defaultValue;
            }
            return Fs.as(converter.convert(delegate.get(index), type, defaultOptions));
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    @Override
    public @Nonnull Object toObject(@Nonnull Type type) throws DataException {
        try {
            return Fs.as(converter.convert(this, LIST_OBJECT_TYPE, type, defaultOptions));
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public @Nonnull Iterator<Object> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object @Nonnull [] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T @Nonnull [] toArray(T @Nonnull [] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(Object object) {
        return delegate.add(object);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @SuppressWarnings("SlowListContainsAll")
    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<?> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<?> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void replaceAll(@Nonnull UnaryOperator<Object> operator) {
        delegate.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Object> c) {
        delegate.sort(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Object get(int index) {
        return delegate.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        delegate.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public @Nonnull ListIterator<Object> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public @Nonnull ListIterator<Object> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public @Nonnull List<Object> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public @Nonnull Spliterator<Object> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean removeIf(@Nonnull Predicate<? super Object> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public @Nonnull Stream<Object> stream() {
        return delegate.stream();
    }

    @Override
    public @Nonnull Stream<Object> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super Object> action) {
        delegate.forEach(action);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof List<?>)) {
            return false;
        }
        return contentEquals((List<?>) object);
    }

    @Override
    public boolean contentEquals(@Nullable List<?> o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
