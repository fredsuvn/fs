package space.sunqian.fs.base.option;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ArrayKit;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities for option.
 *
 * @author sunqian
 */
public class OptionKit {

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
    public static <K, V, O extends Option<K, V>> @Nullable O findOption(
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
    public static <V> V findValue(@Nonnull Object key, @Nonnull Option<?, ?> @Nonnull ... options) {
        @Nullable Option<?, V> option = OptionKit.findOption(key, options);
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
    public static boolean containsKey(@Nonnull Object key, @Nonnull Option<?, ?> @Nonnull ... options) {
        return OptionKit.findOption(key, options) != null;
    }

    /**
     * Merges the additional options to the default options.
     * <p>
     * All additional options whose key equals the key of the default options will override the corresponding option in
     * the default options array. All additional options whose key does not exist in the default options array will be
     * added to the returned options array.
     * <p>
     * For the default options array, if there is no new option added, the default options array itself will be the
     * returned array, otherwise a new array will be created and returned.
     *
     * @param defaultOptions    the default options
     * @param additionalOptions the additional options
     * @param <K>               the key type
     * @param <V>               the value type
     * @return the merged options
     */
    public static <K, V> @Nonnull Option<K, V> @Nonnull [] mergeOptions(
        @Nonnull Option<K, V> @Nonnull @RetainedParam [] defaultOptions,
        @Nonnull Option<K, V> @Nonnull [] additionalOptions
    ) {
        if (additionalOptions.length == 0) {
            return defaultOptions;
        }
        int newCount = 0;
        ADDITIONAL:
        for (Option<K, V> additionalOption : additionalOptions) {
            for (Option<K, V> defaultOption : defaultOptions) {
                if (Objects.equals(defaultOption.key(), additionalOption.key())) {
                    continue ADDITIONAL;
                }
            }
            newCount++;
        }
        Option<K, V>[] result;
        if (newCount == 0) {
            result = defaultOptions;
        } else {
            result = Arrays.copyOf(defaultOptions, defaultOptions.length + newCount);
        }
        int lastIndex = result.length - 1;
        ADDITIONAL:
        for (Option<K, V> additionalOption : additionalOptions) {
            for (int i = 0; i < defaultOptions.length; i++) {
                Option<K, V> defaultOption = defaultOptions[i];
                if (Objects.equals(defaultOption.key(), additionalOption.key())) {
                    result[i] = additionalOption;
                    continue ADDITIONAL;
                }
            }
            // new option
            result[lastIndex--] = additionalOption;
        }
        return result;
    }

    private OptionKit() {
    }
}
