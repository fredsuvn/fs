package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.function.BiConsumer;

/**
 * This interface represents a thread latch that can block or pass the threads. It has two states: latched and
 * unlatched. If it is latched, its {@link #await()} and {@link #await(Duration)} methods block the current thread until
 * the state becomes unlatched. The initialized state is latched.
 * <p>
 * {@link ThreadLatch} can send signals to itself by {@link #signal(Object)}. How to handle the signals is determined by
 * the specified signal consumer such as {@link #newLatch(BiConsumer)}.
 * <p>
 * {@link Waiter} interface is a narrowed interface of this interface, which only has portion of the methods of the
 * {@link ThreadLatch}. It is produced by {@link #waiter()}, and be shared the state with the {@link ThreadLatch}.
 *
 * @param <T> the type of the signal object
 * @author sunqian
 */
@ThreadSafe
public interface ThreadLatch<T> {

    /**
     * Returns a new {@link ThreadLatch}. The signal will be ignored.
     *
     * @param <T> the type of the signal object
     * @return a new {@link ThreadLatch}
     */
    static <T> @Nonnull ThreadLatch<T> newLatch() {
        return LatchBack.newLatch();
    }

    /**
     * Returns a new {@link ThreadLatch} with the specified signal consumer, the signal will be consumed by that
     * consumer. The first argument is the returned latch itself, and the second argument is the signal.
     * <p>
     * Note thread safety is not guaranteed for invoking of the consumer, the consumer need to handle thread safety by
     * itself.
     *
     * @param signalConsumer the specified signal consumer
     * @param <T>            the type of the signal object
     * @return a new {@link ThreadLatch}
     */
    static <T> @Nonnull ThreadLatch<T> newLatch(
        @Nonnull BiConsumer<ThreadLatch<T>, ? super @Nullable T> signalConsumer
    ) {
        return LatchBack.newLatch(signalConsumer);
    }

    /**
     * Blocks the current thread until the state becomes unlatched, unless the thread is interrupted.
     *
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    void await() throws AwaitingException;

    /**
     * Blocks the current thread until the state becomes unlatched, unless the thread is interrupted, or the specified
     * waiting time elapses. Returns {@code true} if the state become unlatched and {@code false} if the waiting time
     * elapsed.
     *
     * @param duration the maximum time to wait
     * @return {@code true} if the state become unlatched and {@code false} if the waiting time elapsed
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    boolean await(@Nonnull Duration duration) throws AwaitingException;

    /**
     * Returns the current state.
     *
     * @return the current state
     */
    @Nonnull
    State state();

    /**
     * Sends a signal to this latch.
     *
     * @param o the signal object
     */
    void signal(@Nullable T o);

    /**
     * Sets current state to latched.
     */
    void latch();

    /**
     * Sets current state to unlatched.
     */
    void unlatch();

    /**
     * Produces and returns a {@link Waiter} which is shared the state with this latch as host.
     *
     * @return a {@link Waiter} which is shared the state with this latch as host
     */
    @Nonnull
    Waiter<T> waiter();

    /**
     * This interface is a narrowed interface of the {@link ThreadLatch}, which only has waiting methods and signal
     * methods, without latch methods. A {@link Waiter} is produced from a {@link ThreadLatch} which as the host and
     * shares the state with the {@link Waiter}.
     *
     * @param <T> the type of the signal object
     */
    interface Waiter<T> {

        /**
         * Blocks the current thread until the state becomes unlatched, unless the thread is interrupted.
         *
         * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
         */
        void await() throws AwaitingException;

        /**
         * Blocks the current thread until the state becomes unlatched, unless the thread is interrupted, or the
         * specified waiting time elapses. Returns {@code true} if the state become unlatched and {@code false} if the
         * waiting time elapsed.
         *
         * @param duration the maximum time to wait
         * @return {@code true} if the state become unlatched and {@code false} if the waiting time elapsed
         * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
         */
        boolean await(@Nonnull Duration duration) throws AwaitingException;

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
        void signal(@Nullable T o);
    }

    /**
     * State of the {@link ThreadLatch}.
     */
    enum State {

        /**
         * State: latched.
         */
        LATCHED(1),

        /**
         * State: unlatched.
         */
        UNLATCHED(0),
        ;

        final int value;

        State(int value) {
            this.value = value;
        }
    }
}
