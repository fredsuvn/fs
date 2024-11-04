package xyz.sunqian.common.cache;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.ref.Val;
import xyz.sunqian.common.ref.Var;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class CacheImpl<K, V> implements Cache<K, V> {

    private final boolean useSoft;
    private final Map<K, ValueReference<K>> data;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final RemovalListener<? super K, ? super V> removalListener;

    CacheImpl(boolean useSoft, int initialCapacity, @Nullable RemovalListener<? super K, ? super V> removalListener) {
        this.useSoft = useSoft;
        this.data = new ConcurrentHashMap<>(initialCapacity);
        this.removalListener = removalListener;
    }

    @Override
    public @Nullable V get(K key) {
        ValueReference<K> old = data.get(key);
        if (old != null) {
            Object oldValue = resolveGet(old);
            return resolveValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Override
    public @Nullable Val<V> getVal(K key) {
        ValueReference<K> old = data.get(key);
        if (old != null) {
            Object oldValue = resolveGet(old);
            return valValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Nullable
    private Object resolveGet(ValueReference<K> old) {
        Object value = resolveExpiry(old);
        cleanUp();
        return value;
    }

    @Override
    public @Nullable V put(K key, V value) {
        ValueReference<K> old = data.put(key, newEntry(key, value, null));
        if (old != null) {
            Object oldValue = resolvePut(old);
            return resolveValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Override
    public @Nullable Val<V> putVal(K key, Value<? extends V> value) {
        ValueReference<K> old = data.put(key, newEntry(key, value.getData(), value.getExpiry()));
        if (old != null) {
            Object oldValue = resolvePut(old);
            return valValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Nullable
    private Object resolvePut(ValueReference<K> old) {
        Object value = resolveExpiry(old);
        old.remove(RemovalListener.Cause.REPLACED);
        cleanUp();
        return value;
    }

    @Override
    public @Nullable V remove(K key) {
        ValueReference<K> old = data.remove(key);
        if (old != null) {
            Object oldValue = resolveRemove(old);
            return resolveValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Override
    public @Nullable Val<V> removeVal(K key) {
        ValueReference<K> old = data.remove(key);
        if (old != null) {
            Object oldValue = resolveRemove(old);
            return valValue(oldValue);
        }
        cleanUp();
        return null;
    }

    @Nullable
    private Object resolveRemove(ValueReference<K> old) {
        Object value = resolveExpiry(old);
        old.remove(RemovalListener.Cause.EXPLICIT);
        cleanUp();
        return value;
    }

    @Override
    public @Nullable V compute(K key, Function<? super K, ? extends V> loader) {
        Var<V> result = Var.ofNull();
        data.compute(key, (k, old) -> {
            if (old != null) {
                Object oldValue = resolveGet(old);
                if (oldValue != null) {
                    result.set(resolveValue(oldValue));
                    return old;
                }
            }
            V newValue = loader.apply(k);
            result.set(newValue);
            return newEntry(k, newValue, null);
        });
        return result.get();
    }

    @Override
    public @Nullable Val<V> computeVal(K key, Function<? super K, @Nullable ? extends Value<? extends V>> loader) {
        Var<V> result = Var.ofNull();
        Object isNull = data.compute(key, (k, old) -> {
            if (old != null) {
                Object oldValue = resolveGet(old);
                if (oldValue != null) {
                    result.set(resolveValue(oldValue));
                    return old;
                }
            }
            Value<? extends V> newValue = loader.apply(k);
            if (newValue == null) {
                return null;
            }
            result.set(newValue.getData());
            return newEntry(k, newValue.getData(), newValue.getExpiry());
        });
        return isNull == null ? null : result;
    }

    @Override
    public boolean contains(K key) {
        ValueReference<K> old = data.get(key);
        if (old != null) {
            Object oldValue = resolveGet(old);
            return oldValue != null;
        }
        cleanUp();
        return false;
    }

    @Override
    public void expire(K key, @Nullable Duration expire) {
        ValueReference<K> old = data.get(key);
        if (old != null) {
            Object oldValue = resolveGet(old);
            if (oldValue != null) {
                old.expire(expire);
                return;
            }
        }
        cleanUp();
    }

    @Override
    public int size() {
        cleanUp();
        return data.size();
    }

    @Override
    public void clear() {
        data.entrySet().removeIf(it -> {
            ValueReference<K> entry = it.getValue();
            if (entry == null) {
                return true;
            }
            if (entry.isExpired()) {
                entry.remove(RemovalListener.Cause.EXPIRED);
                return true;
            }
            entry.remove(RemovalListener.Cause.EXPLICIT);
            return true;
        });
        cleanUp();
    }

    @Override
    public void cleanUp() {
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                break;
            }
            ValueReference<K> entry = Jie.as(x);
            K key = entry.key();
            data.computeIfPresent(key, (k, v) -> {
                if (v == entry) {
                    return null;
                }
                return v;
            });
            if (CacheImpl.this.removalListener != null) {
                RemovalListener.Cause cause = entry.syncCause(RemovalListener.Cause.COLLECTED);
                CacheImpl.this.removalListener.onRemoval(key, resolveValue(entry.value()), cause);
            }
        }
    }

    @Nullable
    private Object resolveExpiry(ValueReference<K> old) {
        if (old.isExpired()) {
            old.remove(RemovalListener.Cause.EXPIRED);
            cleanUp();
            return null;
        }
        Object value = old.value();
        if (value == null) {
            cleanUp();
            return null;
        }
        return value;
    }

    private ValueReference<K> newEntry(K key, @Nullable V value, @Nullable Duration expiry) {
        Object v = value == null ? new Null() : value;
        return useSoft ?
            new SoftValueReference(key, v, expiry)
            :
            new WeakValueReference(key, value, expiry);
    }

    @Nullable
    private V resolveValue(@Nullable Object value) {
        if (value == null || value instanceof Null) {
            return null;
        }
        return Jie.as(value);
    }

    @Nullable
    private Val<V> valValue(@Nullable Object value) {
        return value == null ? null : Val.of(resolveValue(value));
    }

    protected interface ValueReference<K> {

        K key();

        Object value();

        boolean isExpired();

        void expire(@Nullable Duration expiry);

        void remove(RemovalListener.Cause cause);

        RemovalListener.Cause syncCause(RemovalListener.Cause cause);
    }

    private final class SoftValueReference extends SoftReference<Object> implements ValueReference<K> {

        private final K key;
        private Instant startTime;
        private Instant expiryAt;
        private volatile RemovalListener.Cause cause;

        public SoftValueReference(K key, Object referent, @Nullable Duration expiry) {
            super(referent, CacheImpl.this.queue);
            this.key = key;
            if (expiry != null) {
                startTime = Instant.now();
                expiryAt = startTime.plus(expiry);
            }
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Object value() {
            return super.get();
        }

        public boolean isExpired() {
            if (expiryAt == null) {
                return false;
            }
            return Instant.now().compareTo(expiryAt) > 0;
        }

        @Override
        public void expire(@Nullable Duration expiry) {
            if (expiry == null) {
                startTime = null;
                expiryAt = null;
            } else {
                startTime = Instant.now();
                expiryAt = startTime.plus(expiry);
            }
        }

        @Override
        public void remove(RemovalListener.Cause cause) {
            syncCause(cause);
            super.enqueue();
        }

        @Override
        public RemovalListener.Cause syncCause(RemovalListener.Cause cause) {
            if (CacheImpl.this.removalListener == null) {
                return cause;
            }
            RemovalListener.Cause thisCause = this.cause;
            if (thisCause != null) {
                return thisCause;
            }
            synchronized (SoftValueReference.this) {
                RemovalListener.Cause syncCause = this.cause;
                if (syncCause != null) {
                    return syncCause;
                }
                this.cause = cause;
                return cause;
            }
        }
    }

    private final class WeakValueReference extends WeakReference<Object> implements ValueReference<K> {

        private final K key;
        private Instant startTime;
        private Instant expiryAt;
        private RemovalListener.Cause cause;

        public WeakValueReference(K key, Object referent, @Nullable Duration expiry) {
            super(referent, CacheImpl.this.queue);
            this.key = key;
            if (expiry != null) {
                startTime = Instant.now();
                expiryAt = startTime.plus(expiry);
            }
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Object value() {
            return super.get();
        }

        public boolean isExpired() {
            if (expiryAt == null) {
                return false;
            }
            return Instant.now().compareTo(expiryAt) > 0;
        }

        @Override
        public void expire(@Nullable Duration expiry) {
            if (expiry == null) {
                startTime = null;
                expiryAt = null;
            } else {
                startTime = Instant.now();
                expiryAt = startTime.plus(expiry);
            }
        }

        @Override
        public void remove(RemovalListener.Cause cause) {
            syncCause(cause);
            super.enqueue();
        }

        @Override
        public RemovalListener.Cause syncCause(RemovalListener.Cause cause) {
            if (CacheImpl.this.removalListener == null) {
                return cause;
            }
            RemovalListener.Cause thisCause = this.cause;
            if (thisCause != null) {
                return thisCause;
            }
            synchronized (WeakValueReference.this) {
                RemovalListener.Cause syncCause = this.cause;
                if (syncCause != null) {
                    return syncCause;
                }
                this.cause = cause;
                return cause;
            }
        }
    }

    private static final class Null {
    }
}
