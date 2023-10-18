package xyz.fsgik.common.cache;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.base.FsWrapper;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class ReferencedCache<K, V> implements FsCache<K, V> {

    private static final Object NULL = new Object();

    private final FsCache.RemoveListener<K, V> removeListener;
    private final boolean isSoft;

    private final Map<K, Entry> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private volatile boolean inCleanUp = false;

    ReferencedCache(boolean isSoft) {
        this(isSoft, null);
    }

    ReferencedCache(boolean isSoft, RemoveListener<K, V> removeListener) {
        this.isSoft = isSoft;
        this.map = new ConcurrentHashMap<>();
        this.removeListener = removeListener;
    }

    ReferencedCache(boolean isSoft, int initialCapacity, RemoveListener<K, V> removeListener) {
        this.isSoft = isSoft;
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
    }

    @Override
    public @Nullable V get(K key) {
        Entry entry = map.get(key);
        V result = get0(entry);
        cleanUp0();
        return result;
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                return newEntry(k, loader.apply(k));
            }
            Object value = old.getValue();
            if (value == null) {
                old.clear();
                return newEntry(k, loader.apply(k));
            }
            return old;
        });
        V result = get0(entry);
        cleanUp0();
        return result;
    }

    @Override
    public @Nullable FsWrapper<V> getWrapper(K key) {
        Entry entry = map.get(key);
        FsWrapper<V> result = getWrapper0(entry);
        cleanUp0();
        return result;
    }

    @Override
    public @Nullable FsWrapper<V> getWrapper(K key, Function<? super K, @Nullable FsWrapper<? extends V>> loader) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                FsWrapper<? extends V> newValue = loader.apply(k);
                if (newValue == null) {
                    return null;
                }
                return newEntry(k, newValue.get());
            }
            Object value = old.getValue();
            if (value == null) {
                old.clear();
                FsWrapper<? extends V> newValue = loader.apply(k);
                if (newValue == null) {
                    return null;
                }
                return newEntry(k, newValue.get());
            }
            return old;
        });
        FsWrapper<V> result = getWrapper0(entry);
        cleanUp0();
        return result;
    }

    @Override
    public V put(K key, V value) {
        Entry old = map.put(key, newEntry(key, value));
        V result = get0(old);
        cleanUp0();
        return result;
    }

    @Override
    public void remove(K key) {
        Entry entry = map.remove(key);
        if (entry != null) {
            entry.clear();
        }
        cleanUp0();
    }

    @Override
    public void removeIf(BiPredicate<K, V> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry entry = it.getValue();
            Object value = entry.getValue();
            if (value == null) {
                return true;
            }
            if (value == NULL) {
                if (predicate.test(key, null)) {
                    entry.clear();
                    return true;
                } else {
                    return false;
                }
            }
            if (predicate.test(key, Fs.as(value))) {
                entry.clear();
                return true;
            }
            return false;
        });
        cleanUp0();
    }

    @Override
    public int size() {
        cleanUp0();
        return map.size();
    }

    @Override
    public void clear() {
        removeIf((k, v) -> true);
    }

    @Override
    public void cleanUp() {
        cleanUp0();
    }

    @Nullable
    private V get0(@Nullable Entry entry) {
        if (entry == null) {
            return null;
        }
        Object obj = entry.getValue();
        if (obj == null) {
            entry.clear();
            return null;
        }
        if (obj == NULL) {
            return null;
        }
        return Fs.as(obj);
    }

    @Nullable
    private FsWrapper<V> getWrapper0(@Nullable Entry entry) {
        if (entry == null) {
            return null;
        }
        Object obj = entry.getValue();
        if (obj == null) {
            entry.clear();
            return null;
        }
        if (obj == NULL) {
            return FsWrapper.empty();
        }
        return Fs.as(FsWrapper.wrap(obj));
    }

    private Entry newEntry(Object key, @Nullable Object value) {
        Object actualValue = value == null ? NULL : value;
        return isSoft ? new SoftEntry(key, actualValue) : new WeakEntry(key, actualValue);
    }

    private void cleanUp0() {
        if (inCleanUp) {
            return;
        }
        synchronized (this) {
            if (inCleanUp) {
                return;
            }
            inCleanUp = true;
        }
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                break;
            }
            Entry entry = (Entry) x;
            map.remove(entry.getKey());
            //entry.clear();
            if (removeListener != null) {
                removeListener.onRemove(this, Fs.as(entry.getKey()));
            }
        }
        inCleanUp = false;
    }

    private interface Entry {
        Object getKey();

        Object getValue();

        void clear();
    }

    private final class SoftEntry extends SoftReference<Object> implements Entry {

        private final Object key;

        public SoftEntry(Object key, Object referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return get();
        }

        @Override
        public void clear() {
            super.clear();
            if (removeListener != null) {
                removeListener.onRemove(ReferencedCache.this, Fs.as(key));
            }
        }
    }

    private final class WeakEntry extends WeakReference<Object> implements Entry {

        private final Object key;

        public WeakEntry(Object key, Object referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return get();
        }

        @Override
        public void clear() {
            super.clear();
            if (removeListener != null) {
                removeListener.onRemove(ReferencedCache.this, Fs.as(key));
            }
        }
    }
}
