package xyz.sunqian.common.base.option;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieArray;

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
@Immutable
public interface Option<K, V> {

    /**
     * Returns an {@link Option} with the specified key and value.
     *
     * @param key   the specified key
     * @param value the specified value
     * @param <K>   type of the key
     * @param <V>   type of the value
     * @return an {@link Option} with the specified key and value
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
     * Finds the first option value matching the specified key from the given options.
     *
     * @param key     the  specified key
     * @param options the given options
     * @param <V>     type of the value
     * @return the first option value matching the specified key from the given options
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
     * Returns the key of this option.
     *
     * @return the key of this option
     */
    K key();

    /**
     * Returns the value of this option.
     *
     * @return the value of this option.
     */
    @Nullable
    V value();
}
