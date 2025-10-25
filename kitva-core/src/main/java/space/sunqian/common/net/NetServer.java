package space.sunqian.common.net;

import space.sunqian.annotations.Nonnull;

import java.util.List;

/**
 * This interface represents a network server.
 *
 * @param <A> the type of the address in the current network
 * @author sunqian
 */
public interface NetServer<A> extends Cloneable {

    /**
     * Blocks current thread until all threads of this server terminates, or interrupted via an
     * {@link InterruptedException}.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;

    /**
     * Closes this server. This method interrupts all thread of the server, and waits for all threads to terminate.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the address this server is bound to.
     * <p>
     * If the server is closed, it still returns the address at which the server was alive.
     *
     * @return the address this server is bound to
     */
    @Nonnull
    A localAddress();

    /**
     * Returns all current workers of this server.
     *
     * @return all current workers of this server
     */
    @Nonnull
    List<@Nonnull Worker> workers();

    /**
     * Returns whether this server is closed.
     *
     * @return whether this server is closed
     */
    boolean isClosed();

    /**
     * Represents the worker of the server. A worker typically is responsible for handling connected clients, with a
     * delicated thread.
     *
     * @author sunqian
     */
    interface Worker {

        /**
         * Returns the number of connected clients this worker handles.
         *
         * @return the number of connected clients this worker handles
         */
        int clientCount();

        /**
         * Returns the thread this worker runs on.
         *
         * @return the thread this worker runs on
         */
        @Nonnull
        Thread thread();
    }
}
