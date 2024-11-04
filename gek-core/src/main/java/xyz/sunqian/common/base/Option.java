package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.coll.JieArray;

import java.util.Objects;

/**
 * This interface represents optional parameter. For example:
 * <pre>
 *     public void doSomething(Object arg, Option&lt;?, ?&gt;... options)
 * </pre>
 * The key specifies identifier or name of option, and the value is actual arguments may be null or empty.
 *
 * @param <K> type of keys
 * @param <V> type of values
 */
public interface Option<K, V> {

    /**
     * Returns an {@link Option} of given key and value.
     *
     * @param key   given key
     * @param value given value
     * @param <K>   type of keys
     * @param <V>   type of values
     * @return an {@link Option} of given key and value
     */
    static <K, V> Option<K, V> of(K key, V value) {
        return OptionImpls.of(key, value);
    }

    /**
     * Returns an empty {@link Option} array;
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return an empty {@link Option} array
     */
    static <K, V> Option<K, V>[] empty() {
        return Jie.as(OptionImpls.EMPTY_OPTIONS);
    }

    /**
     * Finds and returns option value of specified key in given options, or null if not found.
     *
     * @param key     specified key
     * @param options given options
     * @param <K>     type of keys
     * @param <V>     type of values
     * @return option value of specified key in given options, or null if not found
     */
    @Nullable
    static <K, V> V find(K key, Option<?, ?>... options) {
        if (JieArray.isEmpty(options)) {
            return null;
        }
        for (Option<?, ?> option : options) {
            if (option != null && Objects.equals(option.getKey(), key)) {
                return Jie.as(option.getValue());
            }
        }
        return null;
    }

    /**
     * Finds and returns option value of specified key in given options, or default value if not found.
     *
     * @param key          specified key
     * @param defaultValue default value
     * @param options      given options
     * @param <K>          type of keys
     * @param <V>          type of values
     * @return option value of specified key in given options, or default value if not found
     */
    static <K, V> V find(K key, V defaultValue, Option<?, ?>... options) {
        return Jie.nonNull(find(key, options), defaultValue);
    }

    /**
     * Returns identifier or name of option.
     *
     * @return identifier or name of option
     */
    K getKey();

    /**
     * Returns value of option may be null or empty.
     *
     * @return value of option may be null or empty
     */
    @Nullable
    V getValue();
}
