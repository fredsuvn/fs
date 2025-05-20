package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface provides interruptible waiting methods, which are typically used to block the current thread until it
 * can proceed.
 *
 * @author sunqian
 */
public interface Interruptible {

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
        } catch (InterruptedException e) {
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
        } catch (InterruptedException e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Blocks the current thread until it can proceed.
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    void awaitInterruptibly() throws InterruptedException;

    /**
     * Blocks the current thread until it can proceed, or the specified waiting time elapses. Returns {@code true} if
     * the thread can proceed and {@code false} if the waiting time elapsed.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if the thread can proceed and {@code false} if the waiting time elapsed
     * @throws InterruptedException if the current thread is interrupted
     */
    boolean awaitInterruptibly(@Nonnull Duration duration) throws InterruptedException;
}
