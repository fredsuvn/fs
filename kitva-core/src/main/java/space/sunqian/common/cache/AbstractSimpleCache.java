package space.sunqian.common.cache;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.value.Val;
import space.sunqian.common.base.value.Var;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * This is a skeletal implementation of the {@link SimpleCache} to minimize the effort required to implement the
 * interface. To implement a cache, just need to override the {@link #generate(Object, Object)} and {@link #clean()}.
 * <p>
 * This implementation is based on an underlying {@link ConcurrentMap}, which is a protected field: {@link #cacheMap}.
 * The type of {@link #cacheMap}'s value is {@link Value}, which wraps the actual value to be cached. Every time this
 * cache is invoked, {@link #clean()} is executed to clear expired values. Therefore, it is necessary to correctly
 * implement the storage and invalidation behavior of cached data in {@link #generate(Object, Object)} and
 * {@link #clean()}.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author sunqian
 */
public abstract class AbstractSimpleCache<K, V> implements SimpleCache<K, V> {

    private static final @Nonnull Object NULL_VAL = "You can't see me!";

    /**
     * The underlying cache map, which is used to store the cache value.
     */
    protected final @Nonnull ConcurrentMap<K, Value<K>> cacheMap = new ConcurrentHashMap<>();

    /**
     * Generates a cache value wrapper with the given cache key and cache value. Note the value is masked and non-null.
     *
     * @param key   the given cache key
     * @param value the given cache value which is masked and non-null
     * @return the cache value wrapper
     */
    protected abstract @Nonnull Value<K> generate(@Nonnull K key, @Nonnull Object value);

    @Override
    public @Nullable V get(@Nonnull K key) {
        clean();
        @Nullable Value<K> cv = cacheMap.get(key);
        if (cv == null) {
            return null;
        }
        @Nullable Object raw = cv.refValue();
        if (raw == null) {
            return null;
        }
        return unmaskRawValue(raw);
    }

    @Override
    public @Nullable Val<@Nullable V> getVal(@Nonnull K key) {
        clean();
        @Nullable Value<K> cv = cacheMap.get(key);
        if (cv == null) {
            return null;
        }
        @Nullable Object raw = cv.refValue();
        if (raw == null) {
            return null;
        }
        return Val.of(unmaskRawValue(raw));
    }

    @Override
    public @Nullable V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends @Nullable V> producer) {
        clean();
        @Nullable Value<K> cv = cacheMap.get(key);
        if (cv != null) {
            @Nullable Object raw = cv.refValue();
            if (raw != null) {
                return unmaskRawValue(raw);
            }
        }
        Var<V> value = Var.of(null);
        cacheMap.compute(key, (k, old) -> {
            if (old != null) {
                @Nullable Object raw = old.refValue();
                if (raw != null) {
                    value.set(unmaskRawValue(raw));
                    return old;
                }
            }
            @Nullable V newV = producer.apply(key);
            value.set(newV);
            return generate(key, maskValue(newV));
        });
        return Kit.as(value.get());
    }

    @Override
    public @Nullable Val<V> getVal(
        @Nonnull K key,
        @Nonnull Function<? super @Nonnull K, ? extends @Nullable Val<? extends @Nullable V>> producer
    ) {
        clean();
        @Nullable Value<K> cv = cacheMap.get(key);
        if (cv != null) {
            @Nullable Object raw = cv.refValue();
            if (raw != null) {
                return Val.of(unmaskRawValue(raw));
            }
        }
        Var<Object> value = Var.of(null);
        cacheMap.compute(key, (k, old) -> {
            if (old != null) {
                @Nullable Object raw = old.refValue();
                if (raw != null) {
                    value.set(unmaskRawValue(raw));
                    return old;
                }
            }
            @Nullable Val<? extends V> newV = producer.apply(key);
            if (newV == null) {
                value.set(NULL_VAL);
                return null;
            }
            @Nullable V nv = newV.get();
            value.set(nv);
            return generate(key, maskValue(nv));
        });
        @Nullable Object v = value.get();
        if (v == NULL_VAL) {
            return null;
        }
        return Val.of(Kit.as(v));
    }

    @Override
    public void put(@Nonnull K key, @Nullable V value) {
        clean();
        Value<K> newValue = generate(key, maskValue(value));
        @Nullable Value<K> old = cacheMap.put(key, newValue);
        if (old != null) {
            old.invalid();
        }
    }

    @Override
    public void remove(@Nonnull K key) {
        clean();
        @Nullable Value<K> old = cacheMap.remove(key);
        if (old != null) {
            old.invalid();
        }
    }

    @Override
    public int size() {
        clean();
        return cacheMap.size();
    }

    @Override
    public void clear() {
        cacheMap.forEach((k, v) -> {
            v.invalid();
        });
        clean();
    }

    private @Nonnull Object maskValue(@Nullable V value) {
        return value == null ? new Null() : value;
    }

    private @Nullable V unmaskRawValue(@Nonnull Object raw) {
        if (raw instanceof Null) {
            return null;
        }
        return Kit.as(raw);
    }

    private static final class Null {
        private Null() {
        }
    }

    /**
     * This interface represents the cache value, which is used to wrap the actual value to be cached.
     *
     * @param <K> the key type
     */
    public interface Value<K> {

        /**
         * Returns the cache key.
         *
         * @return the cache key
         */
        @Nonnull
        K key();

        /**
         * Returns the actual value to be cached, may be {@code null} if the value is invalid.
         *
         * @return the actual value to be cached, may be {@code null} if the value is invalid
         */
        @Nullable
        Object refValue();

        /**
         * Invalidates the cache value, the actual cached value should be cleared.
         */
        void invalid();
    }
}
