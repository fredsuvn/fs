package space.sunqian.fs.cache;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Function;

final class CacheBack {

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

    static <K, V> @Nonnull CacheFunction<K, V> ofMap(@Nonnull Map<K, V> map) {
        return new CacheFunctionImpl<>(map);
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

    private static final class CacheFunctionImpl<K, V> implements CacheFunction<K, V> {

        private final @Nonnull Map<K, V> map;

        CacheFunctionImpl(@Nonnull Map<K, V> map) {
            this.map = map;
        }

        @Override
        public V get(K key, Function<? super K, ? extends V> loader) {
            return map.computeIfAbsent(key, loader);
        }
    }

    private CacheBack() {
    }
}
