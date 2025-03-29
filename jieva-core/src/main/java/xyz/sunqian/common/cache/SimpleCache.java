package xyz.sunqian.common.cache;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.value.Val;

import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.function.Function;

/**
 * The {@code SimpleCache} interface is a simplified cache interface with key-value mapping, and it provides convenient,
 * atomic and thread-safe cache operations. {@code SimpleCache} supports null values but does not allow null keys.
 *
 * @param <K> type of the keys
 * @param <V> type of the values
 * @author sunqian
 */
@ThreadSafe
public interface SimpleCache<K, V> {

    /**
     * Returns a new {@link SimpleCache} based on {@link WeakReference}. The default cache duration is permanent until
     * the entry is removed by garbage collection.
     *
     * @param <K> type of the cache keys
     * @param <V> type of the cache values
     * @return a new {@link SimpleCache} based on {@link WeakReference}
     */
    static <K, V> SimpleCache<K, V> weak() {
        return weak(null, null);
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link WeakReference}. The cache automatically removes invalid entries
     * by calling {@link #clean()} during each method invocation. The {@code duration} parameter specifies the default
     * cache duration, may be {@code null} if the duration is permanent until the entry is removed by garbage
     * collection. The {@code removalListener} is an optional callback function for the removal of entries.
     *
     * @param duration        the  default cache duration
     * @param removalListener an optional callback function for the removal of entries
     * @param <K>             type of the cache keys
     * @param <V>             type of the cache values
     * @return a new {@link SimpleCache} based on {@link WeakReference}
     */
    static <K, V> SimpleCache<K, V> weak(
        @Nullable Duration duration,
        @Nullable SimpleCache.RemovalListener<K, V> removalListener
    ) {
        return SimpleCacheBack.newSimpleCache(true, duration, removalListener);
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link SoftReference}. The default cache duration is permanent until
     * the entry is removed by garbage collection.
     *
     * @param <K> type of the cache keys
     * @param <V> type of the cache values
     * @return a new {@link SimpleCache} based on {@link SoftReference}
     */
    static <K, V> SimpleCache<K, V> soft() {
        return soft(null, null);
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link SoftReference}. The cache automatically removes invalid entries
     * by calling {@link #clean()} during each method invocation. The {@code duration} parameter specifies the default
     * cache duration, may be {@code null} if the duration is permanent until the entry is removed by garbage
     * collection. The {@code removalListener} is an optional callback function for the removal of entries.
     *
     * @param duration        the  default cache duration
     * @param removalListener an optional callback function for the removal of entries
     * @param <K>             type of the cache keys
     * @param <V>             type of the cache values
     * @return a new {@link SimpleCache} based on {@link SoftReference}
     */
    static <K, V> SimpleCache<K, V> soft(
        @Nullable Duration duration,
        @Nullable SimpleCache.RemovalListener<K, V> removalListener
    ) {
        return SimpleCacheBack.newSimpleCache(false, duration, removalListener);
    }

    /**
     * Returns a new {@link SimpleCache} based on {@link PhantomReference}. The cache automatically removes invalid
     * entries by calling {@link #clean()} during each method invocation. The {@code removalListener} is an optional
     * callback function for the removal of entries.
     * <p>
     * Actually, this cache cannot retain valid entries due to the features of {@link PhantomReference}, so it is
     * typically used for testing.
     *
     * @param removalListener an optional callback function for the removal of entries
     * @param <K>             type of the cache keys
     * @param <V>             type of the cache values
     * @return a new {@link SimpleCache} based on {@link PhantomReference}
     */
    static <K, V> SimpleCache<K, V> phantom(@Nullable SimpleCache.RemovalListener<K, V> removalListener
    ) {
        return SimpleCacheBack.newSimpleCache(removalListener);
    }

    /**
     * Creates a new {@link ValueInfo} instance with the specified value and duration.
     *
     * @param value    the specified value, may be {@code null}
     * @param duration the specified duration, may be {@code null} for using the default cache duration
     * @param <V>      type of the value
     * @return a new {@link ValueInfo} instance with the specified value and duration
     */
    static <V> ValueInfo<V> valueInfo(@Nullable V value, @Nullable Duration duration) {
        return new ValueInfo<V>() {

            @Override
            public V value() {
                return value;
            }

            @Override
            public @Nullable Duration duration() {
                return duration;
            }
        };
    }

    /**
     * Returns the value for the specified key, or null if the value is invalid or is {@code null} itself. Use
     * {@link #getVal(Object)} to distinguish between these cases.
     *
     * @param key the specified key
     * @return the value for the specified key
     */
    @Nullable
    V get(K key);

    /**
     * Returns the value wrapped by {@link Val} for the specified key, or null if the value is invalid.
     *
     * @param key the specified key
     * @return the value wrapped by {@link Val} for the specified key
     */
    @Nullable
    Val<V> getVal(K key);

    /**
     * Returns the value for the specified key. If the value is invalid, the producer will be called to generate a new
     * value, and the new value will be cached with default settings. This operation is atomic.
     * <p>
     * Any value (including {@code null}) generated by the producer will be cached. For more detailed control, try
     * {@link #getVal(Object, Function)}.
     *
     * @param key      the specified key
     * @param producer the producer to generate new value
     * @param <V1>     type of the generated value
     * @return the value for the specified key
     */
    @Nullable
    <V1 extends V> V get(K key, Function<? super K, @Nullable V1> producer);

    /**
     * Returns the value wrapped by {@link Val} for the specified key. If the value is invalid, the producer will be
     * called to generate a new {@link ValueInfo} instance, and a new value will be cached based on the
     * {@link ValueInfo} instance. This operation is atomic.
     * <p>
     * If the producer generates {@code null}, no value will be cached, and this method will return null.
     *
     * @param key      the specified key
     * @param producer the producer to generate new {@link ValueInfo} instance
     * @param <V1>     type of the generated {@link ValueInfo} instance
     * @return the value wrapped by {@link Val} for the specified key
     */
    @Nullable
    <V1 extends ValueInfo<? extends V>> Val<V> getVal(K key, Function<? super K, @Nullable V1> producer);

    /**
     * Puts the key mapping the value into this cache with default settings.
     *
     * @param key   the key
     * @param value the value
     */
    void put(K key, @Nullable V value);

    /**
     * Puts the key mapping a value based on given {@link ValueInfo}.
     *
     * @param key   the key
     * @param value given {@link ValueInfo}
     */
    void put(K key, ValueInfo<? extends V> value);

    /**
     * Removes the value mapped by the key from this cache.
     *
     * @param key the key
     */
    void remove(K key);

    /**
     * Returns whether this cache contains a valid value mapped by the key.
     *
     * @param key the key
     * @return whether this cache contains a valid value mapped by the key
     */
    boolean contains(K key);

    /**
     * Sets a new duration for the value mapped by the key and resets its expiration from now. If there is no mapping
     * for the key or the value has already expired, this method has no effect.
     *
     * @param key      the key
     * @param duration a new cache duration
     */
    void expire(K key, Duration duration);

    /**
     * Returns size of this cache.
     *
     * @return size of this cache
     */
    int size();

    /**
     * Removes all entries from this cache.
     */
    void clear();

    /**
     * Removes invalid entries, such as expired ones, from this cache.
     */
    void clean();

    /**
     * Detailed value info for {@link SimpleCache}.
     *
     * @param <V> type of the actual value to be cached
     */
    interface ValueInfo<V> {

        /**
         * Returns the actual value to be cached.
         *
         * @return the actual value to be cached
         */
        V value();

        /**
         * Returns cache duration for this value, or {@code null} if default duration is applied.
         *
         * @return cache duration for this value, or {@code null} if default duration is applied
         */
        @Nullable
        Duration duration();
    }

    /**
     * Listener for the cached value's removal.
     *
     * @param <K> type of the cache key
     * @param <V> type of the cache value
     */
    interface RemovalListener<K, V> {

        /**
         * Callback method invoked after a value is removed from the cache. This method is called exactly once for the
         * value. The passed value wrapped by {@link Val} may be {@code null} in cases such as garbage-collection.
         *
         * @param key   the key of the removed value
         * @param value the removed value wrapped by {@link Val}, may be {@code null}
         * @param cause cause for the cached value's removal
         */
        void onRemoval(K key, @Nullable Val<V> value, RemovalCause cause);
    }

    /**
     * Cause for the cached value's removal.
     */
    enum RemovalCause {

        /**
         * Manually removed.
         */
        EXPLICIT,

        /**
         * Potentially removed due to replacement.
         */
        REPLACED,

        /**
         * Automatically removed by the garbage collection.
         */
        COLLECTED,

        /**
         * Removed due to expiration.
         */
        EXPIRED,

        /**
         * Evicted due to size constraints.
         */
        SIZE,
    }
}
