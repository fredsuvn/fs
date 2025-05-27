package xyz.sunqian.common.base.option;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieArray;

/**
 * Static utility class for {@link Option}.
 *
 * @author sunqian
 */
public class JieOption {

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
    @Nullable
    public static <K, V, O extends Option<K, V>> O findOption(
        @Nonnull K key,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        if (JieArray.isEmpty(options)) {
            return null;
        }
        for (Option<?, ?> option : options) {
            if (option != null && Jie.equals(option.key(), key)) {
                return Jie.as(option);
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
        @Nullable Option<?, V> option = findOption(key, options);
        return option == null ? null : option.value();
    }
}
