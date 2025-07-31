package xyz.sunqian.common.cache;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.base.value.Var;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

final class CacheImpls {

    private static final @Nonnull RefGenerator WEAK_GENERATOR = WeakRef::new;
    private static final @Nonnull RefGenerator SOFT_GENERATOR = SoftRef::new;
    private static final @Nonnull RefGenerator PHANTOM_GENERATOR = PhantomRef::new;
    private static final @Nonnull RefGenerator STRONG_GENERATOR = StrongRef::new;

    static <K, V> @Nonnull SimpleCache<K, V> ofWeak() {
        return new QueueRefCache<>(WEAK_GENERATOR);
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofSoft() {
        return new QueueRefCache<>(SOFT_GENERATOR);
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofPhantom() {
        return new QueueRefCache<>(PHANTOM_GENERATOR);
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofStrong() {
        return new StrongRefCache<>(STRONG_GENERATOR);
    }

    private static <V> @Nonnull Object maskValue(@Nullable V value) {
        return value == null ? new Null() : value;
    }

    private static <V> @Nullable V unmaskRawValue(@Nonnull Object raw) {
        if (raw instanceof Null) {
            return null;
        }
        return Jie.as(raw);
    }

    private static <K, V> void compareAndRemove(@Nonnull Map<K, V> map, K key, V value) {
        map.computeIfPresent(key, (k, old) -> {
            if (old == value) {
                return null;
            }
            return old;
        });
    }

    private static abstract class RefCache<K, V> implements SimpleCache<K, V> {

        protected static final @Nonnull Object NULL_VAL = "(╯‵□′)╯︵┻━┻";

        protected final @Nonnull ConcurrentMap<Object, Ref<K>> map = new ConcurrentHashMap<>();
        protected final @Nonnull RefGenerator refGenerator;

        protected RefCache(@Nonnull RefGenerator refGenerator) {
            this.refGenerator = refGenerator;
        }

        @Override
        public @Nullable V get(@Nonnull K key) {
            clean();
            @Nullable Ref<K> rv = map.get(key);
            if (rv == null) {
                return null;
            }
            @Nullable Object raw = rv.refValue();
            if (raw == null) {
                return null;
            }
            return unmaskRawValue(raw);
        }

        @Override
        public @Nullable Val<@Nullable V> getVal(@Nonnull K key) {
            clean();
            @Nullable Ref<K> rv = map.get(key);
            if (rv == null) {
                return null;
            }
            @Nullable Object raw = rv.refValue();
            if (raw == null) {
                return null;
            }
            return Val.of(unmaskRawValue(raw));
        }

        @Override
        public @Nullable V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends @Nullable V> producer) {
            clean();
            @Nullable Ref<K> rv = map.get(key);
            if (rv != null) {
                @Nullable Object raw = rv.refValue();
                if (raw != null) {
                    return unmaskRawValue(raw);
                }
            }
            Var<V> value = Var.of(null);
            map.compute(key, (k, old) -> {
                if (old != null) {
                    @Nullable Object raw = old.refValue();
                    if (raw != null) {
                        value.set(unmaskRawValue(raw));
                        return old;
                    }
                }
                @Nullable V newV = producer.apply(key);
                value.set(newV);
                return refGenerator.generate(this, key, maskValue(newV));
            });
            return Jie.as(value.get());
        }

        @Override
        public @Nullable Val<V> getVal(
            @Nonnull K key,
            @Nonnull Function<? super @Nonnull K, ? extends @Nullable Val<? extends @Nullable V>> producer
        ) {
            clean();
            @Nullable Ref<K> rv = map.get(key);
            if (rv != null) {
                @Nullable Object raw = rv.refValue();
                if (raw != null) {
                    return Val.of(unmaskRawValue(raw));
                }
            }
            Var<Object> value = Var.of(null);
            map.compute(key, (k, old) -> {
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
                return refGenerator.generate(this, key, maskValue(nv));
            });
            @Nullable Object v = value.get();
            if (v == NULL_VAL) {
                return null;
            }
            return Val.of(Jie.as(v));
        }

        @Override
        public void put(@Nonnull K key, @Nullable V value) {
            clean();
            Ref<K> newRef = refGenerator.generate(this, key, maskValue(value));
            @Nullable Ref<K> old = map.put(key, newRef);
            if (old != null) {
                old.invalid();
            }
        }

        @Override
        public void remove(@Nonnull K key) {
            clean();
            @Nullable Ref<K> old = map.remove(key);
            if (old != null) {
                old.invalid();
            }
        }

        @Override
        public void clear() {
            map.forEach((k, rv) -> {
                rv.invalid();
            });
            clean();
        }
    }

    private static final class QueueRefCache<K, V> extends RefCache<K, V> {

        private final @Nonnull ReferenceQueue<Object> queue = new ReferenceQueue<>();

        private QueueRefCache(@Nonnull RefGenerator refGenerator) {
            super(refGenerator);
        }

        @Override
        public void clean() {
            while (true) {
                @Nullable Reference<?> reference = queue.poll();
                if (reference == null) {
                    return;
                }
                Ref<K> rv = Jie.as(reference);
                compareAndRemove(map, rv.key(), rv);
            }
        }
    }

    private static final class StrongRefCache<K, V> extends RefCache<K, V> {

        private StrongRefCache(@Nonnull RefGenerator refGenerator) {
            super(refGenerator);
        }

        @Override
        public void clean() {
        }
    }

    private interface RefGenerator {

        <K, V> @Nonnull Ref<K> generate(@Nonnull RefCache<K, V> cache, @Nonnull K key, @Nonnull Object value);
    }

    private interface Ref<K> {

        @Nonnull
        K key();

        @Nullable
        Object refValue();

        void invalid();
    }

    private static final class WeakRef<K, V> extends WeakReference<Object> implements Ref<K> {

        private final @Nonnull K key;

        WeakRef(@Nonnull RefCache<K, V> cache, @Nonnull K key, @Nonnull Object value) {
            super(value, ((QueueRefCache<K, V>) cache).queue);
            this.key = key;
        }

        @Override
        public @Nonnull K key() {
            return key;
        }

        @Override
        public @Nullable Object refValue() {
            return get();
        }

        @Override
        public void invalid() {
            enqueue();
        }
    }

    private static final class SoftRef<K, V> extends SoftReference<Object> implements Ref<K> {

        private final @Nonnull K key;

        SoftRef(@Nonnull RefCache<K, V> cache, @Nonnull K key, @Nonnull Object value) {
            super(value, ((QueueRefCache<K, V>) cache).queue);
            this.key = key;
        }

        @Override
        public @Nonnull K key() {
            return key;
        }

        @Override
        public @Nullable Object refValue() {
            return get();
        }

        @Override
        public void invalid() {
            enqueue();
        }
    }

    private static final class PhantomRef<K, V> extends PhantomReference<Object> implements Ref<K> {

        private final @Nonnull K key;

        PhantomRef(@Nonnull RefCache<K, V> cache, @Nonnull K key, @Nonnull Object value) {
            super(value, ((QueueRefCache<K, V>) cache).queue);
            this.key = key;
        }

        @Override
        public @Nonnull K key() {
            return key;
        }

        @Override
        public @Nullable Object refValue() {
            return get();
        }

        @Override
        public void invalid() {
            enqueue();
        }
    }

    private static final class StrongRef<K, V> implements Ref<K> {

        private final @Nonnull RefCache<K, V> cache;
        private final @Nonnull K key;
        private @Nullable Object value;

        StrongRef(@Nonnull RefCache<K, V> cache, @Nonnull K key, @Nonnull Object value) {
            this.cache = cache;
            this.key = key;
            this.value = value;
        }

        @Override
        public @Nonnull K key() {
            return key;
        }

        @Override
        public Object refValue() {
            return value;
        }

        @Override
        public void invalid() {
            value = null;
            compareAndRemove(cache.map, key(), this);
        }
    }

    private static final class Null {
        private Null() {
        }
    }
}
