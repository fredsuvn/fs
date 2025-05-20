package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This is an adaptor for checked waiting methods, which are typically used to block the current thread until it can
 * proceed. It provides unchecked methods whose default implementations are adapted from the corresponding checked
 * methods, and the default implementations of the checked methods are throwing {@link UnsupportedOperationException}.
 *
 * @author sunqian
 */
public interface AwaitingAdaptor {

    /**
     * Blocks the current thread until it can proceed.
     * <p>
     * This is an unchecked version of {@link #awaitInterruptibly()}.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    default void await() throws AwaitingException {
        try {
            awaitInterruptibly();
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Blocks the current thread until it can proceed, or the specified waiting time elapses. Returns {@code true} if
     * the thread can proceed and {@code false} if the waiting time elapsed.
     * <p>
     * This is an unchecked version of {@link #awaitInterruptibly(long)}.
     *
     * @param millis the maximum milliseconds to wait
     * @return {@code true} if the thread can proceed and {@code false} if the waiting time elapsed
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    default boolean await(long millis) throws AwaitingException {
        try {
            return awaitInterruptibly(millis);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Blocks the current thread until it can proceed, or the specified waiting time elapses. Returns {@code true} if
     * the thread can proceed and {@code false} if the waiting time elapsed.
     * <p>
     * This is an unchecked version of {@link #awaitInterruptibly(Duration)}.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if the thread can proceed and {@code false} if the waiting time elapsed
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    default boolean await(@Nonnull Duration duration) throws AwaitingException {
        try {
            return awaitInterruptibly(duration);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Blocks the current thread until it can proceed.
     *
     * @throws Exception if the current thread is interrupted or an error occurs while awaiting
     */
    default void awaitInterruptibly() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Blocks the current thread until it can proceed, or the specified waiting time elapses. Returns {@code true} if
     * the thread can proceed and {@code false} if the waiting time elapsed.
     *
     * @param millis the maximum milliseconds to wait
     * @return {@code true} if the thread can proceed and {@code false} if the waiting time elapsed
     * @throws Exception if the current thread is interrupted or an error occurs while awaiting
     */
    default boolean awaitInterruptibly(long millis) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Blocks the current thread until it can proceed, or the specified waiting time elapses. Returns {@code true} if
     * the thread can proceed and {@code false} if the waiting time elapsed.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if the thread can proceed and {@code false} if the waiting time elapsed
     * @throws Exception if the current thread is interrupted or an error occurs while awaiting
     */
    default boolean awaitInterruptibly(@Nonnull Duration duration) throws Exception {
        throw new UnsupportedOperationException();
    }
}
