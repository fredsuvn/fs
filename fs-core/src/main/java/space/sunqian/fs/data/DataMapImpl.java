package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.value.Var;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

final class DataMapImpl implements DataMap {

    private static final @Nonnull Object NONE = new Object();

    private final @Nonnull Map<String, Object> delegate;
    private final @Nonnull ObjectConverter converter;
    private final @Nonnull Option<?, ?> @Nonnull [] options;

    DataMapImpl(
        @Nonnull Map<String, Object> delegate,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull [] options
    ) {
        this.delegate = delegate;
        this.converter = converter;
        this.options = options;
    }

    @Override
    public <T> T get(@Nonnull String key, @Nonnull Type type, T defaultValue) throws DataException {
        try {
            Var<Object> var = Var.of(NONE);
            Object object = this.delegate.computeIfAbsent(
                key,
                k -> {
                    var.set(defaultValue);
                    return null;
                }
            );
            if (var.get() != NONE) {
                return defaultValue;
            }
            return Fs.as(converter.convert(object, type, options));
        } catch (Exception e) {
            throw new DataException(e);
        }
    }

    @Override
    public @Nonnull Object toObject(Type type) throws ObjectException {
        return converter.convertMap(this, type, options);
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
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return delegate.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return delegate.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        delegate.replaceAll(function);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return delegate.replace(key, value);
    }

    @Override
    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataMap)) {
            return false;
        }
        return contentEquals((DataMap) object);
    }

    @Override
    public boolean contentEquals(@Nullable Map<String, Object> o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
