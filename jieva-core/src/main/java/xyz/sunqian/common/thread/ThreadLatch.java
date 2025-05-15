package xyz.sunqian.common.thread;

import xyz.sunqian.annotations.Nonnull;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * This interface represents a thread latch that can block or pass the threads. It has two states: locked and unlocked.
 * If it is locked, its {@link #await()} and {@link #await(Duration)} methods block the current thread until the state
 * becomes unlocked. The initialized state is locked.
 * <p>
 * {@code ThreadLatch} can produce waiters by {@link #waiter()}, which only have waiting methods for awaiting and signal
 * methods to send signals to the {@code ThreadLatch} where the {@code Waiter} is produced.
 *
 * @author sunqian
 */
public interface ThreadLatch {

    /**
     * Returns a new {@link ThreadLatch}. The signal from its {@link #waiter()} will be ignored.
     *
     * @return a new {@link ThreadLatch}
     */
    static ThreadLatch newLatch() {
        return LatchBack.newLatch();
    }

    /**
     * Returns a new {@link ThreadLatch} with the specified signal consumer, the signal from its {@link #waiter()} will
     * be consumed by that consumer. Note thread safety is not guaranteed when invoking the consumer.
     *
     * @param signalConsumer the specified signal consumer
     * @return a new {@link ThreadLatch}
     */
    static ThreadLatch newLatch(Consumer<Object> signalConsumer) {
        return LatchBack.newLatch(signalConsumer);
    }

    /**
     * Blocks the current thread until the state becomes unlocked, unless the thread is interrupted.
     *
     * @throws InterruptedRuntimeException if the current thread is interrupted while waiting
     */
    void await() throws InterruptedRuntimeException;

    /**
     * Blocks the current thread until the state becomes unlocked, unless the thread is interrupted, or the specified
     * waiting time elapses. Returns {@code true} if the state become unlocked and {@code false} if the waiting time
     * elapsed.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if the state become unlocked and {@code false} if the waiting time elapsed
     * @throws InterruptedRuntimeException if the current thread is interrupted while waiting
     */
    boolean await(@Nonnull Duration duration) throws InterruptedRuntimeException;

    /**
     * Set current state to locked.
     */
    void lock();

    /**
     * Set current state to unlocked.
     */
    void unlock();

    /**
     * Returns the current state.
     *
     * @return the current state
     */
    @Nonnull
    State state();

    /**
     * Produces and returns a {@link Waiter} which is shared the state with this latch as host.
     *
     * @return a {@link Waiter} which is shared the state with this latch as host
     */
    Waiter waiter();

    /**
     * {@code Waiter} is produced from a {@link ThreadLatch} which as the host and shares the state with the
     * {@code Waiter}. It only provides passive waiting methods and signal methods to send signals to the host
     * {@link ThreadLatch}.
     */
    interface Waiter {

        /**
         * Blocks the current thread until the state becomes unlocked, unless the thread is interrupted.
         *
         * @throws InterruptedRuntimeException if the current thread is interrupted while waiting
         */
        void await() throws InterruptedRuntimeException;

        /**
         * Blocks the current thread until the state becomes unlocked, unless the thread is interrupted, or the
         * specified waiting time elapses. Returns {@code true} if the state become unlocked and {@code false} if the
         * waiting time elapsed.
         *
         * @param duration the maximum time to wait
         * @return {@code true} if the state become unlocked and {@code false} if the waiting time elapsed
         * @throws InterruptedRuntimeException if the current thread is interrupted while waiting
         */
        boolean await(@Nonnull Duration duration) throws InterruptedRuntimeException;

        /**
         * Returns the current state.
         *
         * @return the current state
         */
        @Nonnull
        State state();

        /**
         * Sends a signal to the {@link ThreadLatch} where it was produced.
         *
         * @param o the signal object
         */
        void signal(Object o);
    }

    /**
     * State of the {@link ThreadLatch}.
     */
    enum State {

        /**
         * State: locked.
         */
        LOCKED,

        /**
         * State: unlocked.
         */
        UNLOCKED,
    }
}
