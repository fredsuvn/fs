package xyz.sunqian.common.cache;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.base.value.Var;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

final class SimpleCacheBack {

    static <K, V> SimpleCache<K, V> newSimpleCache(
        boolean isWeak,
        @Nullable Duration duration,
        @Nullable SimpleCache.RemovalListener<K, V> removalListener
    ) {
        return new ReferenceCache<>(isWeak, duration, removalListener);
    }

    private static <V> Object maskValue(@Nullable V value) {
        return value == null ? new Null() : value;
    }

    private static <V> @Nullable V unmaskRawValue(Object raw) {
        if (raw instanceof Null) {
            return null;
        }
        return Jie.as(raw);
    }

    private static class ReferenceCache<K, V> implements SimpleCache<K, V> {

        private static final Object NULL_VAL = "(╯‵□′)╯︵┻━┻";

        private static final ReferenceValueGenerator WeakGenerator = WeakReferenceValue::new;
        private static final ReferenceValueGenerator SoftGenerator = SoftReferenceValue::new;
        private static final RemovalListener<?, ?> EMPTY_LISTENER =
            (key, value, cause) -> {};

        private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        private final ConcurrentMap<Object, ReferenceValue<K, V>> map = new ConcurrentHashMap<>();

        private final @Nullable Duration duration;
        private final RemovalListener<K, V> removalListener;
        private final ReferenceValueGenerator valueGenerator;

        private ReferenceCache(
            boolean isWeak,
            @Nullable Duration duration,
            @Nullable RemovalListener<K, V> removalListener
        ) {
            this.duration = duration;
            this.removalListener = removalListener == null ? Jie.as(EMPTY_LISTENER) : removalListener;
            this.valueGenerator = isWeak ? WeakGenerator : SoftGenerator;
        }

        @Override
        public @Nullable V get(K key) {
            clean();
            ReferenceValue<K, V> rv = map.get(key);
            if (rv == null) {
                return null;
            }
            @Nullable Object raw = getRawValue(rv);
            if (raw == null) {
                return null;
            }
            return unmaskRawValue(raw);
        }

        @Override
        public @Nullable Val<V> getVal(K key) {
            clean();
            ReferenceValue<K, V> rv = map.get(key);
            if (rv == null) {
                return null;
            }
            @Nullable Object raw = getRawValue(rv);
            if (raw == null) {
                return null;
            }
            return Val.of(unmaskRawValue(raw));
        }

        @Override
        public <V1 extends V> @Nullable V get(K key, Function<? super K, @Nullable V1> producer) {
            clean();
            Var<V> value = Var.of(null);
            map.compute(key, (k, old) -> {
                if (old != null) {
                    @Nullable Object raw = getRawValue(old);
                    if (raw != null) {
                        value.set(unmaskRawValue(raw));
                        return old;
                    }
                }
                V1 v1 = producer.apply(key);
                value.set(v1);
                return newReferenceValue(key, maskValue(v1), expiration(null));
            });
            return Jie.as(value.get());
        }

        @Override
        public @Nullable <V1 extends ValueInfo<? extends V>> Val<V> getVal(
            K key, Function<? super K, @Nullable V1> producer) {
            clean();
            Var<Object> value = Var.of(null);
            map.compute(key, (k, old) -> {
                if (old != null) {
                    @Nullable Object raw = getRawValue(old);
                    if (raw != null) {
                        value.set(unmaskRawValue(raw));
                        return old;
                    }
                }
                V1 v1 = producer.apply(key);
                if (v1 == null) {
                    value.set(NULL_VAL);
                    return null;
                }
                @Nullable V nv = v1.value();
                value.set(nv);
                return newReferenceValue(key, maskValue(nv), expiration(v1.duration()));
            });
            @Nullable Object v = value.get();
            if (v == NULL_VAL) {
                return null;
            }
            return Val.of(Jie.as(v));
        }

        @Override
        public void put(K key, @Nullable V value) {
            clean();
            ReferenceValue<K, V> old = map.put(key, newReferenceValue(key, maskValue(value), expiration(null)));
            if (old != null) {
                @Nullable Object raw = getRawValue(old);
                if (raw != null) {
                    old.invalid(RemovalCause.REPLACED);
                }
            }
        }

        @Override
        public void put(K key, ValueInfo<? extends V> value) {
            clean();
            ReferenceValue<K, V> old = map.put(
                key,
                newReferenceValue(key, maskValue(value.value()), expiration(value.duration()))
            );
            if (old != null) {
                @Nullable Object raw = getRawValue(old);
                if (raw != null) {
                    old.invalid(RemovalCause.REPLACED);
                }
            }
        }

        @Override
        public void remove(K key) {
            clean();
            ReferenceValue<K, V> old = map.remove(key);
            if (old != null) {
                @Nullable Object raw = getRawValue(old);
                if (raw != null) {
                    old.invalid(RemovalCause.EXPLICIT);
                }
            }
        }

        @Override
        public boolean contains(K key) {
            clean();
            ReferenceValue<K, V> rv = map.get(key);
            if (rv == null) {
                return false;
            }
            return getRawValue(rv) != null;
        }

