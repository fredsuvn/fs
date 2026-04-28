package space.sunqian.fs.base.logging;


import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.function.Supplier;

/**
 * This interface is used to wrap an object, and encapsulate the {@link Object#toString()} of the wrapped object. The
 * {@link Object#toString()} of the wrapped object will be executed if, and only if the {@link Object#toString()} of
 * this interface is called. And the {@link Object#toString()} of the wrapped object is only executed once and will be
 * cached by this interface.
 * <p>
 * This interface is typically used to reduce the high overhead concatenation operations for log printing.
 */
public interface ToLog<T> {

    /**
     * Returns a new instance of {@link ToLog} to wrap the given object.
     *
     * @param origin the given wrapped object
     * @param <T>    the type of the given wrapped object
     * @return a new instance of {@link ToLog} to wrap the given object
     */
    static <T> @Nonnull ToLog<T> wrap(@Nullable T origin) {
        return LogBack.toLog(origin);
    }

    /**
     * Returns a new instance of {@link ToLog} to wrap the given {@code toString} supplier. The given {@code toString}
     * supplier provides the same function as the {@link Object#toString()} of the wrapped object using
     * {@link Supplier#get()}.
     *
     * @param supplier the given {@code toString} supplier
     * @return a new instance of {@link ToLog} to wrap the given {@code toString} supplier
     */
    static @Nonnull ToLog<@Nonnull Supplier<@Nonnull String>> wrap(@Nonnull Supplier<@Nonnull String> supplier) {
        return LogBack.toLog(supplier);
    }

    /**
     * Returns the wrapped object.
     *
     * @return the wrapped object
     */
    T origin();

    /**
     * Returns the encapsulated and cached {@link Object#toString()} result of the wrapped object.
     *
     * @return the encapsulated and cached {@link Object#toString()} result of the wrapped object
     */
    @Nonnull
    String toString();
}
