package xyz.sunqian.common.cache;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.value.Val;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Cache interface, provides get/put/remove/compute/contains/expire/clear/clean methods which perform like
 * {@link ConcurrentHashMap}. Implementations should be thread-safe.
 * <p>
 * This cache supports set different expiry for each entry, and null value is permitted. Using {@link #get(Object)}
 * method cannot know if a value itself is {@code null}, or it is expiry or not exists, but {@link #getVal(Object)} can.
 * The other methods are similar as well.
 * <p>
 * Default implementation will call {@link #cleanUp()} before end of each other method calling to clean up expired data.
 * This means that expired data may still occupy memory until the next invocation of {@link #cleanUp()}. Scheduling task
 * for {@link #cleanUp()} can effectively may resolve this problem.
 *
 * @param <K> type of keys
 * @param <V> type of values
 * @author fresduvn
 */
@ThreadSafe
public interface Cache<K, V> {

    /**
     * Creates a new {@link Cache} based on {@link SoftReference}.
     * <p>
     * Due to the nature of {@link SoftReference}, cached values may be garbage collected and become invalidated before
     * their expiry time.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return a new {@link Cache} based on {@link SoftReference}
     */
    static <K, V> Cache<K, V> softCache() {
        return softCache(null);
    }

    /**
     * Creates a new {@link Cache} based on {@link SoftReference} with removal listener.
     * <p>
     * Due to the nature of {@link SoftReference}, cached values may be garbage collected and become invalidated before
     * their expiry time, and the value in the {@code removalListener} callback may also be null.
     *
     * @param removalListener removal listener
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link Cache} based on {@link SoftReference}
     */
    static <K, V> Cache<K, V> softCache(RemovalListener<? super K, ? super V> removalListener) {
        return softCache(16, null);
    }

    /**
     * Creates a new {@link Cache} based on {@link SoftReference} with initial capacity and removal listener.
     * <p>
     * Due to the nature of {@link SoftReference}, cached values may be garbage collected and become invalidated before
     * their expiry time, and the value in the {@code removalListener} callback may also be null.
     *
     * @param initialCapacity initial capacity
     * @param removalListener removal listener
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link Cache} based on {@link SoftReference}
     */
    static <K, V> Cache<K, V> softCache(
        int initialCapacity, @Nullable RemovalListener<? super K, ? super V> removalListener) {
        return new CacheImpl<>(true, initialCapacity, removalListener);
    }

    /**
     * Creates a new {@link Cache} based on {@link WeakReference}.
     * <p>
     * Due to the nature of {@link WeakReference}, cached values may be garbage collected and become invalidated before
     * their expiry time.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return a new {@link Cache} based on {@link WeakReference}
     */
    static <K, V> Cache<K, V> weakCache() {
        return weakCache(null);
    }

    /**
     * Creates a new {@link Cache} based on {@link WeakReference} with removal listener.
     * <p>
     * Due to the nature of {@link WeakReference}, cached values may be garbage collected and become invalidated before
     * their expiry time, and the value in the {@code removalListener} callback may also be null.
     *
     * @param removalListener removal listener
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link Cache} based on {@link WeakReference}
     */
    static <K, V> Cache<K, V> weakCache(RemovalListener<? super K, ? super V> removalListener) {
        return weakCache(16, null);
    }

    /**
     * Creates a new {@link Cache} based on {@link WeakReference} with initial capacity and removal listener.
     * <p>
     * Due to the nature of {@link WeakReference}, cached values may be garbage collected and become invalidated before
     * their expiry time, and the value in the {@code removalListener} callback may also be null.
     *
     * @param initialCapacity initial capacity
     * @param removalListener removal listener
     * @param <K>             type of keys
     * @param <V>             type of values
     * @return a new {@link Cache} based on {@link WeakReference}
     */
    static <K, V> Cache<K, V> weakCache(
        int initialCapacity, @Nullable RemovalListener<? super K, ? super V> removalListener) {
        return new CacheImpl<>(false, initialCapacity, removalListener);
    }

    /**
     * Creates a new instance of {@link Value} with given data and default expiry rules.
     *
     * @param data given data
     * @param <V>  type of value
     * @return a new instance of {@link Value}
     */
    static <V> Value<V> newValue(V data) {
        return newValue(data, null);
    }

    /**
     * Creates a new instance of {@link Value} with given data and expiry. The expiry can be null, which indicates the
     * use of default expiry rules.
     *
     * @param data   given data
     * @param expiry given expiry
     * @param <V>    type of value
     * @return a new instance of {@link Value}
     */
    static <V> Value<V> newValue(V data, @Nullable Duration expiry) {
        return new Value<V>() {
            @Override
            public V getData() {
                return data;
            }

            @Override
            public @Nullable Duration getExpiry() {
                return expiry;
            }
        };
    }

    /**
     * Returns value associating with specified key. The value will be null if there is no entry for specified key or
     * the value is expired or itself is null.
     * <p>
     * This is a {@code get} operation.
     *
     * @param key specified key
     * @return value associating with specified key
     */
    @Nullable
    V get(K key);

    /**
     * Returns value wrapped by {@link Val} associating with specified key. If there is no entry for specified key or
     * the value is expired, return null.
     * <p>
     * This is a {@code get} operation.
     *
     * @param key specified key
     * @return value wrapped by {@link Val} associating with specified key
     */
    @Nullable
    Val<V> getVal(K key);

    /**
     * Puts or overrides the value associated with specified key with default expiry rules, returns old value. The old
     * value will be null if there is no entry for specified key or the value is expired or itself is null.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key   specified key
     * @param value the value
     * @return old value associating with specified key
     */
    @Nullable
    V put(K key, V value);

    /**
     * Puts or overrides the value wrapped by {@link Value} associated with specified key, returns old value wrapper.
     * The old value wrapper will be null if there is no entry for specified key or the value is expired.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key   specified key
     * @param value the value wrapped by {@link Value}
     * @return old value wrapped by {@link Val} associating with specified key
     */
    @Nullable
    Val<V> putVal(K key, Value<? extends V> value);

    /**
     * Removes the value associated with specified key, returns old value. The old value will be null if there is no
     * entry for specified key or the value is expired or itself is null.
     * <p>
     * This is a {@code remove} operation.
     *
     * @param key specified key
     * @return old value associating with specified key
     */
    @Nullable
    V remove(K key);

    /**
     * Removes the value associated with specified key, returns old value wrapper. The old value wrapper will be null if
     * there is no entry for specified key or the value is expired.
     * <p>
     * This is a {@code remove} operation.
     *
     * @param key specified key
     * @return old value wrapped by {@link Val} associating with specified key
     */
    @Nullable
    Val<V> removeVal(K key);

    /**
     * Returns value associating with specified key, and it performs like
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}:
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value using the given loader
     * and enters it into this cache with default expiry rules. Null is permitted so the null value will also be
     * entered.
     * <p>
     * This cache is thread-safe so the loader function is applied at most once per key, and blocks other threads.
     * <p>
     * This is a {@code compute} operation.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key
     */
    @Nullable
    V compute(K key, Function<? super K, @Nullable ? extends V> loader);

    /**
     * Returns value wrapped by {@link Val} associating with specified key, and it performs like
     * {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}:
     * <p>
     * If the specified key is not already associated with a value, attempts to compute its value as {@link Value} using
     * the given loader and enters it into this cache. If the loader returns null, no mapping will be recorded and this
     * method will return null.
     * <p>
     * This cache is thread-safe so the loader function is applied at most once per key, and blocks other threads.
     * <p>
     * This is a {@code compute} operation.
     *
     * @param key    specified key
     * @param loader given loader
     * @return value associating with specified key, or null if the loader returns null
     */
    @Nullable
    Val<V> computeVal(K key, Function<? super K, @Nullable ? extends Value<? extends V>> loader);

    /**
     * Returns whether this cache contains the value associating with specified key and the value is valid.
     * <p>
     * This is a {@code contains} operation.
     *
     * @param key specified key
     * @return whether this cache contains the value associating with specified key and the value is valid
     */
    boolean contains(K key);

    /**
     * Sets expiry for value associated with specified key. The expiry can be null, which indicates the use of default
     * expiry rules.
     * <p>
     * If the value has already expired before, this method will do nothing.
     * <p>
     * This is a {@code put} operation.
     *
     * @param key    specified key
     * @param expire expiry
     */
    void expire(K key, @Nullable Duration expire);

    /**
     * Returns current size.
     *
     * @return current size
     */
    int size();

    /**
     * Removes all values in this cache.
     */
    void clear();

    /**
     * Removes all expired values in this cache.
     */
    void cleanUp();

    /**
     * Cache removal listener, a callback interface which will be invoked after an entry is removed from a cache, should
     * be thread-safe.
     *
     * @param <K> type of keys
     * @param <V> type of values
     */
    @ThreadSafe
    interface RemovalListener<K, V> {

        /**
         * This callback method, invoked after an entry is removed from a cache.
         *
         * @param key   key of entry
         * @param value removed value of entry
         * @param cause reason why the entry was removed
         */
        void onRemoval(K key, @Nullable V value, Cause cause);

        /**
         * The reason why the entry was removed.
         */
        enum Cause {

            /**
             * The entry was manually removed by the user.
             */
            EXPLICIT,

            /**
             * The entry itself was not actually removed, but its value was replaced by the user.
             */
            REPLACED,

            /**
             * The entry was removed automatically because its key or value was garbage-collected.
             */
            COLLECTED,

            /**
             * The entry's expiry timestamp has passed.
             */
            EXPIRED,

            /**
             * The entry was evicted due to size constraints.
             */
            SIZE,
        }
    }

    /**
     * Cache value info.
     *
     * @param <V> type of value
     */
    interface Value<V> {

        /**
         * Returns data of value.
         *
         * @return data of value
         */
        V getData();

        /**
         * Returns expiry of value, can be null, which indicates the use of default expiry rules.
         *
         * @return expiry of value
         */
        @Nullable
        Duration getExpiry();
    }
}
