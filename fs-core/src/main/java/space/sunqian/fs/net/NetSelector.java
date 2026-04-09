package space.sunqian.fs.net;

import space.sunqian.annotation.Nonnull;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * A wrapper for {@link Selector} that provides protection against known {@link Selector} bugs, such as the epoll empty
 * poll bug on Linux systems.
 * <p>
 * The epoll empty poll bug is a known issue where {@link Selector#select()} may continuously return 0 without blocking,
 * causing high CPU usage. This interface provides a safe abstraction that automatically detects and mitigates such
 * issues by rebuilding the {@link Selector} when necessary.
 * <p>
 * Implementations of this interface should handle:
 * <ul>
 *   <li>Detection of empty poll cycles</li>
 *   <li>Automatic {@link Selector} rebuilding when thresholds are exceeded</li>
 *   <li>Migration of registered channels to the new {@link Selector}</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 * <p>
 * Note this interface does not guarantee thread safety because the underlying {@link Selector} may be replaced
 * internally. Callers must ensure that all operations on this interface are synchronized.
 *
 * @author sunqian
 * @see Selector
 */
public interface NetSelector {

    /**
     * The default empty select threshold.
     */
    int DEFAULT_EMPTY_SELECT_THRESHOLD = 512;

    /**
     * Opens and returns a new {@link NetSelector} instance with the default empty select threshold.
     *
     * @return a new {@link NetSelector} instance
     * @throws NetException if an I/O error occurs
     */
    static @Nonnull NetSelector open() throws NetException {
        return open(DEFAULT_EMPTY_SELECT_THRESHOLD);
    }

    /**
     * Opens and returns a new {@link NetSelector} instance with the specified empty select threshold.
     *
     * @param emptySelectThreshold the empty select threshold to use
     * @return a new {@link NetSelector} instance with the specified empty select threshold
     * @throws NetException if an I/O error occurs
     */
    static @Nonnull NetSelector open(int emptySelectThreshold) throws NetException {
        return new NetSelectorImpl(emptySelectThreshold);
    }

    /**
     * Returns the underlying {@link Selector} instance. This method always returns a valid {@link Selector} that is
     * safe to use for channel registration and selection operations.
     * <p>
     * The returned {@link Selector} may be replaced internally if the implementation detects issues such as the epoll
     * empty poll bug. Callers should cache the result of this method only for short periods and should call this method
     * again if they need to ensure they have the current {@link Selector} instance.
     *
     * @return the underlying {@link Selector} instance, never {@code null}
     */
    @Nonnull
    Selector selector();

    /**
     * Performs a selection operation that may block for up to the specified timeout. This method is equivalent to
     * calling {@link Selector#select(long)} on the underlying {@link Selector}.
     *
     * @param timeout the timeout in milliseconds; if positive, block for up to timeout milliseconds, more or less,
     *                while waiting for a channel to become ready; if zero, block indefinitely; must not be negative
     * @return the number of keys, possibly zero, whose ready-operation sets were updated
     * @throws IllegalArgumentException if the timeout is negative
     * @throws NetException             if an I/O error occurs
     */
    int select(long timeout) throws IllegalArgumentException, NetException;

    /**
     * Returns this selector's selected-key set. This method is equivalent to calling {@link Selector#selectedKeys()} on
     * the underlying {@link Selector}.
     *
     * @return the selected-key set, never {@code null}
     * @throws NetException if an I/O error occurs
     */
    @Nonnull
    Set<SelectionKey> selectedKeys() throws NetException;

    /**
     * Returns this selector's key set. This method is equivalent to calling {@link Selector#keys()} on the underlying
     * {@link Selector}.
     *
     * @return the key set, never {@code null}
     * @throws NetException if an I/O error occurs
     */
    @Nonnull
    Set<SelectionKey> keys() throws NetException;

    /**
     * Wakes up the selector if it is currently blocked in a selection operation. This method is equivalent to calling
     * {@link Selector#wakeup()} on the underlying {@link Selector}.
     */
    void wakeup();

    /**
     * Closes the selector and releases any resources associated with it. This method is equivalent to calling
     * {@link Selector#close()} on the underlying {@link Selector}.
     *
     * @throws NetException if an I/O error occurs
     */
    void close() throws NetException;

    /**
     * Cancels the selection key for the specified channel. This method is equivalent to calling
     * {@link SelectionKey#cancel()} on the underlying {@link SelectionKey}.
     *
     * @param channel the channel to cancel
     * @throws NetException if an I/O error occurs
     */
    void cancel(@Nonnull SelectableChannel channel) throws NetException;

    /**
     * Rebuilds the underlying {@link Selector} instance by creating a new {@link Selector} instance and migrating all
     * registered channels to it.
     * <p>
     * This method should be invoked automatically by the implementation when the empty select thresholds are exceeded.
     *
     * @throws NetException if an I/O error occurs
     */
    void rebuildSelector() throws NetException;
}