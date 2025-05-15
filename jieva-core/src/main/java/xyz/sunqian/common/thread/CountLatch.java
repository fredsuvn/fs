package xyz.sunqian.common.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;

/**
 * This is a specific class of {@link ThreadLatch} of which signal type is {@link Long}.
 * <p>
 * This class maintains a long counter. The value of the counter can be positive, negative, or zero. When the latch
 * receives a signal, the counter's value will become the current value plus the signal value. If, and only if the
 * counter's value equals to {@code 0}, the latch's state will become unlatched, otherwise latched. {@link #latch()} and
 * {@link #unlatch()} methods are invalid in this interface.
 * <p>
 * All methods and operations in this class, including consuming signals, are thread safety.
 *
 * @author sunqian
 */
@ThreadSafe
public interface CountLatch extends ThreadLatch<Long> {

    /**
     * Returns a new {@link CountLatch} with initial counter value of 1.
     *
     * @return a new {@link CountLatch} with initial counter value of 1
     */
    static @Nonnull CountLatch newLatch() {
        return newLatch(1);
    }

    /**
     * Returns a new {@link CountLatch} with the specified initial counter value.
     *
     * @param initialCount the specified initial counter value
     * @return a new {@link CountLatch} with the specified initial counter value
     */
    static @Nonnull CountLatch newLatch(long initialCount) {
        return LatchBack.newCountLatch(initialCount);
    }

    /**
     * Sends a long signal to this latch. If the signal is null, it is equivalent to {@code signal(0)}.
     *
     * @param o the long signal
     */
    @Override
    default void signal(@Nullable Long o) {
        signal(o == null ? 0 : o);
    }

    /**
     * Sends a long signal to this latch.
     *
     * @param i the long signal
     */
    void signal(long i);

    /**
     * Sends a {@code -1} to this latch, it is equivalent to {@code signal(-1)}.
     */
    default void countDown() {
        signal(-1);
    }

    /**
     * Sends a {@code 1} to this latch, it is equivalent to {@code signal(1)}.
     */
    default void countUp() {
        signal(1);
    }

    /**
     * Returns the current value of counter.
     *
     * @return the current value of counter
     */
    long count();

    /**
     * This method does nothing in {@link CountLatch}.
     */
    default void latch() {
        // Does nothing
    }

    /**
     * This method does nothing in {@link CountLatch}.
     */
    default void unlatch() {
        // Does nothing
    }

    /**
     * Produces and returns a {@link Waiter} which is shared the state with this latch as host.
     *
     * @return a {@link Waiter} which is shared the state with this latch as host
     */
    @Override
    @Nonnull
    Waiter waiter();

    /**
     * This interface is a narrowed interface of the {@link CountLatch}, which only has waiting methods and signal
     * methods, without latch methods. A {@link CountLatch.Waiter} is produced from a {@link CountLatch} which as the
     * host and shares the state with the {@link CountLatch.Waiter}.
     */
    interface Waiter extends ThreadLatch.Waiter<Long> {

        /**
         * Sends a long signal to the {@link CountLatch} where it was produced. If the signal is null, it is equivalent
         * to {@code signal(0)}.
         *
         * @param o the long signal
         */
        @Override
        void signal(@Nullable Long o);

        /**
         * Sends a long signal to the {@link CountLatch} where it was produced.
         *
         * @param i the long signal
         */
        void signal(long i);

        /**
         * Sends a {@code -1} to the {@link CountLatch} where it was produced, it is equivalent to {@code signal(-1)}.
         */
        void countDown();

        /**
         * Sends a {@code 1} to the {@link CountLatch} where it was produced, it is equivalent to {@code signal(1)}.
         */
        void countUp();
    }
}
