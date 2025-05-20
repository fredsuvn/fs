package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;

/**
 * This interface represents a gate that can block or continue the current thread. It has two states: opened and closed.
 * If it is closed, its {@link #await()} and {@link #await(Duration)} methods block the current thread until the state
 * becomes opened. The initialized state is closed.
 *
 * @author sunqian
 */
public interface ThreadGate extends Interruptible {

    /**
     * Returns a new {@link ThreadGate}.
     *
     * @return a new {@link ThreadGate}
     */
    static @Nonnull ThreadGate newThreadGate() {
        return GateBack.newThreadGate();
    }

    /**
     * Returns whether this thread gate is opened.
     *
     * @return whether this thread gate is opened
     */
    boolean isOpened();

    /**
     * Returns whether this thread gate is closed.
     *
     * @return whether this thread gate is closed
     */
    boolean isClosed();

    /**
     * Opens this thread gate.
     */
    void open();

    /**
     * Closes this thread gate.
     */
    void close();

    /**
     * Blocks the current thread until this thread gate is opened.
     * <p>
     * This is an unchecked version of {@link #awaitInterruptibly()}.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    default void await() throws AwaitingException {
        Interruptible.super.await();
    }

    /**
     * Blocks the current thread until this thread gate is opened, or the specified waiting time elapses. Returns
     * {@code true} if this thread gate become opened and {@code false} if the waiting time elapsed.
     * <p>
     * This is an unchecked version of {@link #awaitInterruptibly(Duration)}.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if this thread gate become opened and {@code false} if the waiting time elapsed
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    default boolean await(@Nonnull Duration duration) throws AwaitingException {
        return Interruptible.super.await(duration);
    }

    /**
     * Blocks the current thread until this thread gate is opened.
     *
     * @throws AwaitingException if the current thread is interrupted
     */
    void awaitInterruptibly() throws InterruptedException;

    /**
     * Blocks the current thread until this thread gate is opened, or the specified waiting time elapses. Returns
     * {@code true} if this thread gate become opened and {@code false} if the waiting time elapsed.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if this thread gate become opened and {@code false} if the waiting time elapsed
     * @throws AwaitingException if the current thread is interrupted
     */
    boolean awaitInterruptibly(@Nonnull Duration duration) throws InterruptedException;
}
