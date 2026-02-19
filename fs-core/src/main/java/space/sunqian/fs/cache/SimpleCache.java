package space.sunqian.fs.cache;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.base.value.Val;

import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This interface is a simplified key-value pair cache interface (implementations must be thread-safe). It only provides
 * get, put, remove and clean operations, and supports null value but does not allow null key. It is suitable for
 * scenarios that require simple cache operations and do not care about the cache lifecycle.
 * <p>
 * It is recommended to use the skeletal implementation: {@link AbstractSimpleCache}.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author sunqian
 * @implNote Although the default implementations will call {@link #clean()} every time they execute other methods, it
 * is recommended to take some measures to enable the {@link #clean()} to call regularly to clean invalid entries
 * @see AbstractSimpleCache
 */
@ThreadSafe
public interface SimpleCache<K, V> extends CacheFunction<K, V> {

    /**
     * Returns a new {@link SimpleCache} based on {@link WeakReference} and {@link ConcurrentHashMap}. The values of the
     * returned cache will be automatically collected by the garbage collection based on the characteristics of
     * {@link WeakReference}. Its {@link #clean()} method releases all entries of which values are collected, and this
     * method is automatically invoked once every time another method is executed.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@link SimpleCache} based on {@link WeakReference}
     */
    static <K, V> @Nonnull SimpleCache<K, V> ofWeak() {
        return SimpleCacheBack.ofWeak();
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link SoftReference} and {@link ConcurrentHashMap}. The values of the
     * returned cache will be automatically collected by the garbage collection based on the characteristics of
     * {@link SoftReference}. Its {@link #clean()} method releases all entries of which values are collected, and this
     * method is automatically invoked once every time another method is executed.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@link SimpleCache} based on {@link SoftReference}
     */
    static <K, V> @Nonnull SimpleCache<K, V> ofSoft() {
        return SimpleCacheBack.ofSoft();
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link PhantomReference} and {@link ConcurrentHashMap}. The values of
     * the returned cache will be automatically collected by the garbage collection based on the characteristics of
     * {@link PhantomReference}. Its {@link #clean()} method releases all entries of which values are collected, and
     * this method is automatically invoked once every time another method is executed.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@link SimpleCache} based on {@link PhantomReference}
     */
    static <K, V> @Nonnull SimpleCache<K, V> ofPhantom() {
        return SimpleCacheBack.ofPhantom();
    }

    /**
     * Returns a new {@link SimpleCache} based on strong reference and {@link ConcurrentHashMap}. The values of the
     * returned cache will never automatically be invalid, and its {@link #clean()} method does nothing. The behavior of
     * the returned cache is just like a regular {@link Map}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new {@link SimpleCache} based on strong reference
     */
    static <K, V> @Nonnull SimpleCache<K, V> ofStrong() {
        return SimpleCacheBack.ofStrong();
    }

    /**
     * Returns a new {@link SimpleCache} based on the given map.
     * <p>
     * Note the returned cache treats {@code null} values as absent, which will lead to certain results in processing
     * {@code null} values. Therefore, putting {@code null} values are not encouraged for returned map cache.
     *
     * @param map the given map to cache the value
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @return a new {@link SimpleCache} based on the given map
     */
    static <K, V> @Nonnull SimpleCache<K, V> ofMap(@Nonnull Map<K, V> map) {
        return SimpleCacheBack.ofMap(map);
    }

    /**
     * Returns the value for the specified key, or {@code null} if the value is invalid or is {@code null} itself. Use
     * {@link #getVal(Object)} to distinguish between these cases.
     *
     * @param key the specified key
     * @return the value for the specified key, or {@code null} if the value is invalid or is {@code null} itself
     */
    @Nullable
    V get(@Nonnull K key);

    /**
     * Returns the value wrapped by {@link Val} for the specified key, or {@code null} if the value is invalid.
     *
     * @param key the specified key
     * @return the value wrapped by {@link Val} for the specified key, or {@code null} if the value is invalid
     */
    @Nullable
    Val<@Nullable V> getVal(@Nonnull K key);

    /**
     * Returns the value for the specified key. If the value is invalid, the loader will be called to generate a new
     * value, and the new value will be cached and returned. If an exception is thrown during the generation, the
     * exception will be thrown directly. This operation is atomic.
     * <p>
     * Any value, including {@code null}, generated by the loader will be cached. To more explicitly to deal with the
     * value, especially the {@code null} value, try {@link #getVal(Object, Function)}.
     *
     * @param key    the specified key
     * @param loader the function to generate new value for the specified key
     * @return the value for the specified key, including {@code null}
     */
    @Override
    V get(@Nonnull K key, @Nonnull Function<? super @Nonnull K, ? extends @Nullable V> loader);

    /**
     * Returns the value wrapped by {@link Val} for the specified key. If the value is invalid, the loader will be
     * called to generate a new value wrapped by {@link Val}, and the new value will be cached and returned. If an
     * exception is thrown during the generation, the exception will be thrown directly. This operation is atomic.
     * <p>
     * If the loader generates a {@code null}, no value will be cached, and this method will return null.
     *
     * @param key    the specified key
     * @param loader the function to generate new value wrapped by {@link Val} for the specified key
     * @return the value wrapped by {@link Val} for the specified key, or {@code null} if the loader generates a
     * {@code null}
     */
    @Nullable
    Val<@Nullable V> getVal(
        @Nonnull K key,
        @Nonnull Function<? super @Nonnull K, ? extends @Nullable Val<? extends @Nullable V>> loader
    );

    /**
     * Puts the key mapping the value into this cache.
     *
     * @param key   the key
     * @param value the value
     */
    void put(@Nonnull K key, @Nullable V value);

    /**
     * Removes the value mapped by the key from this cache.
     *
     * @param key the key
     */
    void remove(@Nonnull K key);

    /**
     * Returns the current size of this cache.
     *
     * @return the current size of this cache
     */
    int size();

    /**
     * Removes all entries from this cache.
     */
    void clear();

    /**
     * Tries to release invalid entries from this cache.
     */
    void clean();

    /**
     * Copies and returns all current entries in this cache. The result map is independent of the cache, so any changes
     * to the cache, including status changes of this cache itself, are not reflected to the map, and vice versa.
     *
     * @return a {@link Map} contains the copy of the current entries in this cache
     */
    @Nonnull
    Map<K, V> copyEntries();
}
