package space.sunqian.fs.cache;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.Val;
import space.sunqian.fs.base.value.Var;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class SimpleCacheBack {

    static <K, V> @Nonnull SimpleCache<K, V> ofWeak() {
        return new WeakCache<>();
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofSoft() {
        return new SoftCache<>();
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofPhantom() {
        return new PhantomCache<>();
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofStrong() {
        return new StrongCache<>();
    }

    static <K, V> @Nonnull SimpleCache<K, V> ofMap(@Nonnull Map<K, V> map) {
        return new MapCache<>(map);
    }

    private static <K, V> void compareAndRemove(@Nonnull Map<K, V> map, K key, V value) {
        V v = map.get(key);
        if (v == value) {
            map.remove(key);
        }

        // map.computeIfPresent(key, (k, old) -> {
        //     if (old == value) {
        //         return null;
        //     }
        //     return old;
        // });
    }

    private static abstract class ReferenceCache<K, V> extends AbstractSimpleCache<K, V> {

        protected final @Nonnull ReferenceQueue<Object> queue = new ReferenceQueue<>();

        private ReferenceCache() {
            super(new ConcurrentHashMap<>());
        }

        @Override
        public void clean() {
            while (true) {
                @Nullable Reference<?> reference = queue.poll();
                if (reference == null) {
                    return;
                }
                Value<K> rv = Fs.as(reference);
                compareAndRemove(cacheMap, rv.key(), rv);
            }
        }
    }

    private static final class WeakCache<K, V> extends ReferenceCache<K, V> {

        @Override
        protected @Nonnull Value<K> generate(@Nonnull K key, @Nonnull Object value) {
            return new WeakValue(key, value);
        }

        private final class WeakValue extends WeakReference<Object> implements Value<K> {

            private final @Nonnull K key;

            WeakValue(@Nonnull K key, @Nonnull Object value) {
                super(value, queue);
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
    }

    private static final class SoftCache<K, V> extends ReferenceCache<K, V> {

        @Override
        protected @Nonnull Value<K> generate(@Nonnull K key, @Nonnull Object value) {
            return new SoftValue(key, value);
        }

        private final class SoftValue extends SoftReference<Object> implements Value<K> {

            private final @Nonnull K key;

            SoftValue(@Nonnull K key, @Nonnull Object value) {
                super(value, queue);
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
    }

    private static final class PhantomCache<K, V> extends ReferenceCache<K, V> {

        @Override
        protected @Nonnull Value<K> generate(@Nonnull K key, @Nonnull Object value) {
            return new PhantomValue(key, value);
        }

        private final class PhantomValue extends PhantomReference<Object> implements Value<K> {

            private final @Nonnull K key;

            PhantomValue(@Nonnull K key, @Nonnull Object value) {
                super(value, queue);
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
    }

    private static final class StrongCache<K, V> extends AbstractSimpleCache<K, V> {

        private StrongCache() {
            super(new ConcurrentHashMap<>());
        }

        @Override
        public void clean() {
        }

        @Override
        protected @Nonnull Value<K> generate(@Nonnull K key, @Nonnull Object value) {
            return new StrongValue(key, value);
        }

        private final class StrongValue implements Value<K> {

            private final @Nonnull K key;
            private @Nullable Object value;

            StrongValue(@Nonnull K key, @Nonnull Object value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public @Nonnull K key() {
                return key;
            }

            @Override
            public @Nullable Object refValue() {
                return value;
            }

            @Override
            public void invalid() {
                value = null;
                compareAndRemove(cacheMap, key(), StrongValue.this);
            }
        }
    }

    private static final class MapCache<K, V> implements SimpleCache<K, V> {

        private static final @Nonnull Object NONE = new Object();
        private final @Nonnull Map<K, V> cacheMap;

        private MapCache(@Nonnull Map<K, V> cacheMap) {
            this.cacheMap = cacheMap;
        }

        @Override
        public V get(@Nonnull K key) {
            return cacheMap.get(key);
        }

        @Override
        public @Nullable Val<V> getVal(@Nonnull K key) {
            Var<Object> var = Var.of(null);
            V v = cacheMap.computeIfAbsent(key, k -> {
                var.set(NONE);
                return null;
            });
            if (var.get() == NONE) {
                return null;
            }
            return Val.of(v);
        }

        @Override
        public V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends V> loader) {
            return cacheMap.computeIfAbsent(key, loader);
        }

        @Override
        public @Nullable Val<V> getVal(
            @Nonnull K key,
            @Nonnull Function<? super @Nonnull K, ? extends @Nullable Val<? extends V>> loader
        ) {
            Var<Object> var = Var.of(null);
            V v = cacheMap.computeIfAbsent(key, k -> {
                Val<? extends V> newV = loader.apply(key);
                if (newV == null) {
                    var.set(NONE);
                    return null;
                } else {
                    return newV.get();
                }
            });
            if (var.get() == NONE) {
                return null;
            }
            return Val.of(v);
        }

        @Override
        public void put(@Nonnull K key, V value) {
            cacheMap.put(key, value);
        }

        @Override
        public void remove(@Nonnull K key) {
            cacheMap.remove(key);
        }

        @Override
        public int size() {
            return cacheMap.size();
        }

        @Override
        public void clear() {
            cacheMap.clear();
        }

        @Override
        public void clean() {
        }

        @Override
        public @Nonnull Map<K, V> copyEntries() {
            return new LinkedHashMap<>(cacheMap);
        }
    }

    private SimpleCacheBack() {
    }
}
