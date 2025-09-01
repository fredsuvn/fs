package xyz.sunqian.common.base.logging;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;

import java.util.function.Supplier;

/**
 * LogString is used to encapsulate a {@link Supplier}, and {@link Supplier#get()} will only be called when
 * {@link #toString()} of {@link LogString} is called. This class is typically used to reduce the high overhead
 * concatenation operations for log printing.
 *
 * @author sunqian
 */
@Immutable
public final class LogString {

    /**
     * Returns a new instance of {@link LogString} with the specified {@link Supplier}.
     *
     * @param supplier the specified {@link Supplier}
     * @return a new instance of {@link LogString} with the specified {@link Supplier}
     */
    public static @Nonnull LogString of(@Nonnull Supplier<@Nonnull String> supplier) {
        return new LogString(supplier);
    }

    private final @Nonnull Supplier<@Nonnull String> supplier;

    private LogString(@Nonnull Supplier<@Nonnull String> supplier) {
        this.supplier = supplier;
    }

    @Override
    public @Nonnull String toString() {
        return supplier.get();
    }
}
