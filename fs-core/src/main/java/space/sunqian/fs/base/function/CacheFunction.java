package space.sunqian.fs.base.function;

import java.util.function.Function;

/**
 * A functional interface which is used to get and cache the value associated with the key. Its functional method is
 * {@link #get(Object, Function)}.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface CacheFunction<K, V> {

    /**
     * Get the value associated with the key from the current cache. If the key is not found, the loader function will
     * be called to produce a new value, and the new value will be cached in the current cache.
     *
     * @param key    the key
     * @param loader the loader function
     * @return the value associated with the key
     */
    V get(K key, Function<? super K, ? extends V> loader);
}
