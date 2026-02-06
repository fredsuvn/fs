package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
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

    private final @Nonnull List<Object> delegate;
    private final @Nonnull ObjectConverter converter;
    private final @Nonnull Option<?, ?> @Nonnull [] options;

    DataListImpl(
        @Nonnull List<?> delegate,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull [] options
    ) {
        this.delegate = Fs.as(delegate);
        this.converter = converter;
        this.options = options;
    }

    @Override
    public <T> T get(int index, @Nonnull Type type, T defaultValue) throws DataException {
        try {
            if (!Checker.isInBounds(index, 0, delegate.size())) {
                return defaultValue;
            }
            return Fs.as(converter.convert(delegate.get(index), type, options));
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    @Override
    public @Nonnull List<Object> toObject(Type type) throws ObjectException {
        ParameterizedType listType = TypeKit.parameterizedType(List.class, new Type[]{type});
        return Fs.as(converter.convert(this, listType, options));
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
    public Iterator<Object> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
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

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Object> operator) {
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
    public ListIterator<Object> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<Object> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super Object> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Stream<Object> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<Object> parallelStream() {
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
        if (!(object instanceof DataList)) {
            return false;
        }
        return contentEquals((DataList) object);
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
