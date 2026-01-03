package space.sunqian.fs.base.logging;

import space.sunqian.annotation.Nonnull;

import java.util.function.Supplier;

/**
 * Utilities for logging.
 *
 * @author sunqian
 */
public class LogKit {

    /**
     * Returns an object that encapsulates a specified {@link Supplier}, and {@link Supplier#get()} will only be called
     * when {@link #toString()} of the returned object is called. This class is typically used to reduce the high
     * overhead concatenation operations for log printing.
     *
     * @param supplier the specified {@link Supplier}
     * @return an object that encapsulates the specified {@link Supplier}
     */
    public static @Nonnull Object lazyToString(@Nonnull Supplier<@Nonnull String> supplier) {
        return new LazyToString(supplier);
    }

    private static final class LazyToString {

        private final @Nonnull Supplier<@Nonnull String> supplier;

        private LazyToString(@Nonnull Supplier<@Nonnull String> supplier) {
            this.supplier = supplier;
        }

        @Override
        public @Nonnull String toString() {
            return supplier.get();
        }
    }

    private LogKit() {
    }
}
