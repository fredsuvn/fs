package xyz.sunqian.common.base.option;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.coll.JieArray;

import java.util.Objects;

/**
 * This interface represents an option with a key and value. For example:
 * <pre>{@code
 *     //declaration:
 *     public void start(Option<?, ?>... options){...}
 *
 *     //usage:
 *     start(
 *         Option.of("server", null),
 *         Option.of("Xms", "1G"),
 *         Option.of("Xmx", "2G")
 *     );
 * }</pre>
 *
 * @param <K> type of the key
 * @param <V> type of the value
 * @author sunqian
 */
public interface Option<K, V> {

    /**
     * Returns an {@link Option} with specified key and value.
     *
     * @param key   specified key
     * @param value specified value
     * @param <K>   type of the key
     * @param <V>   type of the value
     * @return an {@link Option} with specified key and value
     */
    static <K, V> Option<K, V> of(K key, V value) {
        return OptionBack.of(key, value);
    }

    /**
     * Returns an empty {@link Option} array;
     *
     * @param <K> type of the key
     * @param <V> type of the value
     * @return an empty {@link Option} array
     */
    static <K, V> Option<K, V>[] empty() {
        return Jie.as(OptionBack.EMPTY_OPTIONS);
    }

    /**
     * Finds the first option value matching the specified key from given options.
     *
     * @param key     specified key
     * @param options given options
     * @param <V>     type of the value
     * @return the first option value matching the specified key from given options
     */
    @Nullable
    static <V> V find(Object key, Option<?, ?>... options) {
        if (JieArray.isEmpty(options)) {
            return null;
        }
        for (Option<?, ?> option : options) {
            if (option != null && Objects.equals(option.key(), key)) {
                return Jie.as(option.value());
            }
        }
        return null;
    }

    /**
     * Returns key of this option.
     *
     * @return key of this option
     */
    K key();

    /**
     * Returns value of this option.
     *
     * @return value of this option.
     */
    @Nullable
    V value();
}
