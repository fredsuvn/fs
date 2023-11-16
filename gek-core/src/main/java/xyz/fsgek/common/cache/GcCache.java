package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.ref.GekRef;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class GcCache<K, V> implements GekCache<K, V> {

    private final GekCache.RemoveListener<K, V> removeListener;
    private final boolean isSoft;
    private final Map<K, Entry<K, V>> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final long defaultExpirationMillis;

    GcCache(
        boolean isSoft, long defaultExpirationMillis, int initialCapacity, @Nullable RemoveListener<K, V> removeListener) {
        this.isSoft = isSoft;
        this.defaultExpirationMillis = defaultExpirationMillis;
        this.map = initialCapacity <= 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
    }

    @Override
    public @Nullable V get(K key) {
        Entry<K, V> entry = map.get(key);
        Value v = checkEntry(entry);
        cleanUp();
        return v == null ? null : v.value;
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry<K, V> entry = map.get(key);
        Value v = checkEntry(entry);
        if (v != null) {
            cleanUp();
            return v.value;
        }
        GekRef<Value> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            Value oldValue = checkEntry(old);
            if (oldValue != null) {
                result.set(oldValue);
                return old;
            }
            Value newValue = new Value(loader.apply(k), defaultExpirationMillis);
            result.set(newValue);
            return newEntry(k, newValue);
        });
        cleanUp();
        return result.get().value;
    }

    @Override
    public @Nullable Optional<V> getOptional(K key) {
        Entry<K, V> entry = map.get(key);
        Value v = checkEntry(entry);
        cleanUp();
        return v == null ? null : Optional.ofNullable(v.value);
    }

    @Override
    public @Nullable Optional<V> getOptional(K key, Function<? super K, @Nullable ValueInfo<? extends V>> loader) {
        Entry<K, V> entry = map.get(key);
        Value v = checkEntry(entry);
        if (v != null) {
            cleanUp();
            return Optional.ofNullable(v.value);
        }
        GekRef<Value> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            Value oldValue = checkEntry(old);
            if (oldValue != null) {
                result.set(oldValue);
                return old;
            }
            ValueInfo<? extends V> vi = loader.apply(k);
            if (vi == null) {
                return null;
            }
            Value newValue = new Value(vi.get(), vi.expirationMillis());
            result.set(newValue);
            return newEntry(k, newValue);
        });
        cleanUp();
        Value rv = result.get();
        return rv == null ? null : Optional.ofNullable(rv.value);
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, defaultExpirationMillis);
    }

    @Override
    public V put(K key, V value, long expirationMillis) {
        Entry<K, V> old = map.put(key, newEntry(key, value, expirationMillis));
        Value v = checkEntry(old);
        cleanUp();
        return v == null ? null : v.value;
    }

    @Override
    public V put(K key, V value, @Nullable Duration expiration) {
        return put(key, value, expiration == null ? defaultExpirationMillis : expiration.toMillis());
    }

    @Override
    public void expire(K key, long expiration) {
        Entry<K, V> entry = map.get(key);
        Value v = checkEntry(entry);
        if (v != null) {
            v.refreshExpiration(expiration);
        }
        cleanUp();
    }

    @Override
    public void expire(K key, @Nullable Duration expiration) {
        expire(key, expiration == null ? defaultExpirationMillis : expiration.toMillis());
    }

    @Override
    public void remove(K key) {
        Entry<K, V> entry = map.remove(key);
        entry.clear();
        cleanUp();
    }

    @Override
    public void removeIf(BiPredicate<K, V> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K, V> entry = it.getValue();
            Value v = checkEntry(entry);
            if (v == null) {
                return true;
            }
            if (predicate.test(key, v.value)) {
                entry.clear();
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public void removeEntry(BiPredicate<K, ValueInfo<V>> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K, V> entry = it.getValue();
            Value v = checkEntry(entry);
            if (v == null) {
                return true;
            }
            if (predicate.test(key, ValueInfo.of(v.value, v.expiration))) {
                entry.clear();
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public int size() {
        cleanUp();
        return map.size();
    }

    @Override
    public void clear() {
        removeIf((k, v) -> true);
    }

    @Override
    public void cleanUp() {
        try {
            while (true) {
                Object x = queue.poll();
                if (x == null) {
                    break;
                }
                Entry<K, V> entry = Gek.as(x);
                K key = entry.key();
                map.compute(key, (k, v) -> {
                    if (v == entry) {
                        return null;
                    }
                    return v;
                });
                if (removeListener != null) {
                    removeListener.onRemove(key, null, this);
                }
            }
        } finally {
            //inCleanUp = false;
        }
    }

    @Nullable
    private Value checkEntry(@Nullable Entry<K, V> entry) {
        if (entry == null) {
            return null;
        }
        Value value = entry.value();
        if (value == null) {
            return null;
        }
        if (value.isExpired()) {
            entry.clear();
            return null;
        }
        return value;
    }

    private Entry<K, V> newEntry(K key, @Nullable V value, long expiration) {
        Value v = new Value(value, expiration);
        return newEntry(key, v);
    }

    private Entry<K, V> newEntry(K key, Value value) {
        return isSoft ? new SoftEntry(key, value) : new WeakEntry(key, value);
    }

    private interface Entry<K, V> {

        K key();

        GcCache<K, V>.Value value();

        void clear();
    }

    private final class SoftEntry extends SoftReference<Value> implements Entry<K, V> {

        private final K key;

        public SoftEntry(K key, Value referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Value value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.enqueue();
        }
    }

    private final class WeakEntry extends WeakReference<Value> implements Entry<K, V> {

        private final K key;

        public WeakEntry(K key, Value referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Value value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.enqueue();
        }
    }

    private final class Value {
        private final V value;
        private long startTime;
        private long expiration;

        private Value(V value, long expiration) {
            this.value = value;
            this.expiration = expiration;
            this.startTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            if (expiration < 0) {
                if (defaultExpirationMillis < 0) {
                    return false;
                }
                long now = System.currentTimeMillis();
                return now > startTime + defaultExpirationMillis;
            }
            long now = System.currentTimeMillis();
            return now > startTime + expiration;
        }

        public void refreshExpiration(long expiration) {
            this.expiration = expiration;
            this.startTime = System.currentTimeMillis();
        }
    }
}
