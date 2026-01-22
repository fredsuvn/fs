package space.sunqian.fs.base.option;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ArrayKit;

import java.util.Objects;

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
     * Finds and returns the first option whose key equals the specified key from the given options, or null if not
     * found. This method will cast returned option to the specified type {@code O}.
     *
     * @param key     the specified key
     * @param options the given options
     * @param <K>     the key type
     * @param <V>     the value type
     * @param <O>     the returned option type
     * @return the first option whose key equals the specified key from the given options, or null if not found
     */
    static <K, V, O extends Option<K, V>> @Nullable O findOption(
        @Nonnull K key,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        if (ArrayKit.isEmpty(options)) {
            return null;
        }
        for (Option<?, ?> option : options) {
            if (Objects.equals(option.key(), key)) {
                return Fs.as(option);
            }
        }
        return null;
    }

    /**
     * Finds the first option whose key equals the specified key from the given options. Returns the value of the found
     * option, or null if not found. This method will cast returned value to the specified type {@code V}.
     *
     * @param key     the specified key
     * @param options the given options
     * @param <V>     the value type
     * @return the value of the found option, or null if not found
     */
    static <V> V findValue(@Nonnull Object key, @Nonnull Option<?, ?> @Nonnull ... options) {
        @Nullable Option<?, V> option = findOption(key, options);
        return option == null ? null : option.value();
    }

    /**
     * Returns whether the given options contain an option whose key equals the specified key.
     *
     * @param key     the specified key
     * @param options the given options
     * @return {@code true} if given options contain an option whose key equals the specified key, otherwise
     * {@code false}
     */
    static boolean containsKey(@Nonnull Object key, @Nonnull Option<?, ?> @Nonnull ... options) {
        return findOption(key, options) != null;
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
