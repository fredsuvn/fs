package space.sunqian.fs.base.option;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

/**
 * This interface represents an option with a key and value. For example:
 * <pre>{@code
 * //declaration:
 * public void start(Option<?, ?>... options){...}
 *
 * //usage:
 * start(
 *     Option.of("server", null),
 *     Option.of("Xms", "1G"),
 *     Option.of("Xmx", "2G")
 * );
 * }</pre>
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author sunqian
 */
@Immutable
public interface Option<K, V> {

    /**
     * Returns an {@link Option} with the specified key and value.
     *
     * @param key   the specified key
     * @param value the specified value
     * @param <K>   the key type
     * @param <V>   the value type
     * @return an {@link Option} with the specified key and value
     */
    static <K, V> @Nonnull Option<K, V> of(@Nonnull K key, @Nullable V value) {
        return OptionImpl.of(key, value);
    }

    /**
     * Returns an {@link Option} with the specified key and {@code null} value.
     *
     * @param key the specified key
     * @param <K> the key type
     * @param <V> the value type
     * @return an {@link Option} with the specified key and {@code null} value
     */
    static <K, V> @Nonnull Option<K, V> of(@Nonnull K key) {
        return of(key, null);
    }

    /**
     * Returns an empty {@link Option} array;
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return an empty {@link Option} array
     */
    static <K, V> @Nonnull Option<K, V>[] emptyOptions() {
        return Fs.as(OptionImpl.EMPTY_OPTIONS);
    }

    /**
     * Returns the key of this option.
     *
     * @return the key of this option
     */
    @Nonnull
    K key();

    /**
     * Returns the value of this option.
     *
     * @return the value of this option.
     */
    @Nullable
    V value();
}
