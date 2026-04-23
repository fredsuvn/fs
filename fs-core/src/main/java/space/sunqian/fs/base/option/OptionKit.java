package space.sunqian.fs.base.option;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.string.StringKit;
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
     * If the additional options array is empty, the default options array will be returned. Otherwise, a new array will
     * be copied from the default options array with the new length and returned. Any additional option whose key equals
     * the key of the default option will override the default option in the returned array. Any new additional option
     * whose key does not exist in the default options array will be added to the returned array.
     *
     * @param defaultOptions    the default options
     * @param additionalOptions the additional options
     * @param <K>               the key type
     * @param <V>               the value type
     * @return the merged options
     */
    public static <K, V> @Nonnull Option<K, V> @Nonnull [] mergeOptions(
        @Nonnull Option<?, ?> @Nonnull @RetainedParam [] defaultOptions,
        @Nonnull Option<?, ?> @Nonnull ... additionalOptions
    ) {
        if (ArrayKit.isEmpty(additionalOptions)) {
            return Fs.as(defaultOptions);
        }
        int newCount = 0;
        ADDITIONAL:
        for (Option<?, ?> additionalOption : additionalOptions) {
            for (Option<?, ?> defaultOption : defaultOptions) {
                if (Objects.equals(defaultOption.key(), additionalOption.key())) {
                    continue ADDITIONAL;
                }
            }
            newCount++;
        }
        Option<?, ?>[] result = Arrays.copyOf(defaultOptions, defaultOptions.length + newCount);
        int lastIndex = result.length - 1;
        ADDITIONAL:
        for (Option<?, ?> additionalOption : additionalOptions) {
            for (int i = 0; i < defaultOptions.length; i++) {
                Option<?, ?> defaultOption = defaultOptions[i];
                if (Objects.equals(defaultOption.key(), additionalOption.key())) {
                    result[i] = additionalOption;
                    continue ADDITIONAL;
                }
            }
            // new option
            result[lastIndex--] = additionalOption;
        }
        return Fs.as(result);
    }

    /**
     * Merges the additional option to the default options.
     * <p>
     * This method always returns a new array copied from the default options array. If the key of the additional option
     * equals the key of any option in the default options array, the additional option will override the default option
     * in the returned array. Otherwise, the additional option will be added to the returned array.
     *
     * @param defaultOptions   the default options
     * @param additionalOption the additional option
     * @param <K>              the key type
     * @param <V>              the value type
     * @return the merged options
     */
    public static <K, V> @Nonnull Option<K, V> @Nonnull [] mergeOption(
        @Nonnull Option<?, ?> @Nonnull [] defaultOptions,
        @Nonnull Option<?, ?> additionalOption
    ) {
        int index = ArrayKit.indexOf(
            defaultOptions, (i, o) -> Objects.equals(o.key(), additionalOption.key())
        );
        if (index == -1) {
            Option<?, ?>[] result = Arrays.copyOf(defaultOptions, defaultOptions.length + 1);
            result[result.length - 1] = additionalOption;
            return Fs.as(result);
        }
        Option<?, ?>[] result = Arrays.copyOf(defaultOptions, defaultOptions.length);
        result[index] = additionalOption;
        return Fs.as(result);
    }

    /**
     * Returns {@code true} if the given options contains an option whose key equals the specified key and the value of
     * the option is enabled checked by {@link StringKit#isEnabled(CharSequence)}.
     *
     * @param key     the specified key
     * @param options the given options
     * @return {@code true} if the given options contains an option whose key equals the specified key and the value of
     * the option is enabled checked by {@link StringKit#isEnabled(CharSequence)}
     */
    public static boolean isEnabled(@Nonnull Object key, @Nonnull Option<?, ?> @Nonnull ... options) {
        @Nullable Option<?, ?> option = OptionKit.findOption(key, options);
        if (option == null) {
            return false;
        }
        Object value = option.value();
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        if (value instanceof String) {
            return StringKit.isEnabled((String) value);
        }
        return false;
    }

    private OptionKit() {
    }
}