        @Override
        public void expire(K key, Duration duration) {
            clean();
            ReferenceValue<K, V> rv = map.get(key);
            if (rv == null) {
                return;
            }
            @Nullable Object raw = getRawValue(rv);
            if (raw != null) {
                rv.expiration(expiration(duration));
            }
        }

        @Override
        public int size() {
            clean();
            return map.size();
        }

        @Override
        public void clear() {
            map.forEach((k, rv) -> {
                @Nullable Object raw = getRawValue(rv);
                if (raw != null) {
                    rv.invalid(RemovalCause.EXPLICIT);
                }
            });
            clean();
        }

        @Override
        public void clean() {
            while (true) {
                Reference<?> reference = queue.poll();
                if (reference == null) {
                    return;
                }
                ReferenceValue<K, V> rv = Jie.as(reference);
                doClean(rv);
            }
        }

        private void doClean(ReferenceValue<K, V> rv) {
            K k = rv.key();
            map.compute(k, (k1, old) -> {
                if (old == rv) {
                    return null;
                }
                return old;
            });
            rv.doListener(removalListener);
        }

        @Nullable
        private Object getRawValue(ReferenceValue<K, V> rv) {
            Object raw = rv.rawValue();
            if (raw == null) {
                return null;
            }
            if (isExpired(rv)) {
                return null;
            }
            return raw;
        }

        private boolean isExpired(ReferenceValue<K, V> rv) {
            if (rv.expiration() <= 0) {
                return false;
            }
            boolean expired = System.currentTimeMillis() > rv.expiration();
            if (expired) {
                rv.invalid(RemovalCause.EXPIRED);
            }
            return expired;
        }

        private long expiration(@Nullable Duration d) {
            if (d != null) {
                return System.currentTimeMillis() + d.toMillis();
            }
            if (duration != null) {
                return System.currentTimeMillis() + duration.toMillis();
            }
            return 0;
        }

        private ReferenceValue<K, V> newReferenceValue(K key, Object o, long expiration) {
            return valueGenerator.generate(key, o, expiration, queue);
        }
    }

    private interface ReferenceValueGenerator {

        <K, V> ReferenceValue<K, V> generate(
            K key,
            @Nullable Object value,
            long expiration,
            ReferenceQueue<Object> queue
        );
    }

    private interface ReferenceValue<K, V> {

        K key();

        @Nullable
        Object rawValue();

        long expiration();

        void expiration(long expiration);

        void invalid(SimpleCache.RemovalCause cause);

        void doListener(SimpleCache.RemovalListener<K, V> listener);
    }

    private static final class Null {}

    private static final class WeakReferenceValue<K, V> extends WeakReference<Object> implements ReferenceValue<K, V> {

        private final K key;
        private long expiration;

        /*
         * The cause can be REPLACED, EXPLICIT, or EXPIRED.
         * Null means removed by garbage collection.
         */
        private @Nullable SimpleCache.RemovalCause cause;
        /*
         * Only possible after explicitly calling the invalid method.
         */
        private @Nullable Val<V> heldValue;

        WeakReferenceValue(
            K key,
            Object value,
            long expiration,
            ReferenceQueue<? super Object> q
        ) {
            super(value, q);
            this.key = key;
            this.expiration = expiration;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public @Nullable Object rawValue() {
            return get();
        }

        @Override
        public long expiration() {
            return expiration;
        }

        @Override
        public void expiration(long expiration) {
            this.expiration = expiration;
        }

        @Override
        public void invalid(SimpleCache.RemovalCause cause) {
            // Thread safety is not enforced here.
            this.cause = cause;
            @Nullable Object rawValue = rawValue();
            if (rawValue != null) {
                this.heldValue = Val.of(unmaskRawValue(rawValue));
            }
            enqueue();
        }

        @Override
        public void doListener(SimpleCache.RemovalListener<K, V> listener) {
            listener.onRemoval(key, heldValue, cause);
        }
    }

    private static final class SoftReferenceValue<K, V> extends SoftReference<Object> implements ReferenceValue<K, V> {

        private final K key;
        private long expiration;

        /*
         * The cause can be REPLACED, EXPLICIT, or EXPIRED.
         * Null means removed by garbage collection.
         */
        private @Nullable SimpleCache.RemovalCause cause;
        /*
         * Only possible after explicitly calling the invalid method.
         */
        private @Nullable Val<V> heldValue;

        SoftReferenceValue(
            K key,
            Object value,
            long expiration,
            ReferenceQueue<? super Object> q
        ) {
            super(value, q);
            this.key = key;
            this.expiration = expiration;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public @Nullable Object rawValue() {
            return get();
        }

        @Override
        public long expiration() {
            return expiration;
        }

        @Override
        public void expiration(long expiration) {
            this.expiration = expiration;
        }

        @Override
        public void invalid(SimpleCache.RemovalCause cause) {
            // Thread safety is not enforced here.
            this.cause = cause;
            @Nullable Object rawValue = rawValue();
            if (rawValue != null) {
                this.heldValue = Val.of(unmaskRawValue(rawValue));
            }
            enqueue();
        }

        @Override
        public void doListener(SimpleCache.RemovalListener<K, V> listener) {
            listener.onRemoval(key, heldValue, cause);
        }
    }
}
